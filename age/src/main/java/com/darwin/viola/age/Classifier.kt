package com.darwin.viola.age

import android.content.Context
import android.graphics.Bitmap
import android.os.Trace
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.util.*

/**
 * The class Classifier
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 16 Mar 2021
 */
@Suppress("LeakingThis")
internal abstract class Classifier protected constructor(
    context: Context,
    device: Device,
    numThreads: Int
) {

    internal enum class Model {
        QUANTIZED_MOBILE_NET
    }

    internal enum class Device {
        CPU
    }


    private val imageSizeX: Int
    private val imageSizeY: Int
    private var nnApiDelegate: NnApiDelegate? = null
    private var tflite: Interpreter?
    private val tfliteOptions = Interpreter.Options()
    private val labels: List<String>
    private var inputImageBuffer: TensorImage
    private val outputProbabilityBuffer: TensorBuffer
    private val probabilityProcessor: TensorProcessor

    init {
        val modelPath = getModelPath()!!
        val tfliteModel = FileUtil.loadMappedFile(
            context,
            modelPath
        )
        when (device) {
            Device.CPU -> tfliteOptions.setUseXNNPACK(true)
        }
        tfliteOptions.setNumThreads(numThreads)
        tflite = Interpreter(tfliteModel, tfliteOptions)

        // Loads labels out from the label file.
        val labelPath = getLabelPath()!!
        labels = FileUtil.loadLabels(context, labelPath)

        // Reads type and shape of input and output tensors, respectively.
        val imageTensorIndex = 0
        val imageShape = tflite!!.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
        imageSizeY = imageShape[1]
        imageSizeX = imageShape[2]
        val imageDataType = tflite!!.getInputTensor(imageTensorIndex).dataType()
        val probabilityTensorIndex = 0
        val probabilityShape =
            tflite!!.getOutputTensor(probabilityTensorIndex).shape() // {1, NUM_CLASSES}
        val probabilityDataType = tflite!!.getOutputTensor(probabilityTensorIndex).dataType()

        // Creates the input tensor.
        inputImageBuffer = TensorImage(imageDataType)

        // Creates the output tensor and its processor.
        outputProbabilityBuffer =
            TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)

        // Creates the post processor for the output probability.
        val postProcessNormalizeOp = getPostProcessNormalizeOp()
        probabilityProcessor = TensorProcessor.Builder().add(postProcessNormalizeOp).build()
        Util.printLog("Created a Tensor flow Lite Age Classifier.")
    }

    private fun loadImage(bitmap: Bitmap, sensorOrientation: Int): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = bitmap.width.coerceAtMost(bitmap.height)
        val numRotation = sensorOrientation / 90
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeWithCropOrPadOp(
                    cropSize,
                    cropSize
                )
            ) // TODO(b/169379396): investigate the impact of the resize algorithm on accuracy.
            // To get the same inference results as lib_task_api, which is built on top of the Task
            // Library, use ResizeMethod.BILINEAR.
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(numRotation))
            .add(getPreProcessNormalizeOp())
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    fun recognizeImage(bitmap: Bitmap, sensorOrientation: Int): List<AgeRecognition> {
        Trace.beginSection("recognizeImage")
        Trace.beginSection("loadImage")
        inputImageBuffer = loadImage(bitmap, sensorOrientation)
        Trace.endSection()

        // Runs the inference call.
        Trace.beginSection("runInference")
        tflite!!.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())
        Trace.endSection()

        // Gets the map of label and probability.
        val labeledProbability =
            TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                .mapWithFloatValue
        Trace.endSection()

        // Gets top-k results.
        return getTopKProbability(labeledProbability)
    }

    fun close() {
        if (tflite != null) {
            tflite!!.close()
            tflite = null
        }
        if (nnApiDelegate != null) {
            nnApiDelegate!!.close()
            nnApiDelegate = null
        }
    }


    companion object {
        private const val MAX_RESULTS = 3

        @Throws(IOException::class)
        fun create(
            context: Context, model: Model, device: Device, numThreads: Int
        ): Classifier {
            return if (model == Model.QUANTIZED_MOBILE_NET) {
                AgeClassifierQuantizedMobileNet(context, device, numThreads)
            } else {
                throw UnsupportedOperationException()
            }
        }

        private fun getTopKProbability(labelProb: Map<String, Float>): List<AgeRecognition> {
            // Find the best classifications.
            val pq = PriorityQueue<AgeRecognition>(
                MAX_RESULTS
            ) { lhs, rhs -> // Intentionally reversed to put high confidence at the head of the queue.
                (rhs.confidence).compareTo(lhs.confidence)
            }
            for ((key, value) in labelProb) {
                pq.add(AgeRecognition(key, value))
            }
            val recognitions = ArrayList<AgeRecognition>()
            val recognitionsSize = pq.size.coerceAtMost(MAX_RESULTS)
            for (i in 0 until recognitionsSize) {
                val poll = pq.poll()
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                recognitions.add(poll)
            }
            return recognitions
        }
    }


    protected abstract fun getModelPath(): String?

    protected abstract fun getLabelPath(): String?

    protected abstract fun getPreProcessNormalizeOp(): TensorOperator?

    protected abstract fun getPostProcessNormalizeOp(): TensorOperator?

}