package com.ivanb.dictaphone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanb.dictaphone.playback.AndroidAudioPlayer
import com.ivanb.dictaphone.record.AndroidAudioRecorded
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class MainActivity : AppCompatActivity() {

    private var recordModels:MutableList<RecordModel> = ArrayList()

    private val recorder by lazy {
        AndroidAudioRecorded(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recordingsRecyclerView)
    }
    private val adapter: RecordRecyclerViewAdapter by lazy {
        RecordRecyclerViewAdapter(this, recordModels)
    }

    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            0
        )

        setUpRecordModels()

        recyclerView.adapter = adapter;
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addButton:ImageView = findViewById(R.id.add)
        addButton.setOnClickListener { view ->
            val intent = Intent(this, RecordActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    fun startRecording(view: View) {

        File(cacheDir, "audio.mp3").also {
            recorder.start(it)
            audioFile = it
        }
    }

    fun stopRecording(view: View) {

        File(cacheDir, "audio.mp3").also {
            recorder.stop()
        }
    }

    fun playAudio(view: View) {
        player.playFile(audioFile ?: return)
    }

    fun stopAudio(view: View) {
        player.stop()
    }

    private fun setUpRecordModels() {

        val path: String = applicationContext.filesDir.toString()

        val directory = File(path)
        val files = directory.listFiles { file -> file.extension == "mp3" } ?: return

        val test = "prvi.mp3".replaceFirst("[.][^.]+$", "")

        recordModels =  files.map {
            RecordModel(it.nameWithoutExtension, Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime(), it.path)
        }.toMutableList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            val audioFilePath = data?.getStringExtra("filePath")
            val newFile = File(audioFilePath)
            recordModels.add(RecordModel(newFile.nameWithoutExtension, Files.readAttributes(newFile.toPath(), BasicFileAttributes::class.java).creationTime(), newFile.path))
            adapter.notifyItemInserted(recordModels.size - 1)
        }
    }
}