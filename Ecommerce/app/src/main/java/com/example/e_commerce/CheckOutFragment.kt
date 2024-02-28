package com.example.e_commerce

import Adapter.CartAdapter
import Data.AppDatabase
import Data.CartItem
import Data.PurchaseEntity
import Firestore.FirestoreManager
import android.content.pm.PackageManager
import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.databinding.FragmentCheckOutBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CheckOutFragment : Fragment() {

    private lateinit var binding: FragmentCheckOutBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_checkOut_to_cartFragment)
        }

        binding.btnCheckout.setOnClickListener {

            val cartItems = arguments?.getParcelableArrayList<CartItem>("cartItems")

            if (cartItems != null) {

                removeAllCartItemsFromFirestore()

                addPurchaseTolocalDB(cartItems)
            } else {
                Log.e("CheckOutFragment", "Error: Cart items not found in arguments")
            }
            showSuccessDialog()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_checkOut_to_MainMenu)
            }, 6000000)
        }

        if (checkLocationPermission()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }

        return view
    }

    /**
     * Function to remove all cart items from Firestore
     */
    private fun removeAllCartItemsFromFirestore() {
        FirestoreManager.removeAllCartItems(
            onSuccess = {
            Log.d("CheckOutFragment", "All cart items removed from Firestore successfully")
        },
            onFailure = { exception ->
                Log.e("CheckOutFragment", "Error removing cart items from Firestore", exception)
            }
        )
    }

    /**
     * Function to add purchase details to the local database
     */
    private fun addPurchaseTolocalDB(cartItems: List<CartItem>?) {
        if (cartItems != null) {
            val id = 0
            val userId = auth.currentUser!!.uid
            val userAddress = binding.editTextAddress.text.toString()
            val userInfo = binding.editTextFlatSuiteFloor.text.toString()
            val purchaseDate = System.currentTimeMillis()

            //create a instance of the local DB
            val purchaseDB = AppDatabase.getDatabase(requireContext())

            GlobalScope.launch(Dispatchers.IO) {
                val purchaseDao = purchaseDB.purchaseDao()

                val purchaseEntity = PurchaseEntity( id ,userId, userAddress, userInfo, cartItems, purchaseDate)
                purchaseDao.insertPurchase(purchaseEntity)
            }
        } else {
            Log.e("CheckOutFragment", "Error: Cart items are null")
        }
    }

    /**
     * Function to setup map
     */
    private fun setupMap() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        if (mapFragment is SupportMapFragment) {

            mapFragment.getMapAsync { map ->
                googleMap = map
                onMapReady()
            }
        }
    }

    /**
     * Function to check to see if user has permission to use GPS
     */
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function to request user location permission
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Function to enable the use of GPS
     */
    private fun enableMyLocation() {
        if (checkLocationPermission() && ::googleMap.isInitialized) {
            googleMap.isMyLocationEnabled = true
        }
    }


    /**
     * Function to handle the result of location permission request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission allowed
                enableMyLocation()
            }
        }
    }

    /**
     * Function to handle map interactions when the map is ready
     */
    private fun onMapReady() {
        googleMap?.let {
            val lat = 38.7257
            val lng = -9.1503
            val marker = LatLng(lat, lng)
            it.addMarker(MarkerOptions().position(marker).title(getString(R.string.AddressMarker)))
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))

            it.setOnMapClickListener { latLng ->
                it.clear()

                it.addMarker(MarkerOptions().position(latLng).title(getString(R.string.NewAddress)))
                it.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                binding.editTextAddress.setText("Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
            }
        }
    }


    /**
     * Function to create AlertDialog
     */
    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.PurchaseDone)
        builder.setMessage(R.string.PurchaseMessage)

        builder.setPositiveButton(R.string.Ok) {_, _ ->
            findNavController().navigate(R.id.action_checkOut_to_MainMenu)
        }

        val dialog = builder.create()
        dialog.show()
    }
}