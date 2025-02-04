package com.example.storehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts

class ProjectActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private val projects = mutableListOf<String>()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var companyName: String

    private val editProjectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val position = data?.getIntExtra("project_position", -1)
            val newName = data?.getStringExtra("new_project_name")
            if (position != null && position != -1 && newName != null) {
                val oldName = projects[position]
                dbHelper.updateProyecto(companyName, oldName, newName)
                projects[position] = newName
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyect)

        dbHelper = DBHelper(this)
        companyName = intent.getStringExtra("company_name") ?: ""

        if (companyName.isEmpty()) {
            // Si no se recibió el nombre de la empresa, se termina la actividad
            finish()
            return
        }

        findViewById<TextView>(R.id.tv_company_name).text = "Proyectos de $companyName"

        listView = findViewById(R.id.list_proyectos)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, projects)
        listView.adapter = adapter

        findViewById<Button>(R.id.btn_create_proyect).setOnClickListener {
            createNewProject()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            showProjectOptions(position)
        }

        loadProjects()
    }


    private fun createNewProject() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear nuevo proyecto")

        val input = EditText(this)
        input.hint = "Nombre del proyecto"
        builder.setView(input)

        builder.setPositiveButton("Crear") { _, _ ->
            val projectName = input.text.toString()
            if (projectName.isNotEmpty()) {
                dbHelper.addProyecto(companyName, projectName)
                loadProjects()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showProjectOptions(position: Int) {
        val projectName = projects[position]
        val builder = AlertDialog.Builder(this)
        builder.setTitle(projectName)
        builder.setItems(arrayOf("Editar", "Eliminar")) { _, which ->
            when (which) {
                0 -> { // Editar
                    val intent = Intent(this, EditProyectActivity::class.java).apply {
                        putExtra("project_name", projectName)
                        putExtra("project_position", position)
                    }
                    editProjectLauncher.launch(intent)
                }
                1 -> { // Eliminar
                    dbHelper.deleteProyecto(companyName, projectName)
                    projects.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        builder.show()
    }

    private fun loadProjects() {
        projects.clear()
        projects.addAll(dbHelper.getProyectos(companyName))
        adapter.notifyDataSetChanged()
    }
}
