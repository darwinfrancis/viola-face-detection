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

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The class ScopedExecutor
 *
 * @author Darwin Francis
 * @version 1.0
 * @since 09 Jul 2020
 */
internal class ScopedExecutor(private val executor: Executor) :
    Executor {
    private val shutdown =
        AtomicBoolean()

    override fun execute(command: Runnable) {
        // Return early if this object has been shut down.
        if (shutdown.get()) {
            return
        }
        executor.execute {

            // Check again in case it has been shut down in the mean time.
            if (shutdown.get()) {
                return@execute
            }
            command.run()
        }
    }

    /**
     * After this method is called, no runnable's that have been submitted or are subsequently
     * submitted will start to execute, turning this executor into a no-op.
     *
     *
     * Runnable's that have already started to execute will continue.
     */
    fun shutdown() {
        shutdown.set(true)
    }

}