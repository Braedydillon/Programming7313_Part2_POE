package Data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import Data.Expense
// import com.example.tracker.data.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>


}