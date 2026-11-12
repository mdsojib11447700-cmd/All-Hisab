package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AmarAppDatabase
import com.example.data.model.*
import com.example.data.preferences.UserPreferencesManager
import com.example.data.preferences.UserProfileState
import com.example.data.repository.AmarAppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AmarAppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AmarAppDatabase.getDatabase(application, viewModelScope)
    private val repository = AmarAppRepository(database.dao())
    private val prefsManager = UserPreferencesManager(application)

    val userProfile: StateFlow<UserProfileState> = prefsManager.userProfile

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appIdeas: StateFlow<List<AppIdeaEntity>> = repository.allAppIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter states
    val noteSearchQuery = MutableStateFlow("")
    val selectedNoteCategory = MutableStateFlow("all")
    val taskFilter = MutableStateFlow("all") // all, pending, completed

    val filteredNotes: StateFlow<List<NoteEntity>> = combine(
        notes,
        noteSearchQuery,
        selectedNoteCategory
    ) { list, query, cat ->
        list.filter { note ->
            val matchesQuery = query.isEmpty() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
            val matchesCat = cat == "all" || note.category.equals(cat, ignoreCase = true)
            matchesQuery && matchesCat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        tasks,
        taskFilter
    ) { list, filter ->
        when (filter) {
            "pending" -> list.filter { !it.isCompleted }
            "completed" -> list.filter { it.isCompleted }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial summaries
    val totalBalance: StateFlow<Double> = expenses.map { list ->
        val income = list.filter { it.isIncome }.sumOf { it.amount }
        val expense = list.filter { !it.isIncome }.sumOf { it.amount }
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = expenses.map { list ->
        list.filter { it.isIncome }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = expenses.map { list ->
        list.filter { !it.isIncome }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val transactionFilter = MutableStateFlow("all") // all, expense, transfer, recharge, income

    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(
        expenses,
        transactionFilter
    ) { list, filter ->
        when (filter) {
            "expense" -> list.filter { !it.isIncome && it.transactionType == "expense" }
            "transfer" -> list.filter { it.transactionType == "transfer" }
            "recharge" -> list.filter { it.transactionType == "recharge" }
            "add_money" -> list.filter { it.transactionType == "add_money" }
            "income" -> list.filter { it.isIncome }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notes Actions
    fun addNote(title: String, content: String, category: String, colorIndex: Int = 0) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title.trim(),
                    content = content.trim(),
                    category = category,
                    colorIndex = colorIndex
                )
            )
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Tasks Actions
    fun addTask(title: String, priority: String, dueDate: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title.trim(),
                    priority = priority,
                    dueDate = dueDate.trim()
                )
            )
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Expense Actions
    fun addExpense(title: String, amount: Double, isIncome: Boolean, category: String) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title.trim(),
                    amount = amount,
                    isIncome = isIncome,
                    category = category
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Money Transfer Actions
    fun sendMoney(
        provider: String,
        recipient: String,
        amount: Double,
        transferType: String,
        reference: String
    ): String {
        val trxId = "TRX" + (100000..999999).random()
        val typeLabel = if (transferType == "cash_out") "ক্যাশ আউট" else "টাকা পাঠানো"
        val refPart = if (reference.isNotBlank()) " ($reference)" else ""
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = "মানি ট্রান্সফার ($provider - $typeLabel)$refPart",
                    amount = amount,
                    isIncome = false,
                    category = "transfer",
                    transactionType = "transfer",
                    provider = provider,
                    recipient = recipient,
                    trxId = trxId
                )
            )
        }
        return trxId
    }

    // Mobile Recharge Actions
    fun rechargeMobile(
        operator: String,
        number: String,
        amount: Double,
        connectionType: String
    ): String {
        val trxId = "RCG" + (100000..999999).random()
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = "মোবাইল রিচার্জ ($operator $connectionType)",
                    amount = amount,
                    isIncome = false,
                    category = "recharge",
                    transactionType = "recharge",
                    provider = operator,
                    recipient = number,
                    trxId = trxId
                )
            )
        }
        return trxId
    }

    // Add Money Actions
    fun addMoney(
        source: String,
        accountOrCard: String,
        amount: Double
    ): String {
        val trxId = "ADM" + (100000..999999).random()
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = "টাকা যোগ (অ্যাড মানি - $source)",
                    amount = amount,
                    isIncome = true,
                    category = "add_money",
                    transactionType = "add_money",
                    provider = source,
                    recipient = accountOrCard,
                    trxId = trxId
                )
            )
        }
        return trxId
    }

    // Habit Actions
    fun addHabit(title: String, iconName: String = "Star") {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title.trim(),
                    iconName = iconName
                )
            )
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val newDone = !habit.isDoneToday
            val newStreak = if (newDone) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)
            repository.updateHabit(habit.copy(isDoneToday = newDone, streak = newStreak))
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // App Idea Studio Actions
    fun saveAppIdea(
        appName: String,
        category: String,
        targetAudience: String,
        features: String,
        description: String
    ) {
        val prompt = generatePromptText(appName, category, targetAudience, features, description)
        viewModelScope.launch {
            repository.insertAppIdea(
                AppIdeaEntity(
                    appName = appName.trim(),
                    category = category,
                    targetAudience = targetAudience.trim(),
                    features = features.trim(),
                    description = description.trim(),
                    generatedPrompt = prompt
                )
            )
        }
    }

    fun deleteAppIdea(idea: AppIdeaEntity) {
        viewModelScope.launch {
            repository.deleteAppIdea(idea)
        }
    }

    private fun generatePromptText(
        appName: String,
        category: String,
        targetAudience: String,
        features: String,
        description: String
    ): String {
        return "Build a comprehensive, modern Android application named '$appName' in the '$category' category for '$targetAudience'. " +
                "Key features include: $features. " +
                "App overview: $description. " +
                "Craft it using Jetpack Compose, Material 3 design, edge-to-edge support, responsive layouts, and robust local persistence with Room."
    }

    // User Profile Actions
    fun updateProfile(
        userName: String,
        userTagline: String,
        avatarId: Int,
        language: AppLanguage,
        themeColor: AppThemeColor,
        isDarkMode: Boolean,
        currencySymbol: String
    ) {
        prefsManager.updateProfile(
            userName,
            userTagline,
            avatarId,
            language,
            themeColor,
            isDarkMode,
            currencySymbol
        )
    }

    fun toggleLanguage() = prefsManager.toggleLanguage()
    fun setThemeColor(theme: AppThemeColor) = prefsManager.setThemeColor(theme)
    fun toggleDarkMode() = prefsManager.toggleDarkMode()
}
