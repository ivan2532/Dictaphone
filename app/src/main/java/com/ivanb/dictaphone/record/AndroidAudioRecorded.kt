package com.ivanb.dictaphone.record

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import com.ivanb.dictaphone.MainActivity
import java.io.File
import java.io.FileOutputStream


class AndroidAudioRecorded(
    private val context: Context
) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorded(): MediaRecorder {

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {

        createRecorded().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(2)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {

        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}