package com.darwin.viola.age

import android.util.Log

/**
 * Utility class to manage helper functions
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
                Log.d("Viola-Age", message)
        }
    }
}