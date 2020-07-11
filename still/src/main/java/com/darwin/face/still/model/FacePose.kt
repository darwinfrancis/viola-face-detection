package com.darwin.face.still.model

/**
 * The class FacePose
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
data class FacePose(val eulerX: Float, val eulerY: Float, val eulerZ: Float) {
    override fun toString(): String {
        return "FacePose(eulerX=$eulerX, eulerY=$eulerY, eulerZ=$eulerZ)"
    }
}