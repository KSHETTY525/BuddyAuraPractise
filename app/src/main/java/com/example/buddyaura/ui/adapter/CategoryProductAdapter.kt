package com.example.buddyaura.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.ui.fragment.ProductDetailFragment

class CategoryProductAdapter(
    private val list: MutableList<Catalogue>
) : RecyclerView.Adapter<CategoryProductAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.productImage)
        val name: TextView = view.findViewById(R.id.productName)
        val desc: TextView = view.findViewById(R.id.productDesc)
        val price: TextView = view.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_grid, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        // Bind data
        holder.img.setImageResource(item.imageRes)
        holder.name.text = item.title
        holder.desc.text = item.description
        holder.price.text = "₹${item.price}"

        // Handle click
        holder.itemView.setOnClickListener {

            val fragment = ProductDetailFragment()

            val bundle = Bundle().apply {
                putInt("imageRes", item.imageRes)
                putString("name", item.title)
                putString("description", item.description)
                putInt("price", item.price)
            }

            fragment.arguments = bundle

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = list.size
}
