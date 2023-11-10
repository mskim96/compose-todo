package com.example.mono.feature.detail.recorder

import android.net.Uri
import java.io.File

interface AudioPlayer {
    fun playFile(file: File)

    fun playUri(fileUri: Uri)

    fun stop()
}