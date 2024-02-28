package Data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter class to convert complex data types,
 * such as List<CartItem>, to and from their representation in Room database
 */
class Converters {

    // Convert a List<CartItem> to its JSON representation
    @TypeConverter
    fun fromCartItemList(cartItems: List<CartItem>?): String? {
        if (cartItems == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.toJson(cartItems, type)
    }

    // Convert a JSON representation to a List<CartItem>
    @TypeConverter
    fun toCartItemList(cartItemsString: String?): List<CartItem>? {
        if (cartItemsString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(cartItemsString, type)
    }
}