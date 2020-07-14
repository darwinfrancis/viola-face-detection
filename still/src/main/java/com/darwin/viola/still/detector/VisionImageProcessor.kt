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
package com.darwin.viola.still.detector

import android.graphics.Bitmap

/**
 * An interface to process the images with vision face detector.
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
internal interface VisionImageProcessor {

    /**
     * Processes a bitmap image.
     */
    fun processBitmap(bitmap: Bitmap)


    /**
     * Stops the underlying machine learning model and release resources.
     */
    fun stop()
}