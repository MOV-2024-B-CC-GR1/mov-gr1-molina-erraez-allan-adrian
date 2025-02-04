package com.example.storehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.content.Intent

class EditProyectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_proyect)

        val proyectoName = intent.getStringExtra("proyecto_name")
        val proyectoPosition = intent.getIntExtra("proyecto_position", -1)

        val editText = findViewById<EditText>(R.id.et_proyect_name)
        editText.setText(proyectoName)

        findViewById<Button>(R.id.btn_save_proyect).setOnClickListener {
            val newProyectoName = editText.text.toString()
            val resultIntent = Intent().apply {
                putExtra("new_proyecto_name", newProyectoName)
                putExtra("proyecto_position", proyectoPosition)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
