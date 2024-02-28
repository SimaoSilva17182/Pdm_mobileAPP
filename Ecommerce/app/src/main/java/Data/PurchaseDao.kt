package Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) interface for interacting with the 'purchases' table in Room database
 */
@Dao
interface PurchaseDao {
    // Query to get all purchases from the 'purchases' table
    @Query("SELECT * FROM purchases")
    suspend fun getAllPurchases(): List<PurchaseEntity>

    // Insert a purchase into the 'purchases' table with conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity)
}
