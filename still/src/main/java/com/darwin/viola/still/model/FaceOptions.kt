package com.darwin.viola.still.model

/**
 * The class FaceOptions
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
class FaceOptions private constructor(builder: Builder) {

    val prominentFaceDetection: Boolean
    val cropAlgorithm: CropAlgorithm
    val minimumFaceSize: Int
    val ageClassification: Boolean
    val debug: Boolean

    init {
        this.prominentFaceDetection = builder.prominentFaceDetection
        this.cropAlgorithm = builder.cropAlgorithm
        this.minimumFaceSize = builder.minimumFaceSize
        this.ageClassification = builder.ageClassification
        this.debug = builder.debug
    }

    class Builder {
        var prominentFaceDetection: Boolean = false
            private set
        var cropAlgorithm: CropAlgorithm = CropAlgorithm.THREE_BY_FOUR
            private set
        var minimumFaceSize: Int = 15
            private set
        var ageClassification: Boolean = false
            private set
        var debug: Boolean = false
            private set

        fun enableProminentFaceDetection() = apply { this.prominentFaceDetection = true }
        fun cropAlgorithm(algorithm: CropAlgorithm) = apply { this.cropAlgorithm = algorithm }
        fun setMinimumFaceSize(faceSize: Int) = apply { this.minimumFaceSize = faceSize }
        fun enableAgeClassification() = apply { this.ageClassification = true }
        fun enableDebug() = apply { this.debug = true }
        fun build() = FaceOptions(this)
    }

}