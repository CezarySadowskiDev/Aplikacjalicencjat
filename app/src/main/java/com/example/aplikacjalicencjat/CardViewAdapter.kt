//package com.example.aplikacjalicencjat
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//
//class CardViewAdapter(context: Context): RecyclerView.Adapter<MyViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val cardViewMarker = layoutInflater.inflate(R.layout.card_view, parent, false)
//
//        return MyViewHolder(cardViewMarker)
//    }
//
//    override fun getItemCount(): Int {
//
//        return 3
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//
//    }
//
//}
//
//class MyViewHolder(view: View): RecyclerView.ViewHolder(view)