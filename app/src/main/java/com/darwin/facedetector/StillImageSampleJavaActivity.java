package com.darwin.facedetector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.darwin.face.still.FaceDetectionListener;
import com.darwin.face.still.FaceDetector;
import com.darwin.face.still.FoodOrder;
import com.darwin.face.still.model.CropAlgorithm;
import com.darwin.face.still.model.FaceDetectionError;
import com.darwin.face.still.model.FaceOptions;
import com.darwin.face.still.model.Result;

import org.jetbrains.annotations.NotNull;

/**
 * The class StillImageSampleJavaActivity
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 12 Jul 2020
 */
public class StillImageSampleJavaActivity extends AppCompatActivity {

    private Button btDetect;

    private FaceDetector faceDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_still_image_sample);
        btDetect = findViewById(R.id.bt_detect);

         faceDetector = new FaceDetector(listener);
        setEventListener();
    }

    private void setEventListener(){
        btDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectFace();
            }
        });
    }

    private void detectFace(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap =  BitmapFactory.decodeResource(getResources(),R.drawable.single,options);
        FaceOptions faceOptions = new FaceOptions.Builder()
                .cropAlgorithm(CropAlgorithm.SQUARE)
                .enableDebug()
                .build();
        faceDetector.detectFace(bitmap,faceOptions);
    }

    private final FaceDetectionListener listener = new FaceDetectionListener() {
        @Override
        public void onFaceDetected(@NotNull Result result) { }

        @Override
        public void onFaceDetectionFailed(@NotNull FaceDetectionError error, @NotNull String message) { }
    };
}
