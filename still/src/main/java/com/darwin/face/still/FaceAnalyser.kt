package com.darwin.face.still

import android.graphics.Bitmap
import android.graphics.PointF
import com.darwin.face.still.model.CropAlgorithm
import com.darwin.face.still.model.FaceOptions
import com.darwin.face.still.model.FacePortrait
import com.darwin.face.still.model.FacePose
import com.google.android.gms.vision.face.Landmark
import com.google.mlkit.vision.face.Face
import java.util.*
import kotlin.math.sqrt

/**
 * The class FaceAnalyser
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
internal class FaceAnalyser {

    fun analyzeFaces(
        faces: List<Face>,
        bitmap: Bitmap,
        faceOptions: FaceOptions
    ): List<FacePortrait> {
        val facePortraits: List<FacePortrait> = if (faceOptions.prominentFaceDetection) {
            analyseProminentFace(faces, bitmap, faceOptions)
        } else {
            analyseMultipleFaces(faces, bitmap, faceOptions)
        }
        return Collections.unmodifiableList(facePortraits)
    }


    private fun analyseProminentFace(
        faces: List<Face>,
        bitmap: Bitmap,
        faceOptions: FaceOptions
    ): MutableList<FacePortrait> {
        Util.printLog("Prominent face analysis started.")
        val facePortraits: MutableList<FacePortrait> = mutableListOf()
        val portrait =
            processFace(
                getProminentFace(faces)!!,
                bitmap,
                faceOptions.cropAlgorithm,
                faceOptions.minimumFaceSize
            )
        if (portrait != null) {
            Util.printLog("Face crop succeeded with {CropAlgorithm.${faceOptions.cropAlgorithm})} algorithm.")
            facePortraits.add(portrait)
        }
        return facePortraits
    }

    private fun analyseMultipleFaces(
        faces: List<Face>,
        bitmap: Bitmap,
        faceOptions: FaceOptions
    ): MutableList<FacePortrait> {
        Util.printLog("Non prominent face analysis started.")
        val facePortraits: MutableList<FacePortrait> = mutableListOf()
        faces.forEach {
            val portrait =
                processFace(it, bitmap, faceOptions.cropAlgorithm, faceOptions.minimumFaceSize)
            if (portrait != null) {
                Util.printLog("Face crop succeeded with {CropAlgorithm.${faceOptions.cropAlgorithm}} algorithm.")
                facePortraits.add(portrait)
            }
            Util.printLog("---------- ---------- ---------- ----------")
        }
        return facePortraits
    }

    private fun processFace(
        face: Face,
        bitmap: Bitmap,
        cropAlgorithm: CropAlgorithm,
        minFaceSize: Int
    ): FacePortrait? {
        Util.printLog("Processing face with {CropAlgorithm.$cropAlgorithm} algorithm.")
        var faceSize = getFaceSizePercentage(face, bitmap, cropAlgorithm)
        if (faceSize >= minFaceSize) {
            val portraitData = cropFace(face, bitmap, cropAlgorithm)
            val croppedBitmap = portraitData.first
            val smileProbability = face.smilingProbability!!.roundFloat()
            val leftEyeOpenProbability = face.leftEyeOpenProbability!!.roundFloat()
            val rightEyeOpenProbability = face.rightEyeOpenProbability!!.roundFloat()
            val pixelBetweenEyes = portraitData.second.roundDouble()
            faceSize = getAreaOfFaceRelativeToBitmap(
                croppedBitmap.width.toFloat(),
                croppedBitmap.height.toFloat(),
                bitmap
            )
            val facePose = FacePose(
                eulerValueToAngle(face.headEulerAngleX),
                eulerValueToAngle(face.headEulerAngleY),
                eulerValueToAngle(face.headEulerAngleZ)
            )
            return FacePortrait(
                croppedBitmap,
                smileProbability,
                leftEyeOpenProbability,
                rightEyeOpenProbability,
                pixelBetweenEyes,
                faceSize,
                facePose
            )
        } else {
            Util.printLog("The face size{$faceSize} is below minimum threshold value{${minFaceSize}}, skipping face.")
            return null
        }
    }

    private fun cropFace(
        face: Face,
        bitmap: Bitmap,
        cropAlgorithm: CropAlgorithm
    ): Pair<Bitmap, Double> {
        Util.printLog("Face crop with {CropAlgorithm.$cropAlgorithm} algorithm is started.")
        if (cropAlgorithm == CropAlgorithm.LEAST) {
            return cropFaceByLeastAlgorithm(face, bitmap)
        } else {
            val eyeDistance = getPixelBetweenEyes(face)
            val width: Float
            val height: Float
            if (cropAlgorithm == CropAlgorithm.SQUARE) {
                val tempWidth = (eyeDistance / 0.30).toFloat()
                height = (tempWidth / 0.75).toFloat()
                width = height
            } else {
                width = (eyeDistance / 0.25).toFloat()
                height = (width / 0.75).toFloat()
            }

            val leftEyePosition: PointF = getLandmarkPosition(face, Landmark.LEFT_EYE)!!
            val rightEyePosition: PointF = getLandmarkPosition(face, Landmark.RIGHT_EYE)!!
            val eyeMidPoint: PointF =
                midpoint(
                    leftEyePosition.x,
                    rightEyePosition.x,
                    leftEyePosition.y,
                    rightEyePosition.y
                )

            var faceStartX = (eyeMidPoint.x - width / 2).toInt()
            faceStartX = faceStartX.coerceAtLeast(0)
            val upperHeightRatio = if (cropAlgorithm == CropAlgorithm.THREE_BY_FOUR) 0.6 else 0.5
            val faceUpperHeight = (upperHeightRatio * width).toInt() + 1
            var faceStartY = (eyeMidPoint.y - faceUpperHeight).toInt()
            faceStartY = faceStartY.coerceAtLeast(0)


            //finding image coordinates for final bitmap
            var finalStartX = faceStartX
            var finalWidth = width.toInt()
            var finalStartY = faceStartY
            var finalHeight = height.toInt()

            if (finalStartY + finalHeight > bitmap.height) {
                val excessHeight: Int = finalStartY + finalHeight - bitmap.height
                finalHeight -= excessHeight

                //if input image doesn't have require height to make cropped bitmap square, reducing result image width
                if (cropAlgorithm == CropAlgorithm.SQUARE) {
                    finalStartX += excessHeight
                    finalWidth -= excessHeight
                } else {
                    val newWidth = finalHeight * 0.75
                    finalWidth = newWidth.toInt()
                }
            }

            if (finalStartX + finalWidth > bitmap.width) {
                val excessWidth: Int = finalStartX + finalWidth - bitmap.width
                finalWidth -= excessWidth

                //if input image doesn't have require width to make cropped bitmap square, reducing result image height
                if (cropAlgorithm == CropAlgorithm.SQUARE) {
                    finalStartY += excessWidth
                    finalHeight -= excessWidth
                } else {
                    val newHeight = finalWidth / 0.75
                    val heightDiff = finalHeight - newHeight
                    finalStartY += (heightDiff / 2).toInt()
                    finalHeight = newHeight.toInt()
                }
            }


            //converting width,height to multiple of 8
            val widthRemainder = finalWidth % 8
            val heightRemainder = finalHeight % 8
            if (widthRemainder != 0) {
                finalWidth -= widthRemainder
            }
            if (heightRemainder != 0) {
                finalHeight -= heightRemainder
            }

            val croppedBitmap =
                Bitmap.createBitmap(bitmap, finalStartX, finalStartY, finalWidth, finalHeight)
            return Pair(croppedBitmap, eyeDistance)
        }
    }


    private fun cropFaceByLeastAlgorithm(
        face: Face,
        bitmap: Bitmap
    ): Pair<Bitmap, Double> {
        val eyeDistance = getPixelBetweenEyes(face)

        val heightTopOffset = (eyeDistance / 2).toInt()
        val heightBottomOffset = heightTopOffset / 2
        val startX = face.boundingBox.left
        var startY = face.boundingBox.top - heightTopOffset
        var width = face.boundingBox.width()
        var height = face.boundingBox.height() + heightTopOffset + heightBottomOffset
        if (startY < 0) {
            startY = 0
        }
        if ((startY + height) > bitmap.height) {
            height = bitmap.height - startY
        }

        //converting width,height to multiple of 8
        val widthRemainder = width % 8
        val heightRemainder = height % 8
        if (widthRemainder != 0) {
            width -= widthRemainder
        }
        if (heightRemainder != 0) {
            height -= heightRemainder
        }

        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            startX,
            startY,
            width,
            height
        )
        return Pair(croppedBitmap, eyeDistance)
    }

    private fun getFaceSizePercentage(
        face: Face,
        bitmap: Bitmap,
        cropAlgorithm: CropAlgorithm
    ): Float {
        val eyeDistance = getPixelBetweenEyes(face)
        val width: Float
        val height: Float
        when (cropAlgorithm) {
            CropAlgorithm.SQUARE -> {
                val tempWidth = (eyeDistance / 0.25).toFloat()
                height = (tempWidth / 0.75).toFloat()
                width = height
            }
            CropAlgorithm.THREE_BY_FOUR -> {
                width = (eyeDistance / 0.25).toFloat()
                height = (width / 0.75).toFloat()
            }
            CropAlgorithm.LEAST -> {
                val heightTopOffset = (face.boundingBox.height() * 40) / 100
                val heightBottomOffset = heightTopOffset / 3
                var startY = face.boundingBox.top - heightTopOffset
                val tempHeight = face.boundingBox.height() + heightTopOffset + heightBottomOffset
                if (startY < 0) {
                    startY = 0
                }
                height = if ((startY + tempHeight) > bitmap.height) {
                    (bitmap.height - startY).toFloat()
                } else {
                    tempHeight.toFloat()
                }
                width = face.boundingBox.width().toFloat()
            }
        }

        return getAreaOfFaceRelativeToBitmap(width, height, bitmap)
    }

    private fun getAreaOfFaceRelativeToBitmap(width: Float, height: Float, bitmap: Bitmap): Float {
        val areaOfFace = width * height
        val areaOfInputImage = bitmap.width * bitmap.height
        return (areaOfFace * 100) / areaOfInputImage
    }

    private fun getPixelBetweenEyes(face: Face): Double {
        val leftEyePosition: PointF = getLandmarkPosition(face, Landmark.LEFT_EYE)!!
        val rightEyePosition: PointF = getLandmarkPosition(face, Landmark.RIGHT_EYE)!!
        return calculateDistanceBetweenPoints(
            leftEyePosition.x.toDouble(),
            leftEyePosition.y.toDouble(),
            rightEyePosition.x.toDouble(),
            rightEyePosition.y.toDouble()
        )
    }

    private fun getProminentFace(faces: List<Face>): Face? {
        return if (faces.size == 1) {
            faces[0]
        } else {
            var prominentFace: Face? = null
            var prominentEyeDistance = 0.0
            faces.forEach {
                val eyeDistance = getPixelBetweenEyes(it)
                if (eyeDistance > prominentEyeDistance) {
                    prominentEyeDistance = eyeDistance
                    prominentFace = it
                }
            }
            prominentFace
        }
    }

    private fun getLandmarkPosition(
        face: Face,
        landmarkId: Int
    ): PointF? {
        return face.getLandmark(landmarkId)?.position
    }

    private fun calculateDistanceBetweenPoints(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): Double {
        return sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))
    }

    private fun midpoint(
        x1: Float, x2: Float,
        y1: Float, y2: Float
    ): PointF {
        return PointF((x1 + x2) / 2, (y1 + y2) / 2)
    }

    private fun eulerValueToAngle(value: Float): Float = if (value < 0) {
        value + 360
    } else value

}