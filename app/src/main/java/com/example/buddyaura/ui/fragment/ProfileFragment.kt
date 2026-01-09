package com.example.buddyaura.ui.fragment

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.buddyaura.R

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var deleteIcon: ImageView
    private var currentImageUri: Uri? = null
    private var denyCount = 0
    private var isFirstSet = true


    // ---------------- Permission Launcher ----------------
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val cameraGranted = permissions[Manifest.permission.CAMERA] == true
            val mediaGranted =
                permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
                        permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true

            if (cameraGranted && mediaGranted) {
                denyCount = 0
                showCameraGalleryDialog()
            } else {
                denyCount++

                if (denyCount >= 2) {
                    showPermissionExplanationDialog()
                } else {
                    Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // ---------------- GALLERY LAUNCHER ----------------
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                currentImageUri = it
                profileImage.setImageURI(it)
                deleteIcon.visibility = View.VISIBLE
                showSuccessToast()
            }
        }

    // ---------------- CAMERA LAUNCHER ----------------
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                profileImage.setImageBitmap(it)
                deleteIcon.visibility = View.VISIBLE
                showSuccessToast()
            }
        }

    private fun showSuccessToast() {
        if (isFirstSet) {
            Toast.makeText(requireContext(), "Profile Image Set!", Toast.LENGTH_SHORT).show()
            isFirstSet = false
        } else {
            Toast.makeText(requireContext(), "Profile Image Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- View Setup ----------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImage = view.findViewById(R.id.profileImage)
        editIcon = view.findViewById(R.id.editIcon)
        deleteIcon = view.findViewById(R.id.deleteIcon)

        profileImage.setOnClickListener { handleProfileClick() }
        editIcon.setOnClickListener {
            handleProfileClick()
        }

        deleteIcon.setOnClickListener {
            currentImageUri = null
            profileImage.setImageResource(R.drawable.outline_account_circle_24)
            deleteIcon.visibility = View.GONE

            Toast.makeText(
                requireContext(),
                "Profile Image deleted successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ---------------- Main Logic ----------------
    private fun handleProfileClick() {
        if (hasAllPermissions()) {
            showCameraGalleryDialog()
        } else {
            if (denyCount >= 2) {
                showPermissionExplanationDialog()
            } else {
                requestPermissions()
            }
        }
    }

    // ---------------- Permission Helpers ----------------
    private fun hasAllPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val media = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        return camera && media
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        permissionLauncher.launch(permissions)
    }

    // ---------------- Custom Explanation Dialog ----------------
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("You need to give camera and media permission in order to set the profile image.")
            .setPositiveButton("OK") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        startActivity(intent)
    }

    // ---------------- UI ACTIONS ----------------
    private fun showCameraGalleryDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Option")
            .setItems(options) { _, which ->
                if (which == 0) openCamera() else openGallery()
            }
            .show()
    }

    private fun openCamera() {
        try {
            cameraLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Camera app not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
}
