package Firestore

import Adapter.CartAdapter
import Data.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreManager {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Function use to save items into the firebase
     */
    fun saveCartItem(item: CartItem) {
        val userId = auth.currentUser!!.uid
        val cartItemsCollection = firestore.collection("users").document(userId).collection("cart_items")

        val data = hashMapOf(
            "name" to item.name,
            "quantity" to item.quantity,
            "imageUrl" to item.imageUrl
        )

        cartItemsCollection.add(data)
            .addOnSuccessListener { documentReference ->
                println("Item saved with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                println("Error saving item: $exception")
            }
    }

    /**
     * Function to remove all cart items for a given user
     * This is used when the user completes a purchase, ensuring an empty cart for future use
     */
    fun removeAllCartItems(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser!!.uid
        val cartItemsCollection = firestore.collection("users").document(userId).collection("cart_items")

        cartItemsCollection
            .get()
            .addOnSuccessListener { result ->
                val batch = firestore.batch()
                for (document in result) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    .addOnFailureListener { exception ->
                        onFailure.invoke(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    /**
     * Function use to remove only selected items
     */
    fun removeCartItem(adapter: CartAdapter,
                       items: List<CartItem>,
                       onSuccess: () -> Unit,
                       onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser!!.uid
        val cartItemsCollection = firestore.collection("users").document(userId).collection("cart_items")

        for (item in items) {
            cartItemsCollection
                .whereEqualTo("name",item.name)
                .whereEqualTo("quantity", item.quantity)
                .whereEqualTo("imageUrl", item.imageUrl)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                println("Item deleted from Firestore")
                                onSuccess.invoke()
                                // Remove the item from the adapter and update
                                adapter.cartItems.remove(item)
                                adapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener{e ->
                                onFailure.invoke(e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure.invoke(exception)
                }
        }
    }
}