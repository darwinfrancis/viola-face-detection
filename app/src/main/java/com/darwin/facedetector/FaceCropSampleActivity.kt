package com.darwin.facedetector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.darwin.viola.still.FaceDetectionListener
import com.darwin.viola.still.Viola
import com.darwin.viola.still.model.CropAlgorithm
import com.darwin.viola.still.model.FaceDetectionError
import com.darwin.viola.still.model.FaceOptions
import com.darwin.viola.still.model.Result
import kotlinx.android.synthetic.main.activity_face_crop_sample.*

class FaceCropSampleActivity : AppCompatActivity() {

    private lateinit var viola: Viola
    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private val faceListAdapter = FacePhotoAdapter()
    private var bitmap: Bitmap? = null
    private var cropAlgorithm = CropAlgorithm.THREE_BY_FOUR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_crop_sample)
        initializeUI()
        setEventListeners()
        prepareFaceCropper()
    }

    private fun initializeUI() {
        staggeredLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rvCroppedImages.layoutManager = staggeredLayoutManager
        rvCroppedImages.adapter = faceListAdapter
    }

    private fun setEventListeners() {
        btCrop.setOnClickListener {
            val radioButtonID: Int = radio_algorithm.checkedRadioButtonId
            val radioButton: View = radio_algorithm.findViewById(radioButtonID)
            val algorithmIndex: Int = radio_algorithm.indexOfChild(radioButton)
            cropAlgorithm = getAlgorithmByIndex(algorithmIndex)
            crop()
        }
    }

    private fun getAlgorithmByIndex(index: Int): CropAlgorithm = when (index) {
        0 -> CropAlgorithm.THREE_BY_FOUR
        1 -> CropAlgorithm.SQUARE
        2 -> CropAlgorithm.LEAST
        else -> CropAlgorithm.THREE_BY_FOUR
    }

    private fun prepareFaceCropper() {
        viola = Viola(listener)
        val options = BitmapFactory.Options()
        options.inScaled = false
        bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.multi_face, options
        )
        iv_input_image.setImageBitmap(bitmap)
    }

    private fun crop() {
        val faceOption =
            FaceOptions.Builder()
                .cropAlgorithm(cropAlgorithm)
                .setMinimumFaceSize(6)
                .enableDebug()
                .build()
        viola.detectFace(bitmap!!, faceOption)
    }

    private val listener: FaceDetectionListener = object : FaceDetectionListener {

        override fun onFaceDetected(result: Result) {
            faceListAdapter.bindData(result.facePortraits)
        }

        override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
            tvErrorMessage.text = message
        }
    }

}
