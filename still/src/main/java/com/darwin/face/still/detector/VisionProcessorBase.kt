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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
internal abstract class VisionProcessorBase<T> : VisionImageProcessor {

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    override fun processBitmap(bitmap: Bitmap) {
        requestDetectInImage(
            InputImage.fromBitmap(bitmap, 0), bitmap
        )
    }

    private fun requestDetectInImage(
        image: InputImage, originalImage: Bitmap
    ): Task<T> {
        return detectInImage(image).addOnSuccessListener(executor) { results: T ->
            this@VisionProcessorBase.onSuccess(results, originalImage)
        }
            .addOnFailureListener(executor) { e: Exception ->
                e.printStackTrace()
                this@VisionProcessorBase.onFailure(e)
            }
    }


    override fun stop() {
        executor.shutdown()
        isShutdown = true
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    protected abstract fun onSuccess(results: T, originalImage: Bitmap)

    protected abstract fun onFailure(e: Exception)
}
