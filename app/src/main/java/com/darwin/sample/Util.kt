package com.darwin.sample

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.IOException
import java.net.URISyntaxException

/**
 * The class Util
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 16 Jul 2020
 */
class Util {
    companion object {

        @Throws(URISyntaxException::class)
        fun getPath(context: Context, imageUri: Uri): String? {
            var uri = imageUri
            val needToCheckUri = Build.VERSION.SDK_INT >= 19
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            // deal with different Uris.
            if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    when (split[0]) {
                        "image" -> {
                            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection =
                    arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val columnIndex =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex)
                    }
                } catch (e: Exception) {
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

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