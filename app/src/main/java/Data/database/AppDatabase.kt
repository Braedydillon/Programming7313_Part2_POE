package Data.database

import Data.Expense
import Data.dao.ExpenseDao
import Data.dao.MonthlyDao
import Data.dao.UserDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import Data.MonthlyGoal
import Data.User


@Database(
    entities = [User::class, Expense::class, MonthlyGoal::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
   abstract fun userDao(): UserDao
   abstract fun monthlyDao(): MonthlyDao
   abstract fun expenseDao(): ExpenseDao

   companion object{
       @Volatile
       private var INSTANCE: AppDatabase? = null

       fun getDatabase(context: Context): AppDatabase {
           return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tracker_database"
            ).build()
            INSTANCE = instance
            instance


           }

       }
   }

}

