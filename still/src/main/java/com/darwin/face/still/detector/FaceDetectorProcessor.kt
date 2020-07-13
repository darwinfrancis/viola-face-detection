/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.darwin.face.still.detector

import android.graphics.Bitmap
import com.darwin.face.still.FaceAnalyser
import com.darwin.face.still.FaceDetectionListener
import com.darwin.face.still.Util
import com.darwin.face.still.model.FaceDetectionError
import com.darwin.face.still.model.FaceOptions
import com.darwin.face.still.model.Result
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

internal class FaceDetectorProcessor(
    detectorOptions: FaceDetectorOptions,
    private val faceDetectionListener: FaceDetectionListener
) :
    VisionProcessorBase<List<Face>>() {

    lateinit var faceOptions: FaceOptions
    private val detector: FaceDetector = FaceDetection.getClient(detectorOptions)
    private val faceAnalyser = FaceAnalyser()

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        Util.printLog("Face detection process started.")
        return detector.process(image)
    }

    override fun onSuccess(results: List<Face>, originalImage: Bitmap) {
        Util.printLog("Face detection process completed without exceptions.")
        if (results.isEmpty()) {
            Util.printLog("Face detector can't find any face on the given image.")
            faceDetectionListener.onFaceDetectionFailed(
                FaceDetectionError.NO_FACE_DETECTED,
                FaceDetectionError.NO_FACE_DETECTED.message
            )
        } else {
            Util.printLog("Detected ${results.size} face(s), starting face analysis.")
            val portraitList = faceAnalyser.analyzeFaces(results, originalImage, faceOptions)
            if (portraitList.isEmpty()) {
                Util.printLog("Face analyser can't find any valid face with given configuration.")
                faceDetectionListener.onFaceDetectionFailed(
                    FaceDetectionError.NO_VALID_FACE_DETECTED,
                    FaceDetectionError.NO_VALID_FACE_DETECTED.message
                )
            } else {
                Util.printLog("Found ${portraitList.size} valid face(s) out of ${results.size} by the analyser.")
                val result =
                    Result(portraitList.size, portraitList)
                faceDetectionListener.onFaceDetected(result)
            }
        }
    }


    override fun onFailure(e: Exception) {
        Util.printLog("Face detection process failed with following exception {${e.message}}.")
        faceDetectionListener.onFaceDetectionFailed(FaceDetectionError.ERROR, e.message!!)
    }

}
