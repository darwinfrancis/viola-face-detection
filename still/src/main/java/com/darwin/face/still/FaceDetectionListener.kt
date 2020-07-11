package com.darwin.face.still

import com.darwin.face.still.model.FaceDetectionError
import com.darwin.face.still.model.Result

/**
 * The class FaceDetectionListener
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
interface FaceDetectionListener {

    fun onFaceDetected(result: Result)
    fun onFaceDetectionFailed(error: FaceDetectionError, message: String)
}