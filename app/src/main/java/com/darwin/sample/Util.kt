package com.darwin.sample

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException

/**
 * The class Util
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 16 Jul 2020
 */
class Util {
    companion object {

        @Throws(IOException::class)
        fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
            val ei = ExifInterface(image_absolute_path)
            val orientation: Int =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
                    bitmap,
                    horizontal = true,
                    vertical = false
                )
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
                    bitmap,
                    horizontal = false,
                    vertical = true
                )
                else -> bitmap
            }
        }

        private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.preScale(
                if (horizontal) (-1).toFloat() else 1.toFloat(),
                if (vertical) (-1).toFloat() else 1.toFloat()
            )
            return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }

        private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }
}