package com.ivanb.dictaphone

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.*

class CodebookActivity : AppCompatActivity() {

    private val codebookFile by lazy {
        File(applicationContext.filesDir.toString() + "/codebook.txt")
    }

    var codeModels: MutableList<CodeModel> = ArrayList()

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.codesRecyclerView)
    }
    private val adapter: CodeRecyclerViewAdapter by lazy {
        CodeRecyclerViewAdapter(this, this, codeModels)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codebook)

        readCodebook()

        recyclerView.adapter = adapter;
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addButton: ImageView = findViewById(R.id.add_code)
        addButton.setOnClickListener { view ->
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Add Code")

            val input = EditText(this)

            input.inputType =
                InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("Save"
            ) { _, _ ->
                if(input.text.toString().isEmpty()) {
                    Toast.makeText(applicationContext, "A code can't be empty!", Toast.LENGTH_SHORT).show()
                } else {
                    addCode(input.text.toString())
                }
            }

            builder.setNegativeButton("Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun readCodebook() {

        if(!codebookFile.exists()) return

        codeModels = codebookFile.readLines().map { CodeModel(it) }.toMutableList()
        codeModels.sortBy { it.code }
    }

    private fun addCode(newCode: String) {

        codeModels.add(CodeModel(newCode))
        codeModels.sortBy { it.code }
        adapter.notifyItemInserted(codeModels.indexOfFirst { it.code == newCode })

        val fileWriter = FileWriter(codebookFile, codebookFile.exists())
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(newCode + '\n')
        bufferedWriter.close()
    }

    fun deleteCode(codeToDelete: String) {

        val indexToDelete = codeModels.indexOfFirst { it.code == codeToDelete }
        codeModels.removeAll { it.code == codeToDelete }
        adapter.notifyItemRemoved(indexToDelete)

        val tempFile = File(applicationContext.filesDir, "temp.txt")

        val reader = BufferedReader(FileReader(codebookFile))
        val writer = BufferedWriter(FileWriter(tempFile))

        var currentLine: String?
        while (reader.readLine().also { currentLine = it } != null) {
            // Compare the current line to the line to delete
            if (currentLine != codeToDelete) {
                // Write the current line to the new file
                writer.write(currentLine)
                writer.newLine()
            }
        }

        writer.close()
        reader.close()

        // Replace the original file with the new file
        tempFile.renameTo(codebookFile)
    }
}