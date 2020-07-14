package com.darwin.facedetector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.darwin.viola.still.FaceDetectionListener;
import com.darwin.viola.still.Viola;
import com.darwin.viola.still.model.CropAlgorithm;
import com.darwin.viola.still.model.FaceDetectionError;
import com.darwin.viola.still.model.FaceOptions;
import com.darwin.viola.still.model.Result;

import org.jetbrains.annotations.NotNull;

/**
 * The class FaceCropSampleJavaActivity
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 13 Jul 2020
 */
public class FaceCropSampleJavaActivity extends AppCompatActivity {

    private Button btCrop;
    private RadioGroup radioAlgorithm;
    private ImageView ivInputImage;
    private TextView tvErrorMessage;

    private Viola viola;
    private FacePhotoAdapter facePhotoAdapter = new FacePhotoAdapter();
    private Bitmap bitmap;
    private CropAlgorithm cropAlgorithm = CropAlgorithm.THREE_BY_FOUR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_crop_sample);
        initializeUI();
        setEventListeners();
        prepareFaceCropper();
    }

    private void initializeUI() {
        RecyclerView rvCroppedImages = findViewById(R.id.rvCroppedImages);
        btCrop = findViewById(R.id.btCrop);
        radioAlgorithm = findViewById(R.id.radio_algorithm);
        ivInputImage = findViewById(R.id.iv_input_image);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        StaggeredGridLayoutManager staggeredLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        staggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvCroppedImages.setLayoutManager(staggeredLayoutManager);
        rvCroppedImages.setAdapter(facePhotoAdapter);
    }

    private void setEventListeners() {
        btCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonID = radioAlgorithm.getCheckedRadioButtonId();
                View radioButton = radioAlgorithm.findViewById(radioButtonID);
                int algorithmIndex = radioAlgorithm.indexOfChild(radioButton);
                cropAlgorithm = getAlgorithmByIndex(algorithmIndex);
                crop();
            }
        });
    }

    private CropAlgorithm getAlgorithmByIndex(int index) {
        CropAlgorithm algorithm;
        switch (index) {
            case 1:
                algorithm = CropAlgorithm.SQUARE;
                break;
            case 2:
                algorithm = CropAlgorithm.LEAST;
                break;
            default:
                algorithm = CropAlgorithm.THREE_BY_FOUR;
        }
        return algorithm;
    }

    private void prepareFaceCropper() {
        viola = new Viola(listener);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.multi_face, options);
        ivInputImage.setImageBitmap(bitmap);
    }

    private void crop() {
        FaceOptions faceOption = new
                FaceOptions.Builder()
                .cropAlgorithm(cropAlgorithm)
                .setMinimumFaceSize(6)
                .enableDebug()
                .build();
        viola.detectFace(bitmap, faceOption);
    }

    private final FaceDetectionListener listener = new FaceDetectionListener() {
        @Override
        public void onFaceDetected(@NotNull Result result) {
            facePhotoAdapter.bindData(result.getFacePortraits());
        }

        @Override
        public void onFaceDetectionFailed(@NotNull FaceDetectionError error, @NotNull String message) {
            tvErrorMessage.setText(message);
        }
    };
}
