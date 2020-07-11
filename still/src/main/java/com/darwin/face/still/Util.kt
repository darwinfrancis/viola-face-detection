package com.darwin.face.still

import android.util.Log

/**
 * The class Util
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 11 Jul 2020
 */
internal class Util {
    companion object {
        var debug: Boolean = false
        fun printLog(message: String) {
            if (debug)
                Log.d("FaceDetector", message)
        }
    }
}