package com.darwin.viola.still.model

import android.graphics.Bitmap

/**
 * The class FacePortrait
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
data class FacePortrait(
    val face: Bitmap,
    val smileProbability: Float,
    val leftEyeOpenProbability: Float,
    val rightEyeOpenProbability: Float,
    val pixelBetweenEyes: Double,
    val faceSizePercentage: Float,
    val facePose: FacePose
) {

    override fun toString(): String {
        return "FacePortrait(face=$face, smileProbability=$smileProbability, leftEyeOpenProbability=$leftEyeOpenProbability, rightEyeOpenProbability=$rightEyeOpenProbability, pixelBetweenEyes=$pixelBetweenEyes, faceSizePercentage=$faceSizePercentage, facePose=$facePose)"
    }
}