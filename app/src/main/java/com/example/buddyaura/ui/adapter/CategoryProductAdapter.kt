package com.example.buddyaura.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buddyaura.R
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.ui.fragment.ProductDetailFragment
import com.example.buddyaura.util.WishlistManager   // ✅ ADDED

class CategoryProductAdapter(
    private val list: MutableList<Catalogue>
) : RecyclerView.Adapter<CategoryProductAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.productImage)
        val name: TextView = view.findViewById(R.id.productName)
        val desc: TextView = view.findViewById(R.id.productDesc)
        val price: TextView = view.findViewById(R.id.productPrice)

        val wishlist: ImageView = view.findViewById(R.id.ivWishlist) // ✅ ADDED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_grid, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.img)
        } else if (item.imageRes != null) {
            holder.img.setImageResource(item.imageRes)
        } else {
            holder.img.setImageResource(R.drawable.placeholder)
        }

        holder.name.text = item.title
        holder.desc.text = item.description
        holder.price.text = "₹${item.price}"

        // ❤️ WISHLIST LOGIC (ONLY ADDITION)
        val isLiked = WishlistManager.isWishlisted(item)
        holder.wishlist.setImageResource(
            if (isLiked) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
        )

        holder.wishlist.setOnClickListener {
            if (WishlistManager.isWishlisted(item)) {
                WishlistManager.remove(item)
                holder.wishlist.setImageResource(R.drawable.ic_heart_outline)
            } else {
                WishlistManager.add(item)
                holder.wishlist.setImageResource(R.drawable.ic_heart_filled)
            }
        }
        // ❤️ END WISHLIST LOGIC

        holder.itemView.setOnClickListener {
            val fragment = ProductDetailFragment()

            val bundle = Bundle().apply {
                putString("imageUrl", item.imageUrl)
                putInt("imageRes", item.imageRes ?: 0)
                putString("name", item.title)
                putString("productDescription", item.description)
                putInt("price", item.price)
            }

            fragment.arguments = bundle

            val activity = context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<Catalogue>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
