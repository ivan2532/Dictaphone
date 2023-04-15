package com.ivanb.dictaphone

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private val titleTextView: TextView by lazy {
        findViewById(R.id.title)
    }
    private val descriptionTextView: TextView by lazy {
        findViewById(R.id.description)
    }
    private val currentTimeTextView: TextView by lazy {
        findViewById(R.id.current_time)
    }
    private val totalTimeTextView: TextView by lazy {
        findViewById(R.id.total_time)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seek_bar)
    }
    private val pausePlayButton: ImageView by lazy {
        findViewById(R.id.pause_play)
    }
    private val seekForwardButton: ImageView by lazy {
        findViewById(R.id.next)
    }
    private val seekBackwardButton: ImageView by lazy {
        findViewById(R.id.previous)
    }
    private val recordName by lazy {
        intent.getSerializableExtra("NAME") as String
    }
    private val recordPath by lazy {
        intent.getSerializableExtra("PATH") as String
    }

    private val mediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        titleTextView.isSelected = true
        titleTextView.text = recordName;

        val descriptionFile = File(applicationContext.filesDir.toString() + '/' + recordName + ".txt")
        if(descriptionFile.exists()) {
            val fileReader = FileReader(descriptionFile)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            val text = stringBuilder.toString()
            bufferedReader.close()

            descriptionTextView.text = text;
        } else {
            descriptionTextView.text = "There is no description."
        }

        pausePlayButton.setOnClickListener { pausePlay() }
        seekForwardButton.setOnClickListener { seekForward() }
        seekBackwardButton.setOnClickListener { seekBackward() }

        playMusic()
        totalTimeTextView.text = convertToMMSS(mediaPlayer.duration)

        runOnUiThread(
            object : Runnable {
                override fun run() {
                    seekBar.progress = mediaPlayer.currentPosition
                    currentTimeTextView.text = convertToMMSS(mediaPlayer.currentPosition)

                    if(mediaPlayer.isPlaying) {
                        pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_filled_24)
                    }
                    else {
                        pausePlayButton.setImageResource(R.drawable.baseline_play_circle_filled_24)
                    }

                    Handler().postDelayed(this, 100)
                }
            }
        )

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playMusic() {

        mediaPlayer.reset()

        try {
            mediaPlayer.setDataSource(recordPath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            seekBar.progress = 0
            seekBar.max = mediaPlayer.duration
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun seekForward() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + 10 * 1000)
    }

    private fun seekBackward() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition - 10 * 1000)
    }

    private fun pausePlay() {

        if(mediaPlayer.isPlaying) mediaPlayer.pause()
        else mediaPlayer.start()
    }

    private fun convertToMMSS(milliseconds: Int): String {

        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % TimeUnit.MINUTES.toSeconds(1));
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.stop()
    }
}