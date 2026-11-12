package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

object AppStrings {
    fun t(key: String, lang: AppLanguage): String = when (lang) {
        AppLanguage.BANGLA -> bangla[key] ?: key
        AppLanguage.ENGLISH -> english[key] ?: key
    }

    private val bangla = mapOf(
        "home" to "হোম",
        "notes" to "নোট ও ডায়েরি",
        "tasks" to "কাজ ও লক্ষ্য",
        "expenses" to "হিসাব ও ওয়ালেট",
        "studio" to "অ্যাপ মেকার",
        "settings" to "সেটিংস",
        "greeting_morning" to "শুভ সকাল",
        "greeting_afternoon" to "শুভ দুপুর",
        "greeting_evening" to "শুভ সন্ধ্যা",
        "welcome_sub" to "আপনার মনের মতো পার্সোনাল স্পেস",
        "tasks_pending" to "বাকি কাজ",
        "balance" to "মোট ব্যালেন্স",
        "income" to "আয়",
        "expense" to "খরচ",
        "habits" to "প্রতিদিনের অভ্যাস",
        "streak" to "দিন স্ট্রিক",
        "quick_actions" to "দ্রুত অ্যাকশন",
        "add_note" to "নোট লিখুন",
        "add_task" to "নতুন কাজ",
        "add_expense" to "খরচ যোগ",
        "new_app_idea" to "অ্যাপ প্ল্যান",
        "money_transfer" to "মানি ট্রান্সফার",
        "mobile_recharge" to "মোবাইল রিচার্জ",
        "add_money" to "টাকা যোগ (অ্যাড মানি)",
        "card_to_wallet" to "কার্ড থেকে যোগ",
        "bank_to_wallet" to "ব্যাংক থেকে যোগ",
        "add_money_success" to "টাকা যোগ সফল হয়েছে!",
        "send_money" to "টাকা পাঠান",
        "cash_out" to "ক্যাশ আউট",
        "recipient_number" to "প্রাপকের নাম্বার",
        "select_provider" to "মাধ্যম নির্বাচন করুন",
        "select_operator" to "মোবাইল অপারেটর",
        "prepaid" to "প্রিপেইড",
        "postpaid" to "পোস্টপেইড",
        "skitto" to "স্কিটো",
        "transfer" to "ট্রান্সফার",
        "recharge" to "রিচার্জ",
        "enter_pin" to "গোপন পিন প্রবেশ করুন",
        "confirm_transfer" to "ট্রান্সফার কনফার্ম করুন",
        "confirm_recharge" to "রিচার্জ কনফার্ম করুন",
        "trx_id" to "ট্রানজেকশন আইডি",
        "receipt_title" to "ডিজিটাল রসিদ",
        "special_offers" to "ইন্টারনেট ও মিনিট অফার",
        "all" to "সব",
        "personal" to "ব্যক্তিগত",
        "work" to "কাজের",
        "ideas" to "আইডিয়া",
        "secret" to "গোপন",
        "priority_high" to "উচ্চ",
        "priority_medium" to "মাঝারি",
        "priority_low" to "সাধারণ",
        "search_notes" to "নোট বা চিন্তা খুঁজুন...",
        "no_notes" to "কোনো নোট নেই। নতুন নোট তৈরি করুন!",
        "no_tasks" to "কোনো কাজ বাকি নেই! চমৎকার!",
        "no_expenses" to "কোনো লেনদেনের হিসাব নেই।",
        "studio_title" to "আমার মনের মতো অ্যাপ স্টুডিও",
        "studio_desc" to "আপনার যে ধরনের অ্যাপ পছন্দ, তার ব্লুপ্রিন্ট তৈরি করুন এবং সরাসরি এআই প্রম্পট বানিয়ে নিন!",
        "create_blueprint" to "নতুন অ্যাপ ডিজাইন করুন",
        "saved_blueprints" to "সংরক্ষিত অ্যাপ আইডিয়া",
        "copy_prompt" to "এআই প্রম্পট কপি করুন",
        "customize_profile" to "প্রোফাইল ও থিম কাস্টমাইজ",
        "your_name" to "আপনার নাম",
        "your_tagline" to "আপনার মোত্তো / স্লোগান",
        "choose_theme" to "পছন্দের থিম কালার",
        "dark_mode" to "ডার্ক মোড",
        "language" to "ভাষা",
        "calculator" to "ক্যালকুলেটর",
        "save" to "সংরক্ষণ",
        "cancel" to "বাতিল",
        "delete" to "মুছুন"
    )

    private val english = mapOf(
        "home" to "Home",
        "notes" to "Notes & Diary",
        "tasks" to "Tasks & Goals",
        "expenses" to "Wallet & Expenses",
        "studio" to "App Studio",
        "settings" to "Settings",
        "greeting_morning" to "Good Morning",
        "greeting_afternoon" to "Good Afternoon",
        "greeting_evening" to "Good Evening",
        "welcome_sub" to "Your personal app, crafted your way",
        "tasks_pending" to "Pending Tasks",
        "balance" to "Net Balance",
        "income" to "Income",
        "expense" to "Expense",
        "habits" to "Daily Habits",
        "streak" to "day streak",
        "quick_actions" to "Quick Actions",
        "add_note" to "New Note",
        "add_task" to "New Task",
        "add_expense" to "Add Expense",
        "new_app_idea" to "App Plan",
        "money_transfer" to "Money Transfer",
        "mobile_recharge" to "Mobile Recharge",
        "add_money" to "Add Money",
        "card_to_wallet" to "Card to Wallet",
        "bank_to_wallet" to "Bank to Wallet",
        "add_money_success" to "Money Added Successfully!",
        "send_money" to "Send Money",
        "cash_out" to "Cash Out",
        "recipient_number" to "Recipient Number",
        "select_provider" to "Select MFS Provider",
        "select_operator" to "Select Operator",
        "prepaid" to "Prepaid",
        "postpaid" to "Postpaid",
        "skitto" to "Skitto",
        "transfer" to "Transfer",
        "recharge" to "Recharge",
        "enter_pin" to "Enter Security PIN",
        "confirm_transfer" to "Confirm Transfer",
        "confirm_recharge" to "Confirm Recharge",
        "trx_id" to "Transaction ID",
        "receipt_title" to "Digital Receipt",
        "special_offers" to "Internet & Minute Packs",
        "all" to "All",
        "personal" to "Personal",
        "work" to "Work",
        "ideas" to "Ideas",
        "secret" to "Secret",
        "priority_high" to "High",
        "priority_medium" to "Medium",
        "priority_low" to "Low",
        "search_notes" to "Search notes or thoughts...",
        "no_notes" to "No notes yet. Tap + to write one!",
        "no_tasks" to "No pending tasks! All clear!",
        "no_expenses" to "No transactions recorded yet.",
        "studio_title" to "Dream App Creator Studio",
        "studio_desc" to "Design any app you imagine, generate full architecture blueprints and ready-to-use AI Studio prompts!",
        "create_blueprint" to "Design New App",
        "saved_blueprints" to "Saved App Blueprints",
        "copy_prompt" to "Copy AI Prompt",
        "customize_profile" to "Customize Profile & Theme",
        "your_name" to "Your Name",
        "your_tagline" to "Your Motto / Bio",
        "choose_theme" to "Accent Theme Color",
        "dark_mode" to "Dark Mode",
        "language" to "Language",
        "calculator" to "Calculator",
        "save" to "Save",
        "cancel" to "Cancel",
        "delete" to "Delete"
    )
}

val AvatarIcons = listOf(
    Icons.Default.Person to "Person",
    Icons.Default.Face to "Smile",
    Icons.Default.Star to "Star",
    Icons.Default.RocketLaunch to "Rocket",
    Icons.Default.Favorite to "Heart",
    Icons.Default.Psychology to "Brain"
)

// Provider & Operator branding colors
val BkashPink = Color(0xFFD12053)
val NagadOrange = Color(0xFFF7941D)
val RocketPurple = Color(0xFF8C3494)
val UpayYellow = Color(0xFFEAB308)
val BankBlue = Color(0xFF1E3A8A)

val GpBlue = Color(0xFF00A3E0)
val BanglalinkOrange = Color(0xFFFF6600)
val RobiRed = Color(0xFFED1C24)
val AirtelRed = Color(0xFFE40000)
val TeletalkGreen = Color(0xFF008850)

fun getProviderColor(provider: String): Color = when (provider.lowercase()) {
    "bkash", "বিকাশ" -> BkashPink
    "nagad", "নগদ" -> NagadOrange
    "rocket", "রকেট" -> RocketPurple
    "upay", "উপায়" -> UpayYellow
    "bank", "ব্যাংক" -> BankBlue
    "grameenphone", "gp", "গ্রামীনফোন" -> GpBlue
    "banglalink", "bl", "বাংলালিংক" -> BanglalinkOrange
    "robi", "রবি" -> RobiRed
    "airtel", "এয়ারটেল" -> AirtelRed
    "teletalk", "টেলিটক" -> TeletalkGreen
    else -> IndigoPrimary
}

@Composable
fun PriorityBadge(priority: String, lang: AppLanguage, modifier: Modifier = Modifier) {
    val (color, label) = when (priority.lowercase()) {
        "high" -> PriorityHighColor to AppStrings.t("priority_high", lang)
        "medium" -> PriorityMediumColor to AppStrings.t("priority_medium", lang)
        else -> PriorityLowColor to AppStrings.t("priority_low", lang)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CategoryBadge(category: String, lang: AppLanguage, modifier: Modifier = Modifier) {
    val label = AppStrings.t(category.lowercase(), lang)
    val color = when (category.lowercase()) {
        "transfer" -> BkashPink
        "recharge" -> GpBlue
        "salary" -> IncomeColor
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
