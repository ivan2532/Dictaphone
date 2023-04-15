package com.ivanb.dictaphone.record

import java.io.File

interface AudioRecorder {

    fun start(outputFile: File)
    fun stop()
}