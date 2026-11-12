package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "personal", "ideas", "work", "secret"
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val priority: String, // "high", "medium", "low"
    val isCompleted: Boolean = false,
    val dueDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val isIncome: Boolean = false,
    val category: String, // "food", "transport", "shopping", "bills", "salary", "transfer", "recharge", "other"
    val transactionType: String = "expense", // "expense", "transfer", "recharge", "income"
    val provider: String = "", // "bKash", "Nagad", "Rocket", "Upay", "Bank", "Grameenphone", "Banglalink", "Robi", "Airtel", "Teletalk"
    val recipient: String = "", // Phone number or Bank account
    val trxId: String = "", // Transaction ID (e.g. TRX192847 or RCG928371)
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val streak: Int = 0,
    val isDoneToday: Boolean = false,
    val iconName: String = "Star",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_ideas")
data class AppIdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val category: String,
    val targetAudience: String,
    val features: String, // comma-separated or text
    val description: String,
    val generatedPrompt: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AppLanguage(val code: String, val displayName: String) {
    BANGLA("bn", "বাংলা"),
    ENGLISH("en", "English")
}

enum class AppThemeColor(val id: String, val labelBn: String, val labelEn: String) {
    ROYAL_INDIGO("indigo", "রাজকীয় নীল", "Royal Indigo"),
    EMERALD_GREEN("emerald", "পান্না সবুজ", "Emerald Green"),
    SUNSET_CORAL("coral", "সূর্যাস্ত কোরাল", "Sunset Coral"),
    CYBER_VIOLET("violet", "সাইবার ভায়োলেট", "Cyber Violet"),
    MIDNIGHT_CYAN("cyan", "মিডনাইট সিয়ান", "Midnight Cyan")
}
