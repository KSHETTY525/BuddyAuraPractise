package com.example.buddyaura.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R

class ProductImagesAdapter(
    private val images: MutableList<Uri>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgPreview)
        val delete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.image.setImageURI(images[position])

        holder.delete.setOnClickListener {
            val pos = holder.adapterPosition

            if (pos != RecyclerView.NO_POSITION && pos < images.size) {
                onDeleteClick(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, images.size)
            }
        }
    }

    override fun getItemCount(): Int = images.size
}
