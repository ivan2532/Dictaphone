package com.ivanb.dictaphone.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}