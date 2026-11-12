package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.IncomeColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MfsProvider(
    val name: String,
    val nameBn: String,
    val color: Color
)

val MfsProviders = listOf(
    MfsProvider("bKash", "বিকাশ", BkashPink),
    MfsProvider("Nagad", "নগদ", NagadOrange),
    MfsProvider("Rocket", "রকেট", RocketPurple),
    MfsProvider("Upay", "উপায়", UpayYellow),
    MfsProvider("Bank", "ব্যাংক", BankBlue)
)

data class MobileOperator(
    val id: String,
    val name: String,
    val nameBn: String,
    val color: Color,
    val prefixes: List<String>
)

val MobileOperators = listOf(
    MobileOperator("gp", "Grameenphone", "গ্রামীনফোন", GpBlue, listOf("017", "013")),
    MobileOperator("bl", "Banglalink", "বাংলালিংক", BanglalinkOrange, listOf("019", "014")),
    MobileOperator("robi", "Robi", "রবি", RobiRed, listOf("018")),
    MobileOperator("airtel", "Airtel", "এয়ারটেল", AirtelRed, listOf("016")),
    MobileOperator("teletalk", "Teletalk", "টেলিটক", TeletalkGreen, listOf("015"))
)

data class RechargeBundle(
    val title: String,
    val validity: String,
    val price: Double
)

val SampleBundles = listOf(
    RechargeBundle("5 GB ইন্টারনেট", "৩ দিন", 69.0),
    RechargeBundle("20 GB + 300 মিনিট", "৩০ দিন", 399.0),
    RechargeBundle("150 মিনিট অল-নেট", "৭ দিন", 99.0),
    RechargeBundle("1 GB আনলিমিটেড", "১ দিন", 29.0),
    RechargeBundle("40 GB মেগা প্যাক", "৩০ দিন", 499.0)
)

@Composable
fun MoneyTransferDialog(
    lang: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (provider: String, recipient: String, amount: Double, type: String, reference: String) -> String
) {
    val context = LocalContext.current
    var selectedProvider by remember { mutableStateOf(MfsProviders[0]) }
    var transferType by remember { mutableStateOf("send_money") } // send_money, cash_out
    var recipientNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var referenceText by remember { mutableStateOf("") }

    // Step 2 PIN
    var isPinStep by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }

    // Receipt step
    var receiptTrxId by remember { mutableStateOf<String?>(null) }

    val quickRecipients = listOf(
        "আম্মু (01711223344)" to "01711223344",
        "আব্বু (01819202122)" to "01819202122",
        "বন্ধু সোয়েব (01912345678)" to "01912345678"
    )

    if (receiptTrxId != null) {
        DigitalReceiptDialog(
            trxId = receiptTrxId!!,
            title = if (lang == AppLanguage.BANGLA) "মানি ট্রান্সফার সফল!" else "Money Transfer Successful!",
            provider = selectedProvider.name,
            providerColor = selectedProvider.color,
            recipient = recipientNumber,
            amount = amountText.toDoubleOrNull() ?: 0.0,
            currency = currency,
            lang = lang,
            onDismiss = onDismiss
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = selectedProvider.color.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = selectedProvider.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (isPinStep) AppStrings.t("enter_pin", lang) else AppStrings.t("money_transfer", lang),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (isPinStep) {
                // PIN Confirmation Step
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${selectedProvider.name} ${if (transferType == "cash_out") "ক্যাশ আউট" else "টাকা পাঠানো"}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = selectedProvider.color
                            )
                            Text(
                                text = "${if (lang == AppLanguage.BANGLA) "প্রাপক:" else "Recipient:"} $recipientNumber",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${if (lang == AppLanguage.BANGLA) "পরিমাণ:" else "Amount:"} $currency$amountText",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 5) pinText = it.filter { c -> c.isDigit() } },
                        label = { Text(if (lang == AppLanguage.BANGLA) "৪ বা ৫ ডিজিটের পিন নম্বর" else "Security PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_pin_input")
                    )
                }
            } else {
                // Input Step
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Provider Selector
                    Text(
                        text = AppStrings.t("select_provider", lang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MfsProviders) { provider ->
                            val isSelected = selectedProvider == provider
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedProvider = provider }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) provider.color else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = if (isSelected) provider.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.BANGLA) provider.nameBn else provider.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) provider.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Transfer Type (Send Money / Cash Out)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = transferType == "send_money",
                            onClick = { transferType = "send_money" },
                            label = { Text(AppStrings.t("send_money", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = transferType == "cash_out",
                            onClick = { transferType = "cash_out" },
                            label = { Text(AppStrings.t("cash_out", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Recipient Number
                    OutlinedTextField(
                        value = recipientNumber,
                        onValueChange = { recipientNumber = it },
                        label = { Text(AppStrings.t("recipient_number", lang)) },
                        placeholder = { Text("01XXXXXXXXX") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_recipient_input")
                    )

                    // Quick Recipient Contacts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickRecipients.take(2).forEach { (label, num) ->
                            SuggestionChip(
                                onClick = { recipientNumber = num },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Amount
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("${if (lang == AppLanguage.BANGLA) "টাকার পরিমাণ" else "Amount"} ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_amount_input")
                    )

                    // Quick Amount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("100", "500", "1000", "2000").forEach { quickAmt ->
                            SuggestionChip(
                                onClick = { amountText = quickAmt },
                                label = { Text("+$currency$quickAmt", fontSize = 11.sp) }
                            )
                        }
                    }

                    // Reference
                    OutlinedTextField(
                        value = referenceText,
                        onValueChange = { referenceText = it },
                        label = { Text(if (lang == AppLanguage.BANGLA) "রেফারেন্স (ঐচ্ছিক)" else "Reference (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isPinStep) {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (recipientNumber.isNotBlank() && amt > 0) {
                            isPinStep = true
                        }
                    } else {
                        // Confirm with PIN
                        if (pinText.length >= 4) {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val trx = onConfirm(
                                selectedProvider.name,
                                recipientNumber,
                                amt,
                                transferType,
                                referenceText
                            )
                            receiptTrxId = trx
                        } else {
                            Toast.makeText(
                                context,
                                if (lang == AppLanguage.BANGLA) "সঠিক পিন দিন" else "Enter valid PIN",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                enabled = if (!isPinStep) {
                    recipientNumber.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                } else {
                    pinText.length >= 4
                },
                colors = ButtonDefaults.buttonColors(containerColor = selectedProvider.color),
                modifier = Modifier.testTag("transfer_action_button")
            ) {
                Text(
                    if (isPinStep) AppStrings.t("confirm_transfer", lang)
                    else (if (lang == AppLanguage.BANGLA) "এগিয়ে যান" else "Proceed")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (isPinStep) isPinStep = false else onDismiss()
                }
            ) {
                Text(if (isPinStep) (if (lang == AppLanguage.BANGLA) "পিছনে যান" else "Back") else AppStrings.t("cancel", lang))
            }
        }
    )
}

@Composable
fun MobileRechargeDialog(
    lang: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (operator: String, number: String, amount: Double, connectionType: String) -> String
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var selectedOperator by remember { mutableStateOf(MobileOperators[0]) }
    var connectionType by remember { mutableStateOf("Prepaid") } // Prepaid, Postpaid, Skitto
    var amountText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Fast Amount, 1: Bundles

    // PIN Step
    var isPinStep by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }

    // Receipt Step
    var receiptTrxId by remember { mutableStateOf<String?>(null) }

    // Auto-detect operator from number prefix
    LaunchedEffect(phoneNumber) {
        val clean = phoneNumber.trim()
        if (clean.length >= 3) {
            val prefix = clean.substring(0, 3)
            val matched = MobileOperators.find { it.prefixes.contains(prefix) }
            if (matched != null) {
                selectedOperator = matched
            }
        }
    }

    if (receiptTrxId != null) {
        DigitalReceiptDialog(
            trxId = receiptTrxId!!,
            title = if (lang == AppLanguage.BANGLA) "মোবাইল রিচার্জ সফল!" else "Mobile Recharge Successful!",
            provider = selectedOperator.name,
            providerColor = selectedOperator.color,
            recipient = phoneNumber,
            amount = amountText.toDoubleOrNull() ?: 0.0,
            currency = currency,
            lang = lang,
            onDismiss = onDismiss
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = selectedOperator.color.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = selectedOperator.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (isPinStep) AppStrings.t("enter_pin", lang) else AppStrings.t("mobile_recharge", lang),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (isPinStep) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${selectedOperator.name} ($connectionType)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = selectedOperator.color
                            )
                            Text(
                                text = "${if (lang == AppLanguage.BANGLA) "মোবাইল নম্বর:" else "Mobile:"} $phoneNumber",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${if (lang == AppLanguage.BANGLA) "রিচার্জ পরিমাণ:" else "Amount:"} $currency$amountText",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 5) pinText = it.filter { c -> c.isDigit() } },
                        label = { Text(if (lang == AppLanguage.BANGLA) "৪ বা ৫ ডিজিটের গোপন পিন" else "Security PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharge_pin_input")
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mobile Number Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(if (lang == AppLanguage.BANGLA) "১১ ডিজিটের মোবাইল নম্বর" else "Mobile Number") },
                        placeholder = { Text("017XXXXXXXX") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        trailingIcon = {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = selectedOperator.color.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = selectedOperator.nameBn,
                                    color = selectedOperator.color,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharge_phone_input")
                    )

                    // Operator Selector Chips
                    Text(
                        text = AppStrings.t("select_operator", lang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MobileOperators) { op ->
                            val isSelected = selectedOperator == op
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedOperator = op }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) op.color else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (isSelected) op.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.BANGLA) op.nameBn else op.name,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) op.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Connection Type (Prepaid / Postpaid / Skitto)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Prepaid" to "prepaid", "Postpaid" to "postpaid", "Skitto" to "skitto").forEach { (type, stringKey) ->
                            FilterChip(
                                selected = connectionType == type,
                                onClick = { connectionType = type },
                                label = { Text(AppStrings.t(stringKey, lang), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Mode Tabs: Fast Amount vs Special Packs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text(if (lang == AppLanguage.BANGLA) "টাকা" else "Amount") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text(if (lang == AppLanguage.BANGLA) "অফার প্যাক" else "Packs") }
                        )
                    }

                    if (selectedTab == 0) {
                        // Amount input
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("${if (lang == AppLanguage.BANGLA) "রিচার্জের পরিমাণ" else "Recharge Amount"} ($currency)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recharge_amount_input")
                        )

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("20", "50", "100", "200", "500").forEach { quickAmt ->
                                SuggestionChip(
                                    onClick = { amountText = quickAmt },
                                    label = { Text("$currency$quickAmt", fontSize = 11.sp) }
                                )
                            }
                        }
                    } else {
                        // Bundles List
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            SampleBundles.forEach { bundle ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            amountText = bundle.price.toInt().toString()
                                            selectedTab = 0
                                        },
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(bundle.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(bundle.validity, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            "$currency${bundle.price.toInt()}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = selectedOperator.color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isPinStep) {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (phoneNumber.isNotBlank() && amt > 0) {
                            isPinStep = true
                        }
                    } else {
                        if (pinText.length >= 4) {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val trx = onConfirm(
                                selectedOperator.name,
                                phoneNumber,
                                amt,
                                connectionType
                            )
                            receiptTrxId = trx
                        } else {
                            Toast.makeText(
                                context,
                                if (lang == AppLanguage.BANGLA) "সঠিক পিন দিন" else "Enter valid PIN",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                enabled = if (!isPinStep) {
                    phoneNumber.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                } else {
                    pinText.length >= 4
                },
                colors = ButtonDefaults.buttonColors(containerColor = selectedOperator.color),
                modifier = Modifier.testTag("recharge_action_button")
            ) {
                Text(
                    if (isPinStep) AppStrings.t("confirm_recharge", lang)
                    else (if (lang == AppLanguage.BANGLA) "এগিয়ে যান" else "Proceed")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (isPinStep) isPinStep = false else onDismiss()
                }
            ) {
                Text(if (isPinStep) (if (lang == AppLanguage.BANGLA) "পিছনে যান" else "Back") else AppStrings.t("cancel", lang))
            }
        }
    )
}

@Composable
fun DigitalReceiptDialog(
    trxId: String,
    title: String,
    provider: String,
    providerColor: Color,
    recipient: String,
    amount: Double,
    currency: String,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = IncomeColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IncomeColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReceiptRow(label = AppStrings.t("trx_id", lang), value = trxId, isBold = true)
                    ReceiptRow(label = if (lang == AppLanguage.BANGLA) "মাধ্যম / অপারেটর" else "Provider", value = provider)
                    ReceiptRow(label = if (lang == AppLanguage.BANGLA) "প্রাপকের নম্বর" else "Recipient", value = recipient)
                    ReceiptRow(
                        label = if (lang == AppLanguage.BANGLA) "মোট পরিমাণ" else "Amount",
                        value = "$currency${"%,.2f".format(amount)}",
                        isBold = true,
                        valueColor = providerColor
                    )
                    ReceiptRow(label = if (lang == AppLanguage.BANGLA) "সময়" else "Time", value = formattedDate)
                    ReceiptRow(
                        label = if (lang == AppLanguage.BANGLA) "স্ট্যাটাস" else "Status",
                        value = if (lang == AppLanguage.BANGLA) "সফল (Successful)" else "Completed",
                        valueColor = IncomeColor,
                        isBold = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Receipt", "Amar App Receipt\nTrxID: $trxId\nProvider: $provider\nTo: $recipient\nAmount: $currency$amount\nDate: $formattedDate\nStatus: SUCCESS")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        if (lang == AppLanguage.BANGLA) "রসিদ কপি হয়েছে!" else "Receipt copied to clipboard!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onDismiss()
                },
                modifier = Modifier.testTag("receipt_copy_button")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (lang == AppLanguage.BANGLA) "রসিদ কপি করুন" else "Copy Slip")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.BANGLA) "ঠিক আছে" else "Done")
            }
        }
    )
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

val BankList = listOf(
    "BRAC Bank" to "ব্র্যাক ব্যাংক",
    "Dutch-Bangla Bank" to "ডাচ-বাংলা ব্যাংক",
    "City Bank" to "সিটি ব্যাংক",
    "Sonali Bank" to "সোনালী ব্যাংক",
    "Islami Bank" to "ইসলামী ব্যাংক"
)

@Composable
fun AddMoneyDialog(
    lang: AppLanguage,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (source: String, accountOrCard: String, amount: Double) -> String
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("card") } // "card", "bank"
    var selectedCardType by remember { mutableStateOf("Visa") }
    var selectedBank by remember { mutableStateOf(BankList[0]) }
    var cardNumber by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    // Step 2 OTP
    var isOtpStep by remember { mutableStateOf(false) }
    var otpText by remember { mutableStateOf("123456") }

    // Receipt
    var receiptTrxId by remember { mutableStateOf<String?>(null) }

    val activeSource = if (selectedMethod == "card") "$selectedCardType Card" else selectedBank.first

    if (receiptTrxId != null) {
        DigitalReceiptDialog(
            trxId = receiptTrxId!!,
            title = if (lang == AppLanguage.BANGLA) "টাকা যোগ সফল হয়েছে!" else "Money Added Successfully!",
            provider = activeSource,
            providerColor = IncomeColor,
            recipient = if (selectedMethod == "card") "Card: $cardNumber" else "A/C: $bankAccount",
            amount = amountText.toDoubleOrNull() ?: 0.0,
            currency = currency,
            lang = lang,
            onDismiss = onDismiss
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = IncomeColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AddCard,
                            contentDescription = null,
                            tint = IncomeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (isOtpStep) (if (lang == AppLanguage.BANGLA) "ওটিপি (OTP) যাচাই" else "Verify OTP")
                    else AppStrings.t("add_money", lang),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (isOtpStep) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${AppStrings.t("add_money", lang)} ($activeSource)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = IncomeColor
                            )
                            Text(
                                text = "${if (lang == AppLanguage.BANGLA) "অ্যামাউন্ট:" else "Amount:"} $currency$amountText",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = otpText,
                        onValueChange = { if (it.length <= 6) otpText = it.filter { c -> c.isDigit() } },
                        label = { Text(if (lang == AppLanguage.BANGLA) "৬ ডিজিটের ওটিপি কোড" else "6-Digit Security OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_money_otp_input")
                    )

                    Text(
                        text = if (lang == AppLanguage.BANGLA) "পরীক্ষামূলক ওটিপি কোড: ১২৩৪৫৬" else "Demo OTP code: 123456",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Method Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedMethod == "card",
                            onClick = { selectedMethod = "card" },
                            label = { Text(AppStrings.t("card_to_wallet", lang), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedMethod == "bank",
                            onClick = { selectedMethod = "bank" },
                            label = { Text(AppStrings.t("bank_to_wallet", lang), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (selectedMethod == "card") {
                        // Card Type (Visa / Mastercard)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Visa", "Mastercard").forEach { ct ->
                                FilterChip(
                                    selected = selectedCardType == ct,
                                    onClick = { selectedCardType = ct },
                                    label = { Text(ct) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text(if (lang == AppLanguage.BANGLA) "কার্ড নম্বর (১৬ ডিজিট)" else "Card Number") },
                            placeholder = { Text("4242 4242 4242 4242") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_money_card_input")
                        )
                    } else {
                        // Bank Selection
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "ব্যাংক নির্বাচন করুন" else "Select Bank",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(BankList) { b ->
                                val isSelected = selectedBank == b
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedBank = b },
                                    label = { Text(if (lang == AppLanguage.BANGLA) b.second else b.first, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = bankAccount,
                            onValueChange = { bankAccount = it },
                            label = { Text(if (lang == AppLanguage.BANGLA) "ব্যাংক একাউন্ট নম্বর" else "Bank Account Number") },
                            placeholder = { Text("102.120.938472") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_money_account_input")
                        )
                    }

                    // Amount
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("${if (lang == AppLanguage.BANGLA) "যোগ করার পরিমাণ" else "Amount to Add"} ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_money_amount_input")
                    )

                    // Quick Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("500", "1000", "2000", "5000").forEach { quickAmt ->
                            SuggestionChip(
                                onClick = { amountText = quickAmt },
                                label = { Text("+$currency$quickAmt", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isOtpStep) {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        val target = if (selectedMethod == "card") cardNumber else bankAccount
                        if (target.isNotBlank() && amt > 0) {
                            isOtpStep = true
                        }
                    } else {
                        if (otpText.length >= 4) {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val target = if (selectedMethod == "card") cardNumber else bankAccount
                            val trx = onConfirm(activeSource, target, amt)
                            receiptTrxId = trx
                        } else {
                            Toast.makeText(
                                context,
                                if (lang == AppLanguage.BANGLA) "সঠিক ওটিপি দিন" else "Enter valid OTP",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                enabled = if (!isOtpStep) {
                    val target = if (selectedMethod == "card") cardNumber else bankAccount
                    target.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                } else {
                    otpText.length >= 4
                },
                colors = ButtonDefaults.buttonColors(containerColor = IncomeColor),
                modifier = Modifier.testTag("add_money_action_button")
            ) {
                Text(
                    if (isOtpStep) (if (lang == AppLanguage.BANGLA) "কনফার্ম করুন" else "Confirm")
                    else (if (lang == AppLanguage.BANGLA) "এগিয়ে যান" else "Proceed")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (isOtpStep) isOtpStep = false else onDismiss()
                }
            ) {
                Text(if (isOtpStep) (if (lang == AppLanguage.BANGLA) "পিছনে যান" else "Back") else AppStrings.t("cancel", lang))
            }
        }
    )
}

