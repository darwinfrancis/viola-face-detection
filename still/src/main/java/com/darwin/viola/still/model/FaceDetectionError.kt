package com.darwin.viola.still.model

/**
 * The class FaceDetectionError
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 10 Jul 2020
 */
enum class FaceDetectionError(val message: String) {
    LOW_RESOLUTION("Provide an image with dimensions of at least 480x360 pixels."),
    NO_FACE_DETECTED("There is no face portraits in the given image."), NO_VALID_FACE_DETECTED("There is no face with the given threshold,try changing {FaceOption.setMinimumFaceSize}."), ERROR(
        ""
    )
}