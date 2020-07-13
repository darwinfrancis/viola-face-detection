package com.darwin.facedetector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.darwin.face.still.FaceDetectionListener
import com.darwin.face.still.FaceDetector
import com.darwin.face.still.model.CropAlgorithm
import com.darwin.face.still.model.FaceDetectionError
import com.darwin.face.still.model.FaceOptions
import com.darwin.face.still.model.Result
import kotlinx.android.synthetic.main.activity_face_crop_sample.*

class FaceCropSampleActivity : AppCompatActivity() {

    private lateinit var faceDetector: FaceDetector
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private val faceListAdapter = FacePhotoAdapter()
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_crop_sample)
        initializeUI()
        setEventListeners()
        prepareFaceCropper()
    }

    private fun initializeUI() {
        staggeredLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE;
        rvCroppedImages.layoutManager = staggeredLayoutManager
        rvCroppedImages.adapter = faceListAdapter
    }

    private fun setEventListeners() {
        btCrop.setOnClickListener {
            crop()
        }
    }

    private fun prepareFaceCropper() {
        faceDetector = FaceDetector(listener)
        val options = BitmapFactory.Options()
        options.inScaled = false
        bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.double_face, options
        )
        iv_input_image.setImageBitmap(bitmap)
    }

    private fun crop() {
        val faceOption =
            FaceOptions.Builder()
                .cropAlgorithm(CropAlgorithm.THREE_BY_FOUR)
                .setMinimumFaceSize(6)
                .enableDebug()
                .build()
        faceDetector.detectFace(bitmap!!, faceOption)
    }

    private val listener: FaceDetectionListener = object : FaceDetectionListener {

        override fun onFaceDetected(result: Result) {
            faceListAdapter.bindData(result.facePortraits)
            result.facePortraits.forEach {
                val ratio : Float = it.face.width.toFloat() / it.face.height.toFloat()
                Log.e("Ratio", "Ratio : $ratio")
            }
        }


        override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
        }
    }

}
