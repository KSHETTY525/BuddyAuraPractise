package com.example.buddyaura.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.data.CartItem

class CartAdapter(
    private val items: List<CartItem>,
    private val onSelectionChanged: (() -> Unit)? = null  // callback when selection changes
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.cbSelectProduct)
        val image: ImageView = view.findViewById(R.id.itemImage)
        val name: TextView = view.findViewById(R.id.itemName)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val quantity: TextView = view.findViewById(R.id.itemQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.image.setImageResource(item.imageRes)
        holder.name.text = item.name
        holder.price.text = "₹${item.price}"
        holder.quantity.text = "Qty: ${item.quantity}"

        // To avoid checkbox recycling issue, first clear listener
        holder.checkbox.setOnCheckedChangeListener(null)

        // Set checkbox state from model
        holder.checkbox.isChecked = item.isSelected

        // Listen for user checkbox clicks
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onSelectionChanged?.invoke()
        }

        // Optional: clicking whole item toggles checkbox
        holder.itemView.setOnClickListener {
            val newState = !holder.checkbox.isChecked
            holder.checkbox.isChecked = newState
        }
    }

    override fun getItemCount(): Int = items.size
}
