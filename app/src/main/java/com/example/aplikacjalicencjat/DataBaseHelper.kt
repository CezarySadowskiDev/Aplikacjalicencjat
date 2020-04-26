package com.example.aplikacjalicencjat

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object BasicCommand {
    // tworzenie tabeli
    const val SQL_CREATE_TABLE =
        "CREATE TABLE ${TableInfo.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${TableInfo.TABLE_COLUMN_LATITUDE} VARCHAR(255) NOT NULL, " +
                "${TableInfo.TABLE_COLUMN_LONGITUDE} VARCHAR(255) NOT NULL, " +
                "${TableInfo.TABLE_COLUMN_NAME} TEXT NOT NULL, " +
                "${TableInfo.TABLE_COLUMN_TYPE} TEXT NOT NULL)"

    // usuwanie tabeli
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TableInfo.TABLE_NAME}"
}

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, TableInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommand.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommand.SQL_DELETE_TABLE)
        onCreate(db)
    }

}