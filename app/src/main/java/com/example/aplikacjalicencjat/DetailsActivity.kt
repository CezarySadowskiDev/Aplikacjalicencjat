package com.example.aplikacjalicencjat

import android.provider.BaseColumns

object TableInfo: BaseColumns {
    const val TABLE_NAME = "Markers"
    const val TABLE_COLUMN_LATITUDE = "latitude"
    const val TABLE_COLUMN_LONGITUDE = "longitude"
    const val TABLE_COLUMN_NAME = "name"
    const val TABLE_COLUMN_TYPE = "type"
}
