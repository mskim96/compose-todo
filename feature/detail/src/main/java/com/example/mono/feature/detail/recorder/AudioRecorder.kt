package com.example.mono.feature.detail.recorder

import java.io.File

/**
 * Interface for record feature.
 */
interface AudioRecorder {

    fun start(outputFile: File)

    fun stop()
}