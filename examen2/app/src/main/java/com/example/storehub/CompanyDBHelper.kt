package com.example.storehub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Empresa(var id: Long = -1, var name: String, var lat: String, var lng: String)


class EmpresaDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "empresa_manager.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_EMPRESAS = "empresas"
        const val COLUMN_ID = "id"
        const val COLUMN_EMPRESA_NAME = "empresa_name"
        const val COLUMN_LAT = "latitud"
        const val COLUMN_LNG = "longitud"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_EMPRESAS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_EMPRESA_NAME TEXT,"
                + "$COLUMN_LAT REAL,"
                + "$COLUMN_LNG REAL)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMPRESAS")
        onCreate(db)
    }

    fun addEmpresa(empresa: Empresa) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_EMPRESA_NAME, empresa.name)
                put(COLUMN_LAT, empresa.lat.toDoubleOrNull())
                put(COLUMN_LNG, empresa.lng.toDoubleOrNull())
            }
            val id = db.insert(TABLE_EMPRESAS, null, values)
            empresa.id = id // Actualizar el ID de la empresa con el valor insertado
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun updateEmpresa(empresa: Empresa) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_EMPRESA_NAME, empresa.name)
                put(COLUMN_LAT, empresa.lat.toDoubleOrNull())
                put(COLUMN_LNG, empresa.lng.toDoubleOrNull())
            }
            db.update(
                TABLE_EMPRESAS,
                values,
                "$COLUMN_ID=?",
                arrayOf(empresa.id.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun deleteEmpresa(empresa: Empresa) {
        val db = this.writableDatabase
        try {
            db.delete(
                TABLE_EMPRESAS,
                "$COLUMN_ID=?",
                arrayOf(empresa.id.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun getEmpresas(): List<Empresa> {
        val empresaList = mutableListOf<Empresa>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_EMPRESAS,
            arrayOf(COLUMN_ID, COLUMN_EMPRESA_NAME, COLUMN_LAT, COLUMN_LNG),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val empresaName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMPRESA_NAME))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)).toString()
                val lng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LNG)).toString()
                empresaList.add(Empresa(id, empresaName, lat, lng))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return empresaList
    }
}
