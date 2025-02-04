package com.example.storehub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "empresa_proyectos.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_PROYECTOS = "proyectos"
        const val COLUMN_ID = "id"
        const val COLUMN_EMPRESA_NAME = "empresa_name"
        const val COLUMN_PROYECTO_NAME = "proyecto_name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_PROYECTOS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_EMPRESA_NAME TEXT,"
                + "$COLUMN_PROYECTO_NAME TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROYECTOS")
        onCreate(db)
    }

    fun addProyecto(empresaName: String, proyectoName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMPRESA_NAME, empresaName)
        values.put(COLUMN_PROYECTO_NAME, proyectoName)
        db.insert(TABLE_PROYECTOS, null, values)
        db.close()
    }

    fun getProyectos(empresaName: String): List<String> {
        val proyectoList = mutableListOf<String>()
        val db = this.readableDatabase

        try {
            val cursor = db.query(
                TABLE_PROYECTOS,
                arrayOf(COLUMN_PROYECTO_NAME),
                "$COLUMN_EMPRESA_NAME=?",
                arrayOf(empresaName),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    val proyecto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROYECTO_NAME))
                    if (proyecto != null) {
                        proyectoList.add(proyecto)
                    }
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return proyectoList
    }

    fun updateProyecto(empresaName: String, oldProyectoName: String, newProyectoName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PROYECTO_NAME, newProyectoName)
        db.update(
            TABLE_PROYECTOS,
            values,
            "$COLUMN_EMPRESA_NAME=? AND $COLUMN_PROYECTO_NAME=?",
            arrayOf(empresaName, oldProyectoName)
        )
        db.close()
    }

    fun deleteProyecto(empresaName: String, proyectoName: String) {
        val db = this.writableDatabase
        db.delete(
            TABLE_PROYECTOS,
            "$COLUMN_EMPRESA_NAME=? AND $COLUMN_PROYECTO_NAME=?",
            arrayOf(empresaName, proyectoName)
        )
        db.close()
    }


}
