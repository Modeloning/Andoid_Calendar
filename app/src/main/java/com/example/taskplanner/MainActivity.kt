package com.example.taskplanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    private var tasks = mutableListOf<Task>()
    private var selectedTaskPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        taskDao = database.taskDao()

        recyclerView = findViewById(R.id.tasksRecyclerView)

        setupRecyclerView()

        lifecycleScope.launch {

            val tasksFromDatabase = taskDao.getAllTasks()

            if (tasksFromDatabase.isEmpty()) {

                val oldTasks = listOf(
                    Task(
                        title = "Подготовить презентацию",
                        description = "Собрать основные материалы для выступления",
                        date = "06.10.2026",
                        time = "13:30"
                    ),
                    Task(
                        title = "Повторить Kotlin",
                        description = "Разобрать темы по коллекциям и классам",
                        date = "07.10.2026",
                        time = "18:00"
                    ),
                    Task(
                        title = "Закончить практику",
                        description = "Проверить выполненные задания перед сдачей",
                        date = "08.10.2026",
                        time = "15:00"
                    ),
                    Task(
                        title = "Прочитать статью",
                        description = "Изучить материал для следующего занятия",
                        date = "09.10.2026",
                        time = "20:30"
                    ),
                    Task(
                        title = "Составить план недели",
                        description = "Записать учебные дела и сроки выполнения",
                        date = "10.10.2026",
                        time = "10:00"
                    )
                )

                for (task in oldTasks) {
                    taskDao.insertTask(task)
                }
            }

            val updatedTasks = taskDao.getAllTasks()

            tasks.clear()
            tasks.addAll(updatedTasks)

            // Проверяем количество задач в базе
            android.util.Log.d(
                "TASK_DATABASE",
                "Количество задач в базе: ${updatedTasks.size}"
            )

            // Выводим все задачи в Logcat
            for (task in updatedTasks) {
                android.util.Log.d(
                    "TASK_DATABASE",
                    "ID: ${task.id}, название: ${task.title}"
                )
            }

            adapter.notifyDataSetChanged()
        }

        val addTaskButton =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.addTaskButton
            )

        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }

    private fun setupRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(tasks) { task ->

            selectedTaskPosition = tasks.indexOf(task)

            val intent = Intent(this, TaskDetailActivity::class.java)

            intent.putExtra("task_id", task.id)
            intent.putExtra("title", task.title)
            intent.putExtra("description", task.description)
            intent.putExtra("date", task.date)
            intent.putExtra("completed", task.isDone)

            startActivityForResult(intent, 100)
        }

        recyclerView.adapter = adapter
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        // Изменение статуса задачи
        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val completed = data.getBooleanExtra("completed", false)

            if (completed && selectedTaskPosition != -1) {

                val oldTask = tasks[selectedTaskPosition]

                val updatedTask = oldTask.copy(
                    isDone = true
                )

                lifecycleScope.launch {

                    taskDao.updateTask(updatedTask)

                    tasks[selectedTaskPosition] = updatedTask

                    adapter.notifyItemChanged(selectedTaskPosition)
                }
            }
        }

        // Добавление новой задачи
        if (requestCode == 200 &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val title = data.getStringExtra("title") ?: ""
            val description = data.getStringExtra("description") ?: ""
            val date = data.getStringExtra("date") ?: ""

            val newTask = Task(
                title = title,
                description = description,
                isDone = false,
                date = date,
                time = "15:00"
            )

            lifecycleScope.launch {

                taskDao.insertTask(newTask)

                val updatedTasks = taskDao.getAllTasks()

                tasks.clear()
                tasks.addAll(updatedTasks)

                android.util.Log.d(
                    "TASK_DATABASE",
                    "После добавления задач в базе: ${updatedTasks.size}"
                )

                adapter.notifyDataSetChanged()
            }
        }
    }
}