package com.example.storehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.content.Intent

class EditCompanyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_company)

        val empresaName = intent.getStringExtra("empresa_name")
        val empresaPosition = intent.getIntExtra("empresa_position", -1)
        val empresaLat = intent.getStringExtra("empresa_lat") // Clave correcta para la latitud
        val empresaLng = intent.getStringExtra("empresa_lon") // Clave correcta para la longitud

        val nameEditText = findViewById<EditText>(R.id.et_company_name)
        val latEditText = findViewById<EditText>(R.id.editLatitud)
        val lonEditText = findViewById<EditText>(R.id.editLongitud)

        nameEditText.setText(empresaName)
        latEditText.setText(empresaLat)
        lonEditText.setText(empresaLng)

        findViewById<Button>(R.id.btn_save_company).setOnClickListener {
            val newEmpresaName = nameEditText.text.toString()
            val newLat = latEditText.text.toString()
            val newLng = lonEditText.text.toString()

            val resultIntent = Intent().apply {
                putExtra("new_empresa_name", newEmpresaName)
                putExtra("empresa_position", empresaPosition)
                putExtra("new_lat", newLat)
                putExtra("new_lon", newLng)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
