package Adapter

import Data.CartItem
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.squareup.picasso.Picasso

/**
 * Adapter for the RecyclerView displaying cart items
 *
 * @param cartItems List of cart items to be displayed
 * @param onItemClick Callback function triggered when an item is clicked
 */
class CartAdapter(
    var cartItems: MutableList<CartItem>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // List to keep track of the selected items in remove mode
    private val selectedItems = mutableListOf<CartItem>()

    // Flag indicating whether the adapter is in remove mode
    private var isInRemoveMode: Boolean = false

    //Flag indicating whether checkbox should be visible
    var isCheckBoxVisible: Boolean = false

    /**
     * ViewHolder for the cart item views
     *
     * @param itemView The inflated view for a cart item
     */
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val checkBoxItem: CheckBox = itemView.findViewById(R.id.checkBoxItem)
    }

    /**
     * Inflates the layout for a cart item view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    /**
     * Binds the data to the views in each cart item
     */
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        holder.itemName.text = currentItem.name
        holder.itemQuantity.text = "${holder.itemView.context.getString(R.string.Quantity_)}: ${currentItem.quantity}"

        // Loading image using picasso library
        Picasso.get().load(currentItem.imageUrl).into(holder.itemImage)

        // Config checkbox visibility and status
        holder.checkBoxItem.visibility = if (isCheckBoxVisible &&  isInRemoveMode) View.VISIBLE else View.GONE
        holder.checkBoxItem.isChecked  = isInRemoveMode && selectedItems.contains(currentItem)

        holder.itemView.setOnClickListener{
            onItemClick(position)
        }

        holder.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(currentItem)
            } else {
                selectedItems.remove(currentItem)
            }
        }
    }

    /**
     * Returns the total number of items in the data set
     */
    override fun getItemCount(): Int {
        return cartItems.size
    }

    /**
     * Sets the remove mode for the updates
     *
     * @param enabled True if remove mode is enabled, false otherwise
     */
    fun setRemoveMode(enabled: Boolean) {
        isInRemoveMode = enabled
        notifyDataSetChanged()
        Log.d("CartAdapter", "Remove mode set to: $isInRemoveMode")
        Log.d("CartAdapter", "CheckBox visibility: $isCheckBoxVisible")
    }

    /**
     * Updates the data set with new cart items
     *
     * @param newCartItems List of new cart items
     */
    fun updateData(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }

    /**
     * Returns a list of all cart items
     */
    fun getAllItems(): List<CartItem> {
        return cartItems.toList()
    }

    /**
     * Returns a list of selected cart items in remove mode
     */
    fun  getSelectedItems(): MutableList<CartItem> {
        return selectedItems
    }
}