package com.example.buddyaura.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.R

class CatalogueAdapter(
    private val items: List<Catalogue>
) : RecyclerView.Adapter<CatalogueAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productDesc: TextView = itemView.findViewById(R.id.productDesc)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val shareBtn: Button = itemView.findViewById(R.id.shareBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalogue, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.productTitle.text = item.title
        holder.productDesc.text = item.description
        holder.productPrice.text = "₹${item.price}"

        holder.productImage.setImageResource(item.imageRes)

        holder.shareBtn.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = items.size
}