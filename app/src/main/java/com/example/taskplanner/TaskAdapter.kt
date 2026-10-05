package com.example.taskplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.textTaskTitle)
        val date: TextView = itemView.findViewById(R.id.textTaskDate)
        val status: TextView = itemView.findViewById(R.id.textTaskStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val task = tasks[position]

        holder.title.text = task.title
        holder.date.text = task.date

        if (task.isDone) {
            holder.status.text = holder.itemView.context.getString(R.string.task_done)
        } else {
            holder.status.text = holder.itemView.context.getString(R.string.task_not_done)
        }

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}