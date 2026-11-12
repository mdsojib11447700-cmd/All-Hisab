package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.HabitEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AmarAppViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: AmarAppViewModel,
    onNavigateToTab: (Int) -> Unit,
    onOpenProfileSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val balance by viewModel.totalBalance.collectAsState()

    val lang = userProfile.language
    val currency = userProfile.currencySymbol
    val pendingTasks = tasks.filter { !it.isCompleted }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showCalcDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showRechargeDialog by remember { mutableStateOf(false) }
    var showAddMoneyDialog by remember { mutableStateOf(false) }

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingKey = when {
        currentHour in 5..11 -> "greeting_morning"
        currentHour in 12..16 -> "greeting_afternoon"
        else -> "greeting_evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
        }

        // Top Greeting Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val avatarIndex = userProfile.avatarId.coerceIn(0, AvatarIcons.lastIndex)
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable { onOpenProfileSettings() },
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = AvatarIcons[avatarIndex].first,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${AppStrings.t(greetingKey, lang)}, ${userProfile.userName}!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.t("welcome_sub", lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick language toggle
                    FilledTonalIconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "EN" else "বাং",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    // Profile / Settings
                    IconButton(
                        onClick = onOpenProfileSettings,
                        modifier = Modifier.testTag("home_settings_button")
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Customize Theme")
                    }
                }
            }
        }

        // Motivational Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✨ ${userProfile.userTagline}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "আজকে আপনার লক্ষ্য পূরণ করুন!" else "Make today count!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(onClick = { showCalcDialog = true }) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = "Calculator",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Money Transfer, Mobile Recharge & Add Money Quick Hub
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add Money Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showAddMoneyDialog = true }
                        .testTag("home_add_money_card"),
                    colors = CardDefaults.cardColors(containerColor = IncomeColor.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IncomeColor,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AddCard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = AppStrings.t("add_money", lang),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IncomeColor,
                            maxLines = 1
                        )
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "কার্ড • ব্যাংক" else "Card • Bank",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Transfer Banner Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showTransferDialog = true }
                        .testTag("home_money_transfer_card"),
                    colors = CardDefaults.cardColors(containerColor = BkashPink.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BkashPink,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = AppStrings.t("money_transfer", lang),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BkashPink,
                            maxLines = 1
                        )
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "বিকাশ • নগদ" else "bKash • Nagad",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Mobile Recharge Banner Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showRechargeDialog = true }
                        .testTag("home_mobile_recharge_card"),
                    colors = CardDefaults.cardColors(containerColor = GpBlue.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GpBlue,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = AppStrings.t("mobile_recharge", lang),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GpBlue,
                            maxLines = 1
                        )
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "গ্রামীন • রবি" else "GP • Robi • BL",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4 Overview Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = AppStrings.t("tasks_pending", lang),
                    value = "${pendingTasks.size}",
                    icon = Icons.Default.CheckCircleOutline,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateToTab(2) },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = AppStrings.t("balance", lang),
                    value = "$currency${balance.toInt()}",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = if (balance >= 0) IncomeColor else ExpenseColor,
                    onClick = { onNavigateToTab(3) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = AppStrings.t("habits", lang),
                    value = "${habits.count { it.isDoneToday }}/${habits.size}",
                    icon = Icons.Default.SelfImprovement,
                    color = CoralPrimary,
                    onClick = { showAddHabitDialog = true },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = AppStrings.t("notes", lang),
                    value = "${notes.size}",
                    icon = Icons.Default.Description,
                    color = VioletPrimary,
                    onClick = { onNavigateToTab(1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = AppStrings.t("quick_actions", lang),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    label = AppStrings.t("add_note", lang),
                    icon = Icons.Default.Edit,
                    onClick = { showAddNoteDialog = true },
                    modifier = Modifier.weight(1f)
                )
                QuickActionChip(
                    label = AppStrings.t("add_task", lang),
                    icon = Icons.Default.AddTask,
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.weight(1f)
                )
                QuickActionChip(
                    label = AppStrings.t("add_expense", lang),
                    icon = Icons.Default.AttachMoney,
                    onClick = { showAddExpenseDialog = true },
                    modifier = Modifier.weight(1f)
                )
                QuickActionChip(
                    label = AppStrings.t("new_app_idea", lang),
                    icon = Icons.Default.Architecture,
                    onClick = { onNavigateToTab(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Daily Habits Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌱 ${AppStrings.t("habits", lang)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { showAddHabitDialog = true }) {
                    Text(if (lang == AppLanguage.BANGLA) "+ অভ্যাস যোগ" else "+ Add Habit")
                }
            }
        }

        item {
            if (habits.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.BANGLA) "কোনো অভ্যাস যুক্ত করা নেই।" else "No habits yet. Tap + to add one!",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(habits) { habit ->
                        HabitCard(
                            habit = habit,
                            lang = lang,
                            onToggle = { viewModel.toggleHabitToday(habit) },
                            onDelete = { viewModel.deleteHabit(habit) }
                        )
                    }
                }
            }
        }

        // Today's Pending Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📝 ${if (lang == AppLanguage.BANGLA) "আজকের জরুরি কাজ" else "Today's Tasks"}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateToTab(2) }) {
                    Text(if (lang == AppLanguage.BANGLA) "সব দেখুন" else "View all")
                }
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 ${AppStrings.t("no_tasks", lang)}",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(pendingTasks.take(3)) { task ->
                HomeTaskItem(
                    task = task,
                    lang = lang,
                    onToggle = { viewModel.toggleTask(task) }
                )
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }

    // Dialogs
    if (showAddNoteDialog) {
        AddNoteDialog(
            lang = lang,
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { title, content, category, colorIndex ->
                viewModel.addNote(title, content, category, colorIndex)
                showAddNoteDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            lang = lang,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, priority, dueDate ->
                viewModel.addTask(title, priority, dueDate)
                showAddTaskDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            lang = lang,
            currency = userProfile.currencySymbol,
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { title, amount, isIncome, category ->
                viewModel.addExpense(title, amount, isIncome, category)
                showAddExpenseDialog = false
            }
        )
    }

    if (showTransferDialog) {
        MoneyTransferDialog(
            lang = lang,
            currency = currency,
            onDismiss = { showTransferDialog = false },
            onConfirm = { provider, recipient, amount, type, reference ->
                viewModel.sendMoney(provider, recipient, amount, type, reference)
            }
        )
    }

    if (showRechargeDialog) {
        MobileRechargeDialog(
            lang = lang,
            currency = currency,
            onDismiss = { showRechargeDialog = false },
            onConfirm = { operator, number, amount, connectionType ->
                viewModel.rechargeMobile(operator, number, amount, connectionType)
            }
        )
    }

    if (showAddMoneyDialog) {
        AddMoneyDialog(
            lang = lang,
            currency = currency,
            onDismiss = { showAddMoneyDialog = false },
            onConfirm = { source, accountOrCard, amount ->
                viewModel.addMoney(source, accountOrCard, amount)
            }
        )
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            lang = lang,
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { title, iconName ->
                viewModel.addHabit(title, iconName)
                showAddHabitDialog = false
            }
        )
    }

    if (showCalcDialog) {
        QuickCalculatorDialog(
            lang = lang,
            onDismiss = { showCalcDialog = false }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuickActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HabitCard(
    habit: HabitEntity,
    lang: AppLanguage,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isDoneToday) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (habit.isDoneToday) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (habit.isDoneToday) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${habit.streak} ${AppStrings.t("streak", lang)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = habit.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
        }
    }
}

@Composable
fun HomeTaskItem(
    task: TaskEntity,
    lang: AppLanguage,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (task.dueDate.isNotBlank()) {
                    Text(
                        text = "📅 ${task.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            PriorityBadge(priority = task.priority, lang = lang)
        }
    }
}
