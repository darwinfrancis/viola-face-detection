package com.darwin.facedetector

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.darwin.face.still.FaceDetectionListener
import com.darwin.face.still.FaceDetector
import com.darwin.face.still.model.CropAlgorithm
import com.darwin.face.still.model.FaceDetectionError
import com.darwin.face.still.model.FaceOptions
import com.darwin.face.still.model.Result
import kotlinx.android.synthetic.main.activity_still_image_sample.*
import java.io.File
import java.io.FileInputStream


class StillImageSampleActivity : AppCompatActivity() {

    private lateinit var faceDetector: FaceDetector
    private var imageList: MutableList<Bitmap> = mutableListOf()
    var faceIndex = 0
    var cAlgorithm: CropAlgorithm = CropAlgorithm.THREE_BY_FOUR
    var algorithmIndex = 0
    private val algorithmList =
        arrayOf(CropAlgorithm.THREE_BY_FOUR, CropAlgorithm.SQUARE, CropAlgorithm.LEAST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_still_image_sample)
        faceDetector = FaceDetector(listener)

        setEventListener()
        listImagesFromFolder()
    }


    private fun setEventListener() {
        bt_crop_algorithm.text = cAlgorithm.name
        bt_detect.setOnClickListener {
            detectFaces()
        }
        bt_crop_algorithm.setOnClickListener {
            if (algorithmIndex < 2) {
                algorithmIndex++
                cAlgorithm = algorithmList[algorithmIndex]
                bt_crop_algorithm.text = cAlgorithm.name
            } else {
                algorithmIndex = 0
            }
        }
    }

    private fun listImagesFromFolder() {
        val path: String =
            Environment.getExternalStorageDirectory().toString() + "/FImages"
        val directory = File(path)
        val files: Array<File> = directory.listFiles()
        for (i in files.indices) {
            try {
                val f = File(files[i].name)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap =
                    BitmapFactory.decodeStream(FileInputStream("$path/$f"), null, options)
                imageList.add(bitmap!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun detectFaces() {
        /*val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.double_face, options
        )*/
        val faceOption =
            FaceOptions.Builder()
                .cropAlgorithm(cAlgorithm)
                .setMinimumFaceSize(6)
                .enableDebug()
                .build()
        if (faceIndex < imageList.size) {
            val bitmap = imageList[faceIndex]
            iv_org_image.setImageBitmap(bitmap)
            faceDetector.detectFace(bitmap, faceOption)
            faceIndex++
        } else {
            faceIndex = 0
        }
    }

    private val listener: FaceDetectionListener = object : FaceDetectionListener {

        override fun onFaceDetected(result: Result) {
            result.faceCount
            ll_images.removeAllViews()
            result.facePortraits.forEach() {
                val imageView = ImageView(this@StillImageSampleActivity)
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                imageView.layoutParams = params
                ll_images.addView(imageView)
                imageView.setImageBitmap(it.face)
                printLog(it.toString())
            }
        }


        override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
            printLog(error.name + " : " + message)
        }
    }


    private fun printLog(message: String) {
        Log.e("FACE LOG", message)
    }
}
