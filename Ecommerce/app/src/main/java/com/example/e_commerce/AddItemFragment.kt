package com.example.e_commerce

import Model.CartViewModel
import Data.CartItem
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import com.example.e_commerce.databinding.FragmentAddItemBinding


class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var currentPhotoPath: String

    private var photoTakenOrSelected = false
    private var selectedImageUri: Uri? = null
    private val REQUEST_CAMERA_PERMISSION = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        binding.imageButtonProductPhoto.setOnClickListener {
            showPhotoOptionsDialog()
        }

        binding.btnCancel.setOnClickListener {
            navigateToAddToCart()
        }

        binding.btnConfirmItem.setOnClickListener {
            addItemToViewModel()
        }
        return binding.root
    }

    /**
     * Function to add a new item to the viewModel and validate input fields
     */
    private fun addItemToViewModel() {
        val name = binding.editTextProductName.text.toString()
        val itemQuantityStr = binding.editTextQuantity.text.toString()

        if (name.isNotEmpty() && itemQuantityStr.isNotEmpty()) {
            if (itemQuantityStr.isDigitsOnly()) {
                val quantity = itemQuantityStr.toInt()
                if (quantity >= 1) {
                    if (selectedImageUri != null) {
                        val cartItem = CartItem(name, quantity, selectedImageUri.toString())
                        addToCart(cartItem)

                        navigateToAddToCart()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.ChoosePhoto,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    binding.editTextQuantity.error = getString(R.string.Quantity_Error)
                }
            } else {
                binding.editTextQuantity.error = getString(R.string.InvalidQuantity)
            }
        }else {
            binding.editTextProductName.error = getString(R.string.EmptyFields_Error)
        }
    }

    /**
     * Activity result launcher for capturing a photo from the camera
     */
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result: Boolean ->
            if (result) {
                binding.imageButtonProductPhoto.setImageURI(selectedImageUri)
                photoTakenOrSelected = true
            }
        }

    /**
     * Activity result launcher for picking an image from the gallery
     */
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.imageButtonProductPhoto.setImageURI(uri)
                selectedImageUri = uri
                photoTakenOrSelected = true
            }
        }

    /**
     * Function to create a temporary image file
     */
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("HHmmss_ddMMyyyy", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    /**
     * Function to launch the camera to capture a photo and update the UI
     */
    private fun launchCamera() {
        try {
            val photoFile: File? = createImageFile()
            if (photoFile != null)     {
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.e_commerce.fileprovider",
                    photoFile
                )

                selectedImageUri = photoUri
                takePhotoLauncher.launch(photoUri)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.Error_CreatingImageFile,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (ex: IOException){
            ex.printStackTrace()
            Toast.makeText(
                requireContext(),
                R.string.Error_LaunchingCamera,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Function used to save the item added to the viewModel
     */
    private fun addToCart(cartItem: CartItem) {
        cartViewModel.addToCart(cartItem)
    }

    /**
     * Function to navigate from addItem to addCar
     */
    private fun navigateToAddToCart() {
        findNavController().navigate(R.id.action_addItem_to_addToCart)
    }

    /**
     * Function to show a dialog for choosing between camera and gallery options
     */
    private fun showPhotoOptionsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.PhotoOptions)
        builder.setMessage(R.string.Options)

        builder.setPositiveButton(R.string.TakePhoto) { _ , _ ->

            val cameraPermission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(cameraPermission), REQUEST_CAMERA_PERMISSION)
            } else {
                launchCamera()
            }
        }

        builder.setNegativeButton(R.string.Gallery) { _, _ ->

            pickImageLauncher.launch("image/*")
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Function for when the user comes back to this fragment, data is removed unless user goes to camera or gallery
     */
    override fun onResume() {
        super.onResume()
        if (!photoTakenOrSelected) {
            cleanData()
        }
        photoTakenOrSelected = false
    }

    /**
     * Clear data
     */
    private fun cleanData() {
        binding.editTextProductName.text.clear()
        binding.editTextQuantity.text.clear()
    }
}