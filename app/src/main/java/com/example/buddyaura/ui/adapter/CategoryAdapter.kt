package com.example.buddyaura.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.data.Category
import com.example.buddyaura.R
import de.hdodenhof.circleimageview.CircleImageView

class CategoryAdapter(
    private val list: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0   // 🔹 Track selected category

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: CircleImageView = view.findViewById(R.id.categoryImage)
        val name: TextView = view.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = list[position]

        holder.image.setImageResource(item.imageRes)
        holder.name.text = item.name

        // 🔹 Highlight selected category
        if (position == selectedPosition) {
            holder.name.setTextColor(holder.itemView.context.getColor(R.color.ocean))
        } else {
            holder.name.setTextColor(holder.itemView.context.getColor(R.color.black))
        }

        // ✅ Click handler
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()   // refresh UI highlight
            onClick(item)           // call fragment logic
        }
    }

    override fun getItemCount(): Int = list.size
}
