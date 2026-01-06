package com.example.buddyaura.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R

class ProfileImagesAdapter(
    private val images: List<Uri>
) : RecyclerView.Adapter<ProfileImagesAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_image, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.image.setImageURI(images[position])
    }

    override fun getItemCount() = images.size
}
