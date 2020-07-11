package com.darwin.face.still.detector

import android.graphics.Bitmap

/**
 * An interface to process the images with vision face detector.
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
internal interface VisionImageProcessor {

    /**
     * Processes a bitmap image.
     */
    fun processBitmap(bitmap: Bitmap)


    /**
     * Stops the underlying machine learning model and release resources.
     */
    fun stop()
}