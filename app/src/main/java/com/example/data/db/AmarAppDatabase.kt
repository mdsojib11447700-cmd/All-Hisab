package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NoteEntity::class,
        TaskEntity::class,
        ExpenseEntity::class,
        HabitEntity::class,
        AppIdeaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AmarAppDatabase : RoomDatabase() {
    abstract fun dao(): AmarAppDao

    companion object {
        @Volatile
        private var INSTANCE: AmarAppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AmarAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmarAppDatabase::class.java,
                    "amar_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AmarAppDao) {
                // Initial Welcome Notes
                dao.insertNote(
                    NoteEntity(
                        title = "স্বাগতম আমার অ্যাপে!",
                        content = "এটি আপনার একান্ত নিজের অ্যাপ। এখান থেকে আপনি নিজের পছন্দমতো থিম, ভাষা, নোট, কাজ, হিসাব, মানি ট্রান্সফার, মোবাইল রিচার্জ এবং নতুন অ্যাপ আইডিয়া তৈরি করতে পারবেন!",
                        category = "personal",
                        colorIndex = 0,
                        isPinned = true
                    )
                )
                dao.insertNote(
                    NoteEntity(
                        title = "আমার নতুন আইডিয়া",
                        content = "এখানে আপনার প্রতিদিনের চিন্তা ও পরিকল্পনা লিখে রাখুন।",
                        category = "ideas",
                        colorIndex = 1,
                        isPinned = false
                    )
                )

                // Initial Tasks
                dao.insertTask(
                    TaskEntity(
                        title = "আমার অ্যাপের থিম ও রঙ বাছাই করা",
                        priority = "high",
                        isCompleted = false,
                        dueDate = "আজ"
                    )
                )
                dao.insertTask(
                    TaskEntity(
                        title = "বিকাশ বা নগদ দিয়ে মানি ট্রান্সফার পরীক্ষা করা",
                        priority = "medium",
                        isCompleted = false,
                        dueDate = "আজ"
                    )
                )
                dao.insertTask(
                    TaskEntity(
                        title = "মোবাইলে ৫০ টাকা রিচার্জ করা",
                        priority = "medium",
                        isCompleted = false,
                        dueDate = "কাল"
                    )
                )

                // Initial Habits
                dao.insertHabit(
                    HabitEntity(
                        title = "প্রতিদিন পানি পান (২ লিটার)",
                        streak = 3,
                        isDoneToday = true,
                        iconName = "Water"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "বই পড়া বা নতুন কিছু শেখা",
                        streak = 5,
                        isDoneToday = false,
                        iconName = "Book"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "সকালের ব্যায়াম ও হাঁটা",
                        streak = 2,
                        isDoneToday = false,
                        iconName = "Fitness"
                    )
                )

                // Initial Expenses, Transfers, and Recharges
                dao.insertExpense(
                    ExpenseEntity(
                        title = "সকালের নাস্তা",
                        amount = 60.0,
                        isIncome = false,
                        category = "food",
                        transactionType = "expense"
                    )
                )
                dao.insertExpense(
                    ExpenseEntity(
                        title = "অনলাইন ফ্রিল্যান্সিং আয়",
                        amount = 5000.0,
                        isIncome = true,
                        category = "salary",
                        transactionType = "income"
                    )
                )
                dao.insertExpense(
                    ExpenseEntity(
                        title = "মানি ট্রান্সফার (বিকাশ) - 01812345678",
                        amount = 500.0,
                        isIncome = false,
                        category = "transfer",
                        transactionType = "transfer",
                        provider = "bKash",
                        recipient = "01812345678",
                        trxId = "TRX938472"
                    )
                )
                dao.insertExpense(
                    ExpenseEntity(
                        title = "মোবাইল রিচার্জ (Grameenphone) - 01711223344",
                        amount = 50.0,
                        isIncome = false,
                        category = "recharge",
                        transactionType = "recharge",
                        provider = "Grameenphone",
                        recipient = "01711223344",
                        trxId = "RCG294819"
                    )
                )

                // Initial App Idea
                dao.insertAppIdea(
                    AppIdeaEntity(
                        appName = "আমার অনলাইন দোকান (Amar Dokan)",
                        category = "E-Commerce",
                        targetAudience = "স্থানীয় ক্রেতা ও খুচরা ব্যবসায়ী",
                        features = "পণ্য ক্যাটালগ, কার্ট ও অর্ডার, ক্যাশ অন ডেলিভারি, বিকাশ পেমেন্ট ইন্টিগ্রেশন",
                        description = "ছোট ব্যবসার জন্য সহজে পণ্য প্রদর্শনী ও অর্ডার নেওয়ার অ্যান্ড্রয়েড অ্যাপ্লিকেশন।",
                        generatedPrompt = "Build an Android e-commerce application named Amar Dokan for local retail shops in Bangladesh with Jetpack Compose, featuring product catalog with categories, shopping cart, cash-on-delivery & bKash payment option, and order summary."
                    )
                )
            }
        }
    }
}
