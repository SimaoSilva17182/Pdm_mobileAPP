package com.example.e_commerce

import Adapter.CartAdapter
import Data.CartItem
import Firestore.FirestoreManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(mutableListOf(), this::onItemClick)
        recyclerView.adapter = cartAdapter

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch cart items from Firestore and update the adapter
        fetchCartItemsFromFirestore()

        /**
         * Button to make checkboxes visible for item removal
         */
        binding.btnRemove.setOnClickListener {
            cartAdapter.isCheckBoxVisible = true
            cartAdapter.setRemoveMode(true)
            binding.btnRemove.visibility = View.GONE
            binding.btnConfirmRemove.visibility = View.VISIBLE
        }

        /**
         * Button to confirm removal of items
         */
        binding.btnConfirmRemove.setOnClickListener{
            val selectedItems = cartAdapter.getSelectedItems()

            FirestoreManager.removeCartItem(cartAdapter, selectedItems,onSuccess = {
            },
                onFailure = {exception ->
                    Log.e("CartFragment", "Error removing items from Firestore")
                })

            // Reset checkbox visibility and buttons
            cartAdapter.isCheckBoxVisible = false
            cartAdapter.setRemoveMode(false)
            binding.btnRemove.visibility = View.VISIBLE
            binding.btnConfirmRemove.visibility = View.GONE
        }

        /**
         * Button to navigate from cart to checkout with items bundled
         */
        binding.btnBuy.setOnClickListener {
            val cartItems = cartAdapter.getAllItems()

            val checkOutFragment = CheckOutFragment()

            val bundle = Bundle().apply {
                putParcelableArrayList("cartItems", ArrayList(cartItems))
            }

            checkOutFragment.arguments = bundle

            findNavController().navigate(R.id.action_cart_to_checkOut, bundle)
        }

        return view
    }

    /**
     * Function to fetch cart items from Firestore and update the adapter
    */
    private fun fetchCartItemsFromFirestore() {
        val userId = auth.currentUser!!.uid
        firestore.collection("users").document(userId).collection("cart_items")
            .get()
            .addOnSuccessListener { result ->
                val cartItems = result.documents.map { document ->
                    val name = document.getString("name") ?: ""
                    val quantity = document.getLong("quantity") ?: 0
                    val imageUrl = document.getString("imageUrl") ?: ""
                    CartItem(name, quantity.toInt(), imageUrl)
                }

                cartAdapter.updateData(cartItems)
            }
            .addOnFailureListener { exception ->
                Log.e("CartFragment", "Error fetching data from Firestore", exception)
            }
    }

    /**
     * Function for clicking on items in the recyclerView
    */
    private fun onItemClick(position: Int) {
    }
}