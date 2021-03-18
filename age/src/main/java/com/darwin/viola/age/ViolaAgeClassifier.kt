package com.darwin.viola.age

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.IllegalStateException
import kotlin.coroutines.coroutineContext

/**
 * The class ViolaAgeClassifier
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 16 Mar 2021
 */
class ViolaAgeClassifier(private val listener: AgeClassificationListener) {

    private lateinit var classifier: Classifier
    var isInitialized = false
        private set
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        Util.debug = true
    }

    fun initialize(context: Context) {
        if (!isInitialized) {
            Util.printLog("Initializing Viola age classifier.")
            val model = Classifier.Model.QUANTIZED_MOBILE_NET
            val device = Classifier.Device.CPU
            try {
                classifier = Classifier.create(context, model, device, 1)
                isInitialized = true
            } catch (e: IOException) {
                val error =
                    "Failed to create age classifier: ${e.javaClass.canonicalName}(${e.message})"
                Util.printLog(error)
                listener.onAgeClassificationError(error)
            }
        }
    }


    fun dispose() {
        Util.printLog("Disposing age classifier and its resources.")
        isInitialized = false
        classifier.close()
    }

    fun findAgeAsync(faceBitmap: Bitmap) {
        if (isInitialized) {
            Util.printLog("Processing face bitmap for age classification.")
            coroutineScope.launch {
                val results: List<AgeRecognition> =
                    classifier.recognizeImage(faceBitmap, 0)
                Util.printLog("Age classification completed, sending back the result.")
                withContext(Dispatchers.Main) { listener.onAgeClassificationResult(results) }
            }
        } else {
            Util.printLog("Viola age classifier is not initialized.")
            listener.onAgeClassificationError("Viola age classifier is not initialized.")
        }
    }


    @Throws(IllegalStateException::class)
    fun findAgeSynchronized(faceBitmap: Bitmap): List<AgeRecognition> {
        if (isInitialized) {
            Util.printLog("Processing face bitmap in synchronized manner for age classification.")
            return classifier.recognizeImage(faceBitmap, 0)
        } else {
            Util.printLog("Viola age classifier is not initialized. Throwing exception.")
            throw IllegalStateException("Viola age classifier is not initialized.")
        }
    }
}