package com.ivanb.dictaphone

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CodeRecyclerViewAdapter(
    private val context: Context,
    private val codebookActivity: CodebookActivity,
    private val codeModels:MutableList<CodeModel>
) : RecyclerView.Adapter<CodeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.code_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return codeModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentCode = codeModels[position].code
        holder.codeTextView.text = currentCode

        holder.itemView.setOnLongClickListener { view ->

            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)

            builder.setTitle("Delete $currentCode")
            builder.setMessage("Are you sure?")

            builder.setPositiveButton(
                "YES"
            ) { dialog, _ ->
                codebookActivity.deleteCode(currentCode)
                dialog.dismiss()
            }

            builder.setNegativeButton(
                "NO"
            ) { dialog, _ ->
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.show()

            true
        }
    }

    class ViewHolder(
        itemView: View,
        val codeTextView: TextView = itemView.findViewById(R.id.code)
    ) : RecyclerView.ViewHolder(itemView)
}