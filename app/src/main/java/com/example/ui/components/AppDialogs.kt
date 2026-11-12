package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeColor
import com.example.data.preferences.UserProfileState
import com.example.ui.theme.*

@Composable
fun AddNoteDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String, colorIndex: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("personal") }
    var selectedColor by remember { mutableStateOf(0) }

    val categories = listOf("personal", "ideas", "work", "secret")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.t("add_note", lang), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "শিরোনাম" else "Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input")
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "নোটের বিবরণ" else "Content") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_content_input")
                )

                Text(
                    text = if (lang == AppLanguage.BANGLA) "ক্যাটেগরি" else "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(AppStrings.t(cat, lang)) },
                            modifier = Modifier.testTag("note_cat_$cat")
                        )
                    }
                }

                Text(
                    text = if (lang == AppLanguage.BANGLA) "রঙ বাছাই করুন" else "Color Accent",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == index) 2.5.dp else 1.dp,
                                    color = if (selectedColor == index) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = index }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
                        onConfirm(title, content, category, selectedColor)
                    }
                },
                enabled = title.isNotBlank() || content.isNotBlank(),
                modifier = Modifier.testTag("save_note_button")
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.t("cancel", lang))
            }
        }
    )
}

@Composable
fun AddTaskDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (title: String, priority: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("medium") }
    var dueDate by remember { mutableStateOf(if (lang == AppLanguage.BANGLA) "আজ" else "Today") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.t("add_task", lang), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "কাজের বিবরণ" else "Task title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                Text(
                    text = if (lang == AppLanguage.BANGLA) "অগ্রাধিকার" else "Priority",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("low", "medium", "high").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(AppStrings.t("priority_$p", lang)) },
                            modifier = Modifier.testTag("task_prio_$p")
                        )
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "সময় / তারিখ" else "Due Date / Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, priority, dueDate)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.t("cancel", lang))
            }
        }
    )
}

@Composable
fun AddExpenseDialog(
    lang: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Double, isIncome: Boolean, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("food") }

    val categories = if (isIncome) listOf("salary", "freelance", "gift", "other") else listOf("food", "transport", "shopping", "bills", "other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isIncome) (if (lang == AppLanguage.BANGLA) "আয় যোগ করুন" else "Add Income")
                else (if (lang == AppLanguage.BANGLA) "খরচ যোগ করুন" else "Add Expense"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isIncome,
                        onClick = {
                            isIncome = false
                            category = "food"
                        },
                        label = { Text(AppStrings.t("expense", lang)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PriorityHighColor.copy(alpha = 0.2f),
                            selectedLabelColor = PriorityHighColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isIncome,
                        onClick = {
                            isIncome = true
                            category = "salary"
                        },
                        label = { Text(AppStrings.t("income", lang)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeColor.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "বিবরণ (উদাঃ দুপুরের খাবার)" else "Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("${if (lang == AppLanguage.BANGLA) "পরিমাণ" else "Amount"} ($currency)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (lang == AppLanguage.BANGLA) "ক্যাটেগরি" else "Category",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.capitalize()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onConfirm(title, amt, isIncome, category)
                    }
                },
                enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("save_expense_button")
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.t("cancel", lang))
            }
        }
    )
}

@Composable
fun AddHabitDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (title: String, iconName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("Star") }

    val icons = listOf("Star", "Water", "Book", "Fitness", "Heart", "Code")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lang == AppLanguage.BANGLA) "নতুন অভ্যাস যোগ" else "Add Daily Habit", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "অভ্যাসের নাম (উদাঃ প্রতিদিন ২০ মিনিট বই পড়া)" else "Habit title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onConfirm(title, selectedIcon)
                },
                enabled = title.isNotBlank()
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.t("cancel", lang)) }
        }
    )
}

@Composable
fun EditProfileDialog(
    profile: UserProfileState,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        tagline: String,
        avatarId: Int,
        language: AppLanguage,
        theme: AppThemeColor,
        isDark: Boolean,
        currency: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(profile.userName) }
    var tagline by remember { mutableStateOf(profile.userTagline) }
    var avatarId by remember { mutableStateOf(profile.avatarId) }
    var selectedLang by remember { mutableStateOf(profile.language) }
    var selectedTheme by remember { mutableStateOf(profile.themeColor) }
    var isDark by remember { mutableStateOf(profile.isDarkMode) }
    var currency by remember { mutableStateOf(profile.currencySymbol) }

    val lang = profile.language

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.t("customize_profile", lang), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name & Tagline
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.t("your_name", lang)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text(AppStrings.t("your_tagline", lang)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Avatar Selection
                Text(
                    text = if (lang == AppLanguage.BANGLA) "অ্যাভাটার আইকন" else "Avatar Icon",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AvatarIcons.forEachIndexed { index, pair ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (avatarId == index) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (avatarId == index) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { avatarId = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pair.first,
                                contentDescription = pair.second,
                                tint = if (avatarId == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Theme Selection
                Text(
                    text = AppStrings.t("choose_theme", lang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppThemeColor.values().forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedTheme = theme }
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme,
                                onClick = { selectedTheme = theme }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.BANGLA) theme.labelBn else theme.labelEn,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Language
                Text(
                    text = AppStrings.t("language", lang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.values().forEach { l ->
                        FilterChip(
                            selected = selectedLang == l,
                            onClick = { selectedLang = l },
                            label = { Text(l.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Dark mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.t("dark_mode", lang), style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isDark, onCheckedChange = { isDark = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, tagline, avatarId, selectedLang, selectedTheme, isDark, currency)
                },
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.t("cancel", lang)) }
        }
    )
}

@Composable
fun QuickCalculatorDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var storedValue by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var isNewInput by remember { mutableStateOf(true) }

    fun onDigit(d: String) {
        if (isNewInput || display == "0") {
            display = d
            isNewInput = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        val current = display.toDoubleOrNull() ?: 0.0
        storedValue = current
        pendingOp = op
        isNewInput = true
    }

    fun onEquals() {
        val current = display.toDoubleOrNull() ?: 0.0
        val prev = storedValue
        if (prev != null && pendingOp != null) {
            val result = when (pendingOp) {
                "+" -> prev + current
                "-" -> prev - current
                "×" -> prev * current
                "÷" -> if (current != 0.0) prev / current else 0.0
                else -> current
            }
            display = if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
            storedValue = null
            pendingOp = null
            isNewInput = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(AppStrings.t("calculator", lang), fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Display screen
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = display,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End)
                    )
                }

                val buttons = listOf(
                    listOf("C", "±", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { btn ->
                            val isAction = btn in listOf("÷", "×", "-", "+", "=")
                            val isClear = btn in listOf("C", "±", "%")
                            Button(
                                onClick = {
                                    when (btn) {
                                        "C" -> {
                                            display = "0"
                                            storedValue = null
                                            pendingOp = null
                                            isNewInput = true
                                        }
                                        "±" -> {
                                            val v = display.toDoubleOrNull() ?: 0.0
                                            display = (-v).toString().removeSuffix(".0")
                                        }
                                        "%" -> {
                                            val v = display.toDoubleOrNull() ?: 0.0
                                            display = (v / 100).toString()
                                        }
                                        "=" -> onEquals()
                                        "÷", "×", "-", "+" -> onOp(btn)
                                        else -> onDigit(btn)
                                    }
                                },
                                modifier = Modifier
                                    .weight(if (btn == "0") 2f else 1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAction) MaterialTheme.colorScheme.primary
                                    else if (isClear) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isAction) MaterialTheme.colorScheme.onPrimary
                                    else if (isClear) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(btn, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
