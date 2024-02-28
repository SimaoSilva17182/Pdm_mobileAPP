package Model

import Data.CartItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * ViewModel for managing the cart items in the RecyclerView
 */
class CartViewModel : ViewModel() {

    // LiveData to observe changes in the cart items
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    /**
     * Function to add a cart item to the ViewModel
     */
    fun addToCart(cartItem: CartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.add(cartItem)
        _cartItems.value = currentList
    }

    /**
     * Function to remove a cart item from the ViewModel
     */
    fun removeItemFromCart(cartItem: CartItem) {
        val currentList = _cartItems.value.orEmpty().toMutableList()
        currentList.remove(cartItem)
        _cartItems.value = currentList
    }

    /**
     * Function to clear all cart items from the ViewModel
     */
    fun clearCartItems() {
        _cartItems.value = emptyList()
    }

}