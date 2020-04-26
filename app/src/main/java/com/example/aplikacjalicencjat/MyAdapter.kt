package com.example.aplikacjalicencjat

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_view.view.*

class MyAdapter(private val db: SQLiteDatabase): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val markerRow = layoutInflater.inflate(R.layout.card_view, parent, false)

        return MyViewHolder(markerRow)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(
            TableInfo.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val liczbaWierszy = cursor.count

        cursor.close()

        return liczbaWierszy
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val markerRowName = holder.view.markerName

        val cursor = db.query(
            TableInfo.TABLE_NAME,
            null,
            BaseColumns._ID + "=?",
            arrayOf(holder.adapterPosition.plus(1).toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            if (!(cursor.getString(3).isNullOrEmpty())) {
                markerRowName.text = cursor.getString(3)
            }
        }
        cursor.close()
    }

}

class MyViewHolder(val view: View): RecyclerView.ViewHolder(view)















