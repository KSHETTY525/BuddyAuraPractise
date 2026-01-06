package com.example.buddyaura.ui.fragment

import android.app.AlertDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.ProfileViewModel
import com.example.buddyaura.R
import com.example.buddyaura.ui.adapter.ProfileImagesAdapter

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileImage: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProfileImagesAdapter
    private lateinit var viewModel: ProfileViewModel

    private var imageUri: Uri? = null

    // 📷 Camera (single image)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                viewModel.imageList.add(imageUri!!)
                showLatestImage()
                adapter.notifyDataSetChanged()
            }
        }

    // 🖼️ Gallery (MULTIPLE images)
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.imageList.addAll(uris)
                showLatestImage()
                adapter.notifyDataSetChanged()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init ViewModel
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        // Init views
        profileImage = view.findViewById(R.id.profileImage)
        recycler = view.findViewById(R.id.profileImagesRecycler)

        // Setup RecyclerView
        adapter = ProfileImagesAdapter(viewModel.imageList)
        recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = adapter

        // Restore last image
        showLatestImage()

        profileImage.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showLatestImage() {
        if (viewModel.imageList.isNotEmpty()) {
            profileImage.setImageURI(viewModel.imageList.last())
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Profile Picture")
            .setItems(options) { _, which ->
                if (which == 0) openCamera() else openGallery()
            }
            .show()
    }

    private fun openCamera() {
        imageUri = createImageUri()
        cameraLauncher.launch(imageUri)
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openGalleryInternal()
            } else {
                showPermissionMessage()
            }
        }


    private fun openGallery() {
        galleryLauncher.launch("image/*")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // ✅ Android 13+ → no permission needed
            openGalleryInternal()
        } else {
            // ❗ Android 12 and below → permission required
            if (requireContext().checkSelfPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryInternal()
            } else {
                galleryPermissionLauncher.launch(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun openGalleryInternal() {
        galleryLauncher.launch("image/*")
    }

    private fun showPermissionMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Please give permission to access gallery")
            .setPositiveButton("OK", null)
            .show()
    }



    private fun createImageUri(): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    }


}
