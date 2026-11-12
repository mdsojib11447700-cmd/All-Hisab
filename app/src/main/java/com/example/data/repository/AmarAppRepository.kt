package com.example.data.repository

import com.example.data.db.AmarAppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AmarAppRepository(private val dao: AmarAppDao) {
    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)
    suspend fun deleteNoteById(id: Long) = dao.deleteNoteById(id)

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = dao.deleteTaskById(id)

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = dao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = dao.deleteExpenseById(id)

    // Habits
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()
    suspend fun insertHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)

    // App Ideas
    val allAppIdeas: Flow<List<AppIdeaEntity>> = dao.getAllAppIdeas()
    suspend fun insertAppIdea(idea: AppIdeaEntity) = dao.insertAppIdea(idea)
    suspend fun deleteAppIdea(idea: AppIdeaEntity) = dao.deleteAppIdea(idea)
}
