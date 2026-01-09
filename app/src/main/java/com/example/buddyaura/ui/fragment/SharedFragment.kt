package com.example.buddyaura.ui.fragment

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.receiver.NetworkReceiver
import com.example.buddyaura.services.MusicService
import com.example.buddyaura.ui.adapter.ProductImagesAdapter
import com.example.buddyaura.util.ImageUtils
import android.net.Uri

class SharedFragment : Fragment(R.layout.fragment_shared) {

    private val networkReceiver = NetworkReceiver()

    private val productImages = mutableListOf<Uri>()
    private lateinit var adapter: ProductImagesAdapter

    // 📸 Camera Launcher
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = ImageUtils.saveBitmap(requireContext(), it)
                productImages.add(uri)
                adapter.notifyDataSetChanged()
            }
        }

    // 🖼 Gallery Launcher (Multiple)
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                productImages.addAll(uris)
                adapter.notifyDataSetChanged()
            }
        }

    // 📂 Camera / Gallery Dialog
    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(null)
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvImages = view.findViewById<RecyclerView>(R.id.rvImages)
        val btnAddProducts = view.findViewById<Button>(R.id.btnAddProducts)
        val startBtn = view.findViewById<Button>(R.id.btnStart)
        val stopBtn = view.findViewById<Button>(R.id.btnStop)

        adapter = ProductImagesAdapter(productImages) { position ->
            if (position in productImages.indices) {
                productImages.removeAt(position)
                Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
            }
        }

        rvImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = adapter

        // ➕ Add Images
        btnAddProducts.setOnClickListener {
            showImagePickerDialog()
        }

        // ▶ Start Music
        startBtn.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                requireContext().startForegroundService(intent)
            else
                requireContext().startService(intent)
        }

        // ⏹ Stop Music
        stopBtn.setOnClickListener {
            requireContext().stopService(
                Intent(requireContext(), MusicService::class.java)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        ContextCompat.registerReceiver(
            requireActivity(),
            networkReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(networkReceiver)
    }
}
