package com.ivanb.dictaphone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class MainActivity : AppCompatActivity() {

    private var recordModels:MutableList<RecordModel> = ArrayList()

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recordingsRecyclerView)
    }
    private val adapter: RecordRecyclerViewAdapter by lazy {
        RecordRecyclerViewAdapter(this, recordModels)
    }

    private val comparator = Comparator<RecordModel> { recordModel1, recordModel2 ->
        recordModel2.creationTime.compareTo(recordModel1.creationTime)
    }

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

        val codebookButton: Button = findViewById(R.id.codebook_button)
        codebookButton.setOnClickListener { view ->
            val intent = Intent(this, CodebookActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpRecordModels() {

        val path: String = applicationContext.filesDir.toString()

        val directory = File(path)
        val files = directory.listFiles { file -> file.extension == "mp3" } ?: return

        recordModels =  files.map {
            RecordModel(it.nameWithoutExtension.substringBefore('-'), Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime(), it.path)
        }.toMutableList()
        recordModels.sortWith(comparator)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            val audioFilePath = data?.getStringExtra("filePath")
            val newFile = File(audioFilePath)

            val fileName = newFile.nameWithoutExtension.substringBefore('-')

            recordModels.add(RecordModel(fileName, Files.readAttributes(newFile.toPath(), BasicFileAttributes::class.java).creationTime(), newFile.path))
            recordModels.sortWith(comparator)
            adapter.notifyItemInserted(recordModels.indexOfFirst { it.path == newFile.path })
        }
    }
}