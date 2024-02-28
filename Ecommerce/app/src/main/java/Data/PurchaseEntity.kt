package Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Data class representing a purchase entity stored in Room database
 */
@Entity(tableName = "purchases")
@TypeConverters(Converters::class)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val userAddress: String,
    val userInfo: String,
    val cartItems: List<CartItem>?,
    val purchaseDate: Long = System.currentTimeMillis()
)