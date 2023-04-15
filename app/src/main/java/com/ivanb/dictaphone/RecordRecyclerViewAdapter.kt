package com.ivanb.dictaphone

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class RecordRecyclerViewAdapter(
    private val context: Context,
    private val recordModels:MutableList<RecordModel>
) : RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater:LayoutInflater = LayoutInflater.from(context)
        val view:View = inflater.inflate(R.layout.recording_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentRecord = recordModels[position]
        holder.nameTextView.text = currentRecord.name
        holder.dateTimeView.text = currentRecord.getCreationTimeString()

        holder.itemView.setOnClickListener { view ->

            val intent = Intent(view.context, PlayerActivity::class.java)
            intent.putExtra("NAME", currentRecord.name)
            intent.putExtra("PATH", currentRecord.path)
            view.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener { view ->

            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)

            builder.setTitle("Delete " + currentRecord.name)
            builder.setMessage("Are you sure?")

            builder.setPositiveButton(
                "YES"
            ) { dialog, _ ->
                File(currentRecord.path).delete()
                recordModels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, recordModels.size)
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

    override fun getItemCount(): Int {
        return recordModels.size
    }

    class ViewHolder(
        itemView: View,
        val nameTextView:TextView = itemView.findViewById(R.id.recordName),
        val dateTimeView:TextView = itemView.findViewById(R.id.recordDateTime)
    ) : RecyclerView.ViewHolder(itemView)
}