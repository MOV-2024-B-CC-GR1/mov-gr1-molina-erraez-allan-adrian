package com.example.storehub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: EmpresaDBHelper
    private lateinit var projectDbHelper: DBHelper
    private val empresas = mutableListOf<Empresa>()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    // Variables auxiliares para la edición de proyectos
    private var currentEditingCompanyName: String? = null
    private var currentProjectList: List<String> = emptyList()

    // Launcher para editar empresas
    private val editCompanyLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val position = data.getIntExtra("empresa_position", -1)
                if (position != -1) {
                    val empresa = empresas[position]
                    empresa.name = data.getStringExtra("new_empresa_name") ?: empresa.name
                    empresa.lat = data.getStringExtra("new_lat") ?: empresa.lat
                    empresa.lng = data.getStringExtra("new_lon") ?: empresa.lng

                    // Actualizar en la base de datos
                    dbHelper.updateEmpresa(empresa)
                    loadEmpresas()
                }
            }
        }
    }

    // Launcher para editar proyectos
    private val editProjectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val position = data.getIntExtra("proyecto_position", -1)
                if (position != -1 && currentEditingCompanyName != null) {
                    val newProjectName = data.getStringExtra("new_proyecto_name") ?: ""
                    // Recuperar el nombre anterior del proyecto de la lista actual
                    val oldProjectName = currentProjectList.getOrNull(position)
                    if (oldProjectName != null && newProjectName.isNotEmpty()) {
                        projectDbHelper.updateProyecto(currentEditingCompanyName!!, oldProjectName, newProjectName)
                        // Actualizar el diálogo mostrando la lista actualizada
                        showProjectsDialog(currentEditingCompanyName!!)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        dbHelper = EmpresaDBHelper(this)
        projectDbHelper = DBHelper(this)
        listView = findViewById(R.id.lista_empresas)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, empresas.map { it.name })
        listView.adapter = adapter

        findViewById<Button>(R.id.btn_create_empresa).setOnClickListener {
            createNewEmpresa()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            showEmpresaOptions(position)
        }

        loadEmpresas()
    }

    private fun createNewEmpresa() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registrar Nueva Empresa")

        // Inflar el layout para crear/editar empresa
        val layout = layoutInflater.inflate(R.layout.activity_edit_company, null)
        val inputName = layout.findViewById<EditText>(R.id.et_company_name)
        val inputLat = layout.findViewById<EditText>(R.id.editLatitud)
        val inputLng = layout.findViewById<EditText>(R.id.editLongitud)


        layout.findViewById<Button>(R.id.btn_save_company)?.visibility = View.GONE
        layout.findViewById<Button>(R.id.textView3)?.visibility = View.GONE

        builder.setView(layout)

        builder.setPositiveButton("Crear") { _, _ ->
            val empresaName = inputName.text.toString()
            val empresaLat = inputLat.text.toString()
            val empresaLng = inputLng.text.toString()
            if (empresaName.isNotEmpty() && empresaLat.isNotEmpty() && empresaLng.isNotEmpty()) {
                val empresa = Empresa(name = empresaName, lat = empresaLat, lng = empresaLng)
                dbHelper.addEmpresa(empresa)
                loadEmpresas()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showEmpresaOptions(position: Int) {
        val empresa = empresas[position]
        val builder = AlertDialog.Builder(this)
        builder.setTitle(empresa.name)
        builder.setItems(arrayOf("Ver Proyectos", "Añadir Proyecto", "Editar", "Eliminar", "Ver Ubicación")) { _, which ->
            when (which) {
                0 -> showProjectsDialog(empresa.name) // Ver Proyectos
                1 -> addProjectToCompany(empresa.name) // Añadir Proyecto
                2 -> { // Editar empresa
                    val intent = Intent(this, EditCompanyActivity::class.java).apply {
                        putExtra("empresa_name", empresa.name)
                        putExtra("empresa_position", position)
                        putExtra("empresa_lat", empresa.lat)
                        putExtra("empresa_lon", empresa.lng)
                    }
                    editCompanyLauncher.launch(intent)
                }
                3 -> { // Eliminar empresa
                    dbHelper.deleteEmpresa(empresa)
                    loadEmpresas()
                }
                4 -> { // Ver Ubicación
                    val intent = Intent(this, MapsActivity::class.java).apply {
                        putExtra("empresa_name", empresa.name)
                        putExtra("empresa_lat", empresa.lat)
                        putExtra("empresa_lon", empresa.lng)
                    }
                    startActivity(intent)
                }
            }
        }
        builder.show()
    }

    private fun showProjectsDialog(companyName: String) {
        val projects = projectDbHelper.getProyectos(companyName)
        currentEditingCompanyName = companyName
        currentProjectList = projects

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Proyectos de $companyName")

        if (projects.isEmpty()) {
            builder.setMessage("No hay proyectos registrados.")
            builder.setPositiveButton("Cerrar", null)
            builder.show()
        } else {
            // Mostrar la lista de proyectos
            builder.setItems(projects.toTypedArray()) { _, which ->
                val selectedProject = projects[which]
                // Al tocar un proyecto, se muestra otro diálogo con opciones "Editar" y "Eliminar"
                val options = arrayOf("Editar", "Eliminar")
                AlertDialog.Builder(this)
                    .setTitle(selectedProject)
                    .setItems(options) { _, optionIndex ->
                        when (optionIndex) {
                            0 -> { // Editar
                                val intent = Intent(this, EditProyectActivity::class.java).apply {
                                    putExtra("proyecto_name", selectedProject)
                                    putExtra("proyecto_position", which)
                                }
                                editProjectLauncher.launch(intent)
                            }
                            1 -> { // Eliminar
                                projectDbHelper.deleteProyecto(companyName, selectedProject)
                                // Recargar la lista de proyectos y actualizar el diálogo
                                showProjectsDialog(companyName)
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            builder.setPositiveButton("Cerrar", null)
            builder.show()
        }
    }

    private fun addProjectToCompany(companyName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir Proyecto a $companyName")

        val input = EditText(this)
        input.hint = "Nombre del Proyecto"
        builder.setView(input)

        builder.setPositiveButton("Agregar") { _, _ ->
            val projectName = input.text.toString()
            if (projectName.isNotEmpty()) {
                projectDbHelper.addProyecto(companyName, projectName)
                showProjectsDialog(companyName)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun loadEmpresas() {
        empresas.clear()
        empresas.addAll(dbHelper.getEmpresas())
        adapter.clear()
        adapter.addAll(empresas.map { it.name })
        adapter.notifyDataSetChanged()
    }
}
