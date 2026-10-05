package com.example.taskplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        intent.getIntExtra("task_id", 0)
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val completed = intent.getBooleanExtra("completed", false)

        val backButton = findViewById<Button>(R.id.backButton)
        val completeButton = findViewById<Button>(R.id.completeButton)

        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailDate = findViewById<TextView>(R.id.detailDate)
        val detailDescription = findViewById<TextView>(R.id.detailDescription)
        val detailStatus = findViewById<TextView>(R.id.detailStatus)

        detailTitle.text = title
        detailDate.text = getString(R.string.date_prefix, date)
        detailDescription.text = description

        if (completed) {
            detailStatus.text = getString(R.string.task_done)
            completeButton.text = getString(R.string.already_done)
        } else {
            detailStatus.text = getString(R.string.task_not_done)
            completeButton.text = getString(R.string.mark_done)
        }

        backButton.setOnClickListener {
            finish()
        }

        completeButton.setOnClickListener {
            detailStatus.text = getString(R.string.task_done)
            completeButton.text = getString(R.string.already_done)

            val resultIntent = Intent()
            resultIntent.putExtra("completed", true)

            setResult(RESULT_OK, resultIntent)
        }
    }
}