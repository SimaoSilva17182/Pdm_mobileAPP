package Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database class for the application, defining the entities and version
 */
@Database(entities = [PurchaseEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    // Abstract function to get the DAO (Data Access Object) for PurchaseEntity
    abstract fun purchaseDao(): PurchaseDao

    // Companion object to implement the Singleton pattern for database instance
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Function to get the database instance using Singleton pattern
        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}