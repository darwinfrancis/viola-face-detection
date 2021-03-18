package com.darwin.sample;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.darwin.viola.still.FaceDetectionListener;
import com.darwin.viola.still.Viola;
import com.darwin.viola.still.model.CropAlgorithm;
import com.darwin.viola.still.model.FaceDetectionError;
import com.darwin.viola.still.model.FaceOptions;
import com.darwin.viola.still.model.FacePortrait;
import com.darwin.viola.still.model.Result;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class VoilaSampleJavaActivity
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 13 Jul 2020
 */
public class ViolaSampleJavaActivity extends AppCompatActivity {

    private Button btCrop, btImage;
    private RadioGroup radioAlgorithm;
    private ImageView ivInputImage;
    private TextView tvErrorMessage;

    private Viola viola;
    private FacePhotoAdapter facePhotoAdapter = new FacePhotoAdapter();
    private Bitmap bitmap;
    private CropAlgorithm cropAlgorithm = CropAlgorithm.THREE_BY_FOUR;
    private PermissionHelper permissionHelper;

    private final int imagePickerIntentId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viola_sample);
        permissionHelper = new PermissionHelper(this);
        initializeUI();
        setEventListeners();
        prepareFaceCropper();
    }

    @Override
    public void onResume() {
        super.onResume();
        permissionHelper.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionHelper.onDestroy();
    }

    private void initializeUI() {
        RecyclerView rvCroppedImages = findViewById(R.id.rvCroppedImages);
        btCrop = findViewById(R.id.btCrop);
        btImage = findViewById(R.id.btImage);
        radioAlgorithm = findViewById(R.id.radio_algorithm);
        ivInputImage = findViewById(R.id.iv_input_image);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        StaggeredGridLayoutManager staggeredLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        staggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvCroppedImages.setLayoutManager(staggeredLayoutManager);
        rvCroppedImages.setAdapter(facePhotoAdapter);
    }

    private void setEventListeners() {
        btImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });
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

    private void requestStoragePermission() {
        permissionHelper.setListener(permissionsListener);
        String[] requiredPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        permissionHelper.requestPermission(requiredPermissions, 100);
    }

    private void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, imagePickerIntentId);
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
        viola.addAgeClassificationPlugin(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.po_single, options);
        ivInputImage.setImageBitmap(bitmap);
    }

    private void crop() {
        FaceOptions faceOption = new
                FaceOptions.Builder()
                .cropAlgorithm(cropAlgorithm)
                .setMinimumFaceSize(6)
                .enableAgeClassification()
                .enableDebug()
                .build();
        viola.detectFace(bitmap, faceOption);
    }

    private final FaceDetectionListener listener = new FaceDetectionListener() {
        @Override
        public void onFaceDetected(@NotNull Result result) {
            facePhotoAdapter.bindData(result.getFacePortraits());
            tvErrorMessage.setVisibility(View.GONE);
        }

        @Override
        public void onFaceDetectionFailed(@NotNull FaceDetectionError error, @NotNull String message) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(View.VISIBLE);
            facePhotoAdapter.bindData(new ArrayList<FacePortrait>());
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == imagePickerIntentId && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            String imagePath;
            try {
                imagePath = Util.Companion.getPath(ViolaSampleJavaActivity.this, pickedImage);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(imagePath, options);
                try {
                    bitmap = Util.Companion.modifyOrientation(bitmap, imagePath);
                    ivInputImage.setImageBitmap(bitmap);
                    facePhotoAdapter.bindData(new ArrayList<FacePortrait>());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private final PermissionHelper.PermissionsListener permissionsListener = new PermissionHelper.PermissionsListener() {
        @Override
        public void onPermissionGranted(int request_code) {
            tvErrorMessage.setVisibility(View.GONE);
            pickImageFromGallery();
        }

        @Override
        public void onPermissionRejectedManyTimes(@NonNull List<String> rejectedPerms, int request_code, boolean neverAsk) {
            tvErrorMessage.setText("Permission for storage access denied.");
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
