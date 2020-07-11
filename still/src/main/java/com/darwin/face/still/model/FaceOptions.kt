package com.darwin.face.still.model

import android.os.Debug

/**
 * The class FaceOptions
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
class FaceOptions private constructor(
    internal val prominentFaceDetection: Boolean = false,
    internal val cropAlgorithm: CropAlgorithm = CropAlgorithm.THREE_BY_FOUR,
    internal val minFaceSize: Int = 15,
    internal val debug: Boolean = false
) {

    data class Builder(
        private var prominentFaceDetection: Boolean = false,
        private var cropAlgorithm: CropAlgorithm = CropAlgorithm.THREE_BY_FOUR,
        private var minimumFaceSize: Int = 15,
        private var debug: Boolean = false
    ) {

        fun enableProminentFaceDetection() = apply { this.prominentFaceDetection = true }
        fun cropAlgorithm(algorithm: CropAlgorithm) = apply { this.cropAlgorithm = algorithm }
        fun setMinimumFaceSize(faceSize: Int) = apply { this.minimumFaceSize = faceSize }
        fun enableDebug() = apply { this.debug = true }
        fun build() = FaceOptions(prominentFaceDetection, cropAlgorithm, minimumFaceSize,debug)
    }

}