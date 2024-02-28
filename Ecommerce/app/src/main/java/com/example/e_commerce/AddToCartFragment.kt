package com.example.e_commerce

import Adapter.CartAdapter
import Data.CartItem
import Firestore.FirestoreManager
import Model.CartViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.FragmentAddToCartBinding

class AddToCartFragment : Fragment() {

    private val cartItems = mutableListOf<CartItem>()
    private var shouldClearCartItems = false
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentAddToCartBinding
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddToCartBinding.inflate(inflater, container, false)
        val view = binding.root

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        // Set up RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recyclerViewAddToCart)
        cartAdapter = CartAdapter(cartItems) { position ->
        }
        cartAdapter.isCheckBoxVisible = true
        cartAdapter.setRemoveMode(true)
        recyclerView.adapter = cartAdapter

        /**
         * Button to add items to the cart
         */
        binding.btnAddToCart.setOnClickListener {
            val allItems = cartAdapter.getAllItems()
            for (item in allItems) {
                FirestoreManager.saveCartItem(item)
            }

            cartViewModel.clearCartItems()
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            shouldClearCartItems = true

            navigateToCart()
        }

        /**
         * Button to remove items from the recyclerView
         */
        binding.btnRemoveItem.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            for (item in selectedItems) {
                cartViewModel.removeItemFromCart(item)
                cartItems.remove(item)
            }
            cartAdapter.notifyDataSetChanged()
            cartAdapter.getSelectedItems().clear()
        }

        /**
         * Button to add items
         */
        binding.btnAddItem.setOnClickListener {
            navigateToAddItem()
        }

        /**
         * Observe changes in the ViewModel's cartItems and update the adapter
         */
        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.updateData(cartItems)
        }

        return  view
    }

    /**
     * Function for when the user comes back to this fragment (like on backPress())
     */
    override fun onResume() {
        super.onResume()

        if (shouldClearCartItems) {
            clearCartItems()
            shouldClearCartItems = false
        }
    }

    /**
     * Clear items from recyclerView
     */
    private fun clearCartItems() {

        cartItems.clear()
        cartAdapter.notifyDataSetChanged()
    }

    /**
     * Function to navigate from addCart to addItem
     */
    private fun navigateToAddItem() {
        findNavController().navigate(R.id.action_addToCart_to_addItem)
    }

    /**
     * Function to navigate from addCart to cart
     */
    private fun navigateToCart() {
        findNavController().navigate(R.id.action_addToCart_to_cart)
    }
}