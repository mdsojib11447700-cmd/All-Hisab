package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppIdeaEntity
import com.example.data.model.AppLanguage
import com.example.ui.components.AppStrings
import com.example.ui.viewmodel.AmarAppViewModel

data class PrebuiltTemplate(
    val titleBn: String,
    val titleEn: String,
    val category: String,
    val audienceBn: String,
    val audienceEn: String,
    val featuresBn: String,
    val featuresEn: String,
    val descBn: String,
    val descEn: String,
    val iconName: String
)

val SampleAppTemplates = listOf(
    PrebuiltTemplate(
        titleBn = "আমার অনলাইন দোকান (Amar Dokan)",
        titleEn = "Amar Dokan - E-Commerce Store",
        category = "E-Commerce",
        audienceBn = "বাংলাদেশের খুচরা ব্যবসায়ী ও সাধারণ ক্রেতা",
        audienceEn = "Local retail businesses and everyday shoppers",
        featuresBn = "পণ্য ক্যাটালগ, ক্যাটেগরি ফিল্টার, কার্ট ও চেকআউট, ক্যাশ অন ডেলিভারি ও বিকাশ পেমেন্ট",
        featuresEn = "Product catalog, categories, cart & checkout, bKash & Cash on Delivery support",
        descBn = "খুচরা ব্যবসার জন্য পণ্য প্রদর্শন এবং সহজে অর্ডার নেওয়ার আধুনিক অ্যাপ।",
        descEn = "A seamless mobile store for local businesses with cart and payment.",
        iconName = "Storefront"
    ),
    PrebuiltTemplate(
        titleBn = "ফিটনেস ও সুস্থতা ট্র্যাকার",
        titleEn = "Daily Fitness & Diet Tracker",
        category = "Health & Fitness",
        audienceBn = "স্বাস্থ্য সচেতন যে কেউ",
        audienceEn = "Fitness enthusiasts and healthy lifestyle seekers",
        featuresBn = "দৈনিক ক্যালোরি কাউন্টার, স্টেপ ও ওয়ার্কআউট লগ, পানি পানের রিমাইন্ডার, ওজন প্রগ্রেস চার্ট",
        featuresEn = "Calorie tracker, step and workout log, hydration reminder, weight charts",
        descBn = "প্রতিদিনের শরীরচর্চা এবং স্বাস্থ্যকর ডায়েট বজায় রাখার পূর্ণাঙ্গ গাইড।",
        descEn = "Comprehensive companion for tracking workouts, nutrition, and hydration.",
        iconName = "FitnessCenter"
    ),
    PrebuiltTemplate(
        titleBn = "স্মার্ট কুইজ ও বিসিএস প্রস্তুতি",
        titleEn = "Smart BCS & Job Quiz Prep",
        category = "Education",
        audienceBn = "চাকরিপ্রার্থী ও বিশ্ববিদ্যালয়ের ছাত্র-ছাত্রী",
        audienceEn = "Job candidates and university students",
        featuresBn = "টাইমার কুইজ, বিষয়ভিত্তিক প্রশ্নব্যাংক, লিডারবোর্ড ও প্রগ্রেস অ্যানালিটিক্স, বুকমার্ক",
        featuresEn = "Timed quizzes, topic question bank, score leaderboard, bookmarking",
        descBn = "বিসিএস ও বিভিন্ন সরকারি-বেসরকারি চাকরির পরীক্ষার জন্য প্রস্তুত হওয়ার অ্যাপ।",
        descEn = "Exam preparation app with timed mock tests and progress analytics.",
        iconName = "School"
    ),
    PrebuiltTemplate(
        titleBn = "দৈনিক ইসলামিক জীবনধারা",
        titleEn = "Daily Islamic Lifestyle",
        category = "Lifestyle & Religion",
        audienceBn = "মুসলিম উম্মাহ",
        audienceEn = "Muslim users worldwide",
        featuresBn = "নামাজের সময়সূচি, কিবলা কম্পাস, ডিজিটাল তাসবিহ কাউন্টার, দৈনন্দিন দোয়া ও অডিও",
        featuresEn = "Prayer times, Qibla compass, digital Tasbih counter, daily Duas with audio",
        descBn = "প্রতিদিনের ইবাদত ও দ্বীনি জীবনকে সহজ ও শৃঙ্খলিত রাখার অ্যাপ।",
        descEn = "Prayer companion with timely alerts, Qibla, and digital Tasbih.",
        iconName = "Mosque"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStudioScreen(
    viewModel: AmarAppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val appIdeas by viewModel.appIdeas.collectAsState()
    val lang = userProfile.language

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedTemplateForPrompt by remember { mutableStateOf<AppIdeaEntity?>(null) }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AI Studio Prompt", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            if (lang == AppLanguage.BANGLA) "প্রম্পট কপি করা হয়েছে! AI Studio-তে পেস্ট করুন।" else "Prompt copied! Paste it in AI Studio.",
            Toast.LENGTH_SHORT
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            AppStrings.t("studio_title", lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (lang == AppLanguage.BANGLA) "আপনার নিজের অ্যাপের ব্লুপ্রিন্ট ও এআই প্রম্পট" else "Create and export blueprints for your dream app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("create_app_blueprint_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Create App Blueprint", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Studio Card
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Architecture,
                                        contentDescription = "Studio",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    if (lang == AppLanguage.BANGLA) "আমি আমার মতো অ্যাপ বানাতে চাই!" else "I want to build my dream app!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    if (lang == AppLanguage.BANGLA) "নিচের রেডিমেড টেমপ্লেট বেছে নিন অথবা নিজের আইডিয়া লিখে তৈরি করুন।" else "Pick a pre-built template or define your own custom idea.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_design_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Draw, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(AppStrings.t("create_blueprint", lang))
                        }
                    }
                }
            }

            // Ready Templates Section
            item {
                Text(
                    text = if (lang == AppLanguage.BANGLA) "💡 প্রস্তুত অ্যাপ টেমপ্লেট ও আইডিয়া" else "💡 Inspiration App Blueprints",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SampleAppTemplates) { template ->
                        TemplateCard(
                            template = template,
                            lang = lang,
                            onUseTemplate = {
                                viewModel.saveAppIdea(
                                    appName = if (lang == AppLanguage.BANGLA) template.titleBn else template.titleEn,
                                    category = template.category,
                                    targetAudience = if (lang == AppLanguage.BANGLA) template.audienceBn else template.audienceEn,
                                    features = if (lang == AppLanguage.BANGLA) template.featuresBn else template.featuresEn,
                                    description = if (lang == AppLanguage.BANGLA) template.descBn else template.descEn
                                )
                                Toast.makeText(
                                    context,
                                    if (lang == AppLanguage.BANGLA) "ব্লুপ্রিন্ট যোগ করা হয়েছে!" else "Blueprint added!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            // Saved Blueprints List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.t("saved_blueprints", lang)} (${appIdeas.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (appIdeas.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (lang == AppLanguage.BANGLA) "কোনো সংরক্ষিত অ্যাপ আইডিয়া নেই" else "No saved app blueprints yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(appIdeas) { idea ->
                    AppIdeaCard(
                        idea = idea,
                        lang = lang,
                        onCopyPrompt = { copyToClipboard(idea.generatedPrompt) },
                        onDelete = { viewModel.deleteAppIdea(idea) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showCreateDialog) {
        CreateCustomAppDialog(
            lang = lang,
            onDismiss = { showCreateDialog = false },
            onSave = { appName, category, audience, features, desc ->
                viewModel.saveAppIdea(appName, category, audience, features, desc)
                showCreateDialog = false
                Toast.makeText(
                    context,
                    if (lang == AppLanguage.BANGLA) "নতুন অ্যাপ আইডিয়া ব্লুপ্রিন্ট তৈরি হয়েছে!" else "New app blueprint saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
fun TemplateCard(
    template: PrebuiltTemplate,
    lang: AppLanguage,
    onUseTemplate: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(210.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = template.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = if (lang == AppLanguage.BANGLA) template.titleBn else template.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = if (lang == AppLanguage.BANGLA) template.descBn else template.descEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            FilledTonalButton(
                onClick = onUseTemplate,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    if (lang == AppLanguage.BANGLA) "ব্লুপ্রিন্ট ব্যবহার করুন" else "Use Blueprint",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AppIdeaCard(
    idea: AppIdeaEntity,
    lang: AppLanguage,
    onCopyPrompt: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = idea.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = idea.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            if (idea.description.isNotBlank()) {
                Text(
                    text = idea.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }

            if (idea.features.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "মূল ফিচারসমূহ:" else "Key Features:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = idea.features,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "প্রস্তুত এআই স্টুডিও প্রম্পট:" else "Generated AI Studio Prompt:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = idea.generatedPrompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) (if (lang == AppLanguage.BANGLA) "প্রম্পট লুকান" else "Hide Prompt")
                        else (if (lang == AppLanguage.BANGLA) "প্রম্পট দেখুন" else "View Prompt")
                    )
                }

                Button(
                    onClick = onCopyPrompt,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(AppStrings.t("copy_prompt", lang), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun CreateCustomAppDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (appName: String, category: String, audience: String, features: String, desc: String) -> Unit
) {
    var appName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (lang == AppLanguage.BANGLA) "ই-কমার্স" else "E-Commerce") }
    var audience by remember { mutableStateOf(if (lang == AppLanguage.BANGLA) "সাধারণ মানুষ ও ব্যবহারকারী" else "General Users") }
    var features by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val presetFeatures = listOf(
        "ইউজার লগইন", "রুম ডেটাবেজ", "ডার্ক মোড", "বিকাশ পেমেন্ট", "সার্চ ফিল্টার", "পুশ নোটিফিকেশন", "শেয়ার অপশন"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lang == AppLanguage.BANGLA) "আমার নতুন অ্যাপের ব্লুপ্রিন্ট" else "Custom App Blueprint", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "অ্যাপের নাম" else "App Name") },
                    placeholder = { Text("উদাঃ আমার বাজেট ট্র্যাকার") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "ক্যাটেগরি" else "Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "অ্যাপটির মূল কাজ কী?" else "App Overview") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = features,
                    onValueChange = { features = it },
                    label = { Text(if (lang == AppLanguage.BANGLA) "ফিচারসমূহ (কমা দিয়ে লিখুন)" else "Key Features") },
                    placeholder = { Text("উদাঃ লগইন, চার্ট, পিডিএফ এক্সপোর্ট") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (lang == AppLanguage.BANGLA) "দ্রুত ফিচার সিলেক্ট করুন:" else "Quick Feature Chips:",
                    style = MaterialTheme.typography.labelSmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetFeatures.take(3).forEach { f ->
                        SuggestionChip(
                            onClick = {
                                features = if (features.isBlank()) f else "$features, $f"
                            },
                            label = { Text(f, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (appName.isNotBlank()) {
                        onSave(appName, category, audience, features, description)
                    }
                },
                enabled = appName.isNotBlank()
            ) {
                Text(AppStrings.t("save", lang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.t("cancel", lang)) }
        }
    )
}
