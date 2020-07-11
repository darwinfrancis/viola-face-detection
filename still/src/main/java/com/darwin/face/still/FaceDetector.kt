package com.darwin.face.still

import android.graphics.Bitmap
import com.darwin.face.still.detector.FaceDetectorProcessor
import com.darwin.face.still.detector.VisionImageProcessor
import com.darwin.face.still.model.FaceDetectionError
import com.darwin.face.still.model.FaceOptions
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * The class FaceDetector
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
private const val processingBitmapWidth = 720
private const val processingBitmapHeight = 1280

class FaceDetector(private val faceDetectionListener: FaceDetectionListener) {

    private val imageProcessor: VisionImageProcessor

    init {
        val options = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        imageProcessor = FaceDetectorProcessor(options, faceDetectionListener)
    }


    fun detectFace(image: Bitmap, option: FaceOptions = getDefaultFaceOptions()) {
        Util.debug = option.debug
        if (isValidInputImage(image)) {
            Util.printLog("The given input bitmap is valid, ready for image processing.")
            (imageProcessor as FaceDetectorProcessor).faceOptions = option
            imageProcessor.processBitmap(resize(image))
        } else {
            Util.printLog("The given input bitmap is not valid, terminating image processing.")
            faceDetectionListener.onFaceDetectionFailed(
                FaceDetectionError.LOW_RESOLUTION, FaceDetectionError.LOW_RESOLUTION.message
            )
        }
    }

    private fun getDefaultFaceOptions(): FaceOptions {
        return FaceOptions.Builder()
            .setMinimumFaceSize(15)
            .build()
    }

    private fun isValidInputImage(image: Bitmap): Boolean {
        val isLandscape = image.width > image.height
        if (isLandscape && (image.width < 480 || image.height < 360)) {
            return false
        } else if (!isLandscape && (image.width < 360 || image.height < 480)) {
            return false
        }
        return true
    }

    private fun resize(image: Bitmap): Bitmap {
        Util.printLog("Re-scaling input bitmap for fast image processing.")
        val maxWidth = processingBitmapWidth
        val maxHeight = processingBitmapHeight
        val width = image.width
        val height = image.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
    }

}