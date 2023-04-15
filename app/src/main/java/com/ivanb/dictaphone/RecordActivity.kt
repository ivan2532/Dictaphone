package com.ivanb.dictaphone

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ivanb.dictaphone.record.AndroidAudioRecorded
import org.w3c.dom.Text
import java.io.File


class RecordActivity : AppCompatActivity() {

    private val recordStopButton: ImageView by lazy {
        findViewById(R.id.record_stop)
    }

    private val descriptionInput: EditText by lazy {
        findViewById(R.id.editTextDescription)
    }

    private val currentTimeTextView: TextView by lazy {
        findViewById(R.id.current_time)
    }

    private val recorder: AndroidAudioRecorded by lazy {
        AndroidAudioRecorded(applicationContext)
    }

    private lateinit var countDownTimer: CountDownTimer
    private var timeElapsedMillis: Long = 0
    private val tickIntervalMillis: Long = 1000 // 1 second

    private var audioFile: File? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, tickIntervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                timeElapsedMillis += tickIntervalMillis
                updateTimerText()
            }

            override fun onFinish() {

            }
        }

        recordStopButton.setOnClickListener {

            if(!isRecording) {

                File(cacheDir, "temp.mp3").also {
                    recorder.start(it)
                    countDownTimer.start()
                    isRecording = true
                    audioFile = it
                }
                recordStopButton.setImageResource(R.drawable.baseline_stop_circle_24)
            }
            else {

                recorder.stop()
                countDownTimer.cancel()
                isRecording = false

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Save")

                val input = EditText(this)

                input.inputType =
                    InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("Save"
                ) { _, _ ->
                    val pathToSave = applicationContext.filesDir.toString() + '/' + input.text.toString()
                    val destinationAudioFile = File("$pathToSave.mp3")
                    audioFile?.copyTo(destinationAudioFile, true)
                    audioFile?.delete()

                    val data = Intent()
                    data.putExtra("filePath", destinationAudioFile.path.toString())
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }

                builder.setNegativeButton("Cancel"
                ) { dialog, _ ->
                    dialog.cancel()

                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                builder.show()
            }
        }
    }

    private fun updateTimerText() {
        val seconds = (timeElapsedMillis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        currentTimeTextView.text = "$minutes:${String.format("%02d", remainingSeconds)}"
    }
}