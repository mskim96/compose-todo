package com.example.mono.feature.detail.recorder

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import java.io.File

class AudioPlayerImpl(private val context: Context) : AudioPlayer {

    private var player: MediaPlayer? = null

    var currentPosition = mutableStateOf(player?.currentPosition)
    var duration = mutableStateOf(player?.duration)

    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    override fun playUri(fileUri: Uri) {
        MediaPlayer.create(context, fileUri).apply {
            player = this
            start()
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    fun complete() {
        player?.setOnCompletionListener {
            stop()
        }
    }
}