package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.ExpenseEntity
import com.example.ui.components.*
import com.example.ui.theme.ExpenseColor
import com.example.ui.theme.IncomeColor
import com.example.ui.viewmodel.AmarAppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: AmarAppViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()
    val balance by viewModel.totalBalance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()
    val currentFilter by viewModel.transactionFilter.collectAsState()

    val lang = userProfile.language
    val currency = userProfile.currencySymbol

    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showRechargeDialog by remember { mutableStateOf(false) }
    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var selectedItemForReceipt by remember { mutableStateOf<ExpenseEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        AppStrings.t("expenses", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_expense_top_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Net Balance Big Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = AppStrings.t("balance", lang),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$currency${"%,.2f".format(balance)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (balance >= 0) IncomeColor else ExpenseColor
                            )

                            FilledTonalButton(
                                onClick = { showAddMoneyDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = IncomeColor.copy(alpha = 0.15f),
                                    contentColor = IncomeColor
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("balance_add_money_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(AppStrings.t("add_money", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Income
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = IncomeColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = IncomeColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        AppStrings.t("income", lang),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "+$currency${"%,.0f".format(income)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = IncomeColor
                                    )
                                }
                            }

                            // Expense
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = ExpenseColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = ExpenseColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        AppStrings.t("expense", lang),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "-$currency${"%,.0f".format(expense)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Three Prominent MFS Feature Action Cards: Add Money, Money Transfer & Mobile Recharge
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
                            .testTag("wallet_add_money_card"),
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
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IncomeColor,
                                maxLines = 1
                            )
                            Text(
                                text = if (lang == AppLanguage.BANGLA) "কার্ড • ব্যাংক" else "Card • Bank",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Money Transfer Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showTransferDialog = true }
                            .testTag("transfer_quick_card"),
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
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BkashPink,
                                maxLines = 1
                            )
                            Text(
                                text = if (lang == AppLanguage.BANGLA) "বিকাশ • নগদ" else "bKash • Nagad",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Mobile Recharge Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showRechargeDialog = true }
                            .testTag("recharge_quick_card"),
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
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GpBlue,
                                maxLines = 1
                            )
                            Text(
                                text = if (lang == AppLanguage.BANGLA) "গ্রামীন • রবি" else "GP • Robi • BL",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Transaction Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "all" to AppStrings.t("all", lang),
                        "add_money" to AppStrings.t("add_money", lang),
                        "transfer" to AppStrings.t("transfer", lang),
                        "recharge" to AppStrings.t("recharge", lang),
                        "expense" to AppStrings.t("expense", lang),
                        "income" to AppStrings.t("income", lang)
                    )

                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = currentFilter == key,
                            onClick = { viewModel.transactionFilter.value = key },
                            label = { Text(label) },
                            modifier = Modifier.testTag("filter_$key")
                        )
                    }
                }
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.BANGLA) "লেনদেনের ইতিহাস" else "Transaction History",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredExpenses.size} ${if (lang == AppLanguage.BANGLA) "টি রেকর্ড" else "records"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Transactions list
            if (filteredExpenses.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = AppStrings.t("no_expenses", lang),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredExpenses) { item ->
                    TransactionRowItem(
                        item = item,
                        currency = currency,
                        lang = lang,
                        onClick = {
                            if (item.trxId.isNotBlank()) {
                                selectedItemForReceipt = item
                            }
                        },
                        onDelete = { viewModel.deleteExpense(item) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(72.dp))
            }
        }
    }

    // Standard Expense Dialog
    if (showAddDialog) {
        AddExpenseDialog(
            lang = lang,
            currency = currency,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, isIncome, category ->
                viewModel.addExpense(title, amount, isIncome, category)
                showAddDialog = false
            }
        )
    }

    // Money Transfer Dialog
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

    // Mobile Recharge Dialog
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

    // Add Money Dialog
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

    // Digital Receipt Viewer
    selectedItemForReceipt?.let { item ->
        val receiptTitle = when (item.transactionType) {
            "add_money" -> if (lang == AppLanguage.BANGLA) "টাকা যোগ করার রসিদ" else "Add Money Receipt"
            "transfer" -> if (lang == AppLanguage.BANGLA) "মানি ট্রান্সফার রসিদ" else "Money Transfer Receipt"
            else -> if (lang == AppLanguage.BANGLA) "মোবাইল রিচার্জ রসিদ" else "Recharge Receipt"
        }
        DigitalReceiptDialog(
            trxId = item.trxId,
            title = receiptTitle,
            provider = if (item.provider.isNotBlank()) item.provider else item.category.capitalize(),
            providerColor = getProviderColor(item.provider),
            recipient = if (item.recipient.isNotBlank()) item.recipient else "-",
            amount = item.amount,
            currency = currency,
            lang = lang,
            onDismiss = { selectedItemForReceipt = null }
        )
    }
}

@Composable
fun TransactionRowItem(
    item: ExpenseEntity,
    currency: String,
    lang: AppLanguage,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(item.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    val providerColor = remember(item.provider, item.category) {
        if (item.provider.isNotBlank()) getProviderColor(item.provider)
        else if (item.isIncome) IncomeColor else ExpenseColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = providerColor.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when (item.transactionType) {
                        "transfer" -> Icons.Default.Send
                        "recharge" -> Icons.Default.PhoneAndroid
                        "add_money" -> Icons.Default.AddCard
                        else -> if (item.isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = providerColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.provider.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = providerColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.provider,
                                color = providerColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                    }

                    if (item.trxId.isNotBlank()) {
                        Text(
                            text = item.trxId,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (item.isIncome) "+" else "-"}$currency${"%,.2f".format(item.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isIncome) IncomeColor else ExpenseColor
                )
                if (item.trxId.isNotBlank()) {
                    Text(
                        text = if (lang == AppLanguage.BANGLA) "রসিদ দেখুন" else "View slip",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
