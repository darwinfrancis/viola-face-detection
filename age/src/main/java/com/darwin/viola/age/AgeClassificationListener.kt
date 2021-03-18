package com.darwin.viola.age

/**
 * The class AgeClassificationListener
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 16 Mar 2021
 */
interface AgeClassificationListener {
    fun onAgeClassificationResult(result: List<AgeRecognition>)
    fun onAgeClassificationError(error:String)
}