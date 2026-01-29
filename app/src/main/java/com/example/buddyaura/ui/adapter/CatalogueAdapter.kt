package com.example.buddyaura.ui.adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.buddyaura.R
import com.example.buddyaura.data.Catalogue

class CatalogueAdapter(
    private val items: MutableList<Catalogue>
) : RecyclerView.Adapter<CatalogueAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productDesc: TextView = itemView.findViewById(R.id.productDesc)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val shareBtn: Button = itemView.findViewById(R.id.shareBtn)
        val downloadBtn: Button = itemView.findViewById(R.id.downloadBtn)
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

        Glide.with(holder.productImage.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.productImage)

        holder.downloadBtn.setOnClickListener {
            item.imageUrl?.let { url ->
                downloadImage(holder.itemView.context, url)
            } ?: run {
                Toast.makeText(holder.itemView.context, "No image available", Toast.LENGTH_SHORT).show()
            }
        }

        holder.shareBtn.setOnClickListener {
            showKycDialog(holder.itemView.context)
        }
    }

    override fun getItemCount(): Int = items.size

    // ✅ Pagination support
    fun addItems(newItems: List<Catalogue>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    // ✅ For refresh
    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    // ================= IMAGE DOWNLOAD =================

    private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val filename = "BuddyAura_${System.currentTimeMillis()}.jpg"

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BuddyAura")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            val outputStream = resolver.openOutputStream(it)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            outputStream.close()

            Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadImage(context: Context, imageUrl: String) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    saveImageToGallery(context, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    // ================= KYC DIALOG =================

    private fun showKycDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("KYC Required")
            .setMessage("To become a reseller, you must complete your KYC.")
            .setPositiveButton("Complete KYC") { _, _ ->
                Toast.makeText(context, "Redirecting to KYC...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
