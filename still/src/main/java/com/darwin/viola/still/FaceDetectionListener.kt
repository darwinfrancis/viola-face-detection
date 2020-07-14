package com.darwin.viola.still

import com.darwin.viola.still.model.FaceDetectionError
import com.darwin.viola.still.model.Result

/**
 * The listener used to send callback events to client.
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
interface FaceDetectionListener {

    fun onFaceDetected(result: Result)
    fun onFaceDetectionFailed(error: FaceDetectionError, message: String)
}