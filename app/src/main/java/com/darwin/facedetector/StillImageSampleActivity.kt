package com.darwin.facedetector

import android.graphics.BitmapFactory
import android.os.Bundle
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


class StillImageSampleActivity : AppCompatActivity() {

    private lateinit var faceDetector: FaceDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_still_image_sample)
        faceDetector = FaceDetector(listener)

        setEventListener()

    }


    private fun setEventListener() {
        bt_detect.setOnClickListener {
            detectFaces()
        }
    }


    private fun detectFaces() {
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.double_face, options
        )
        val faceOption =
            FaceOptions.Builder()
                .cropAlgorithm(CropAlgorithm.THREE_BY_FOUR)
                .setMinimumFaceSize(15)
                .enableDebug()
                .build()
        faceDetector.detectFace(bitmap,faceOption)
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
