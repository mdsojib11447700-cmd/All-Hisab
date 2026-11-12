package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeColor
import com.example.ui.components.AppStrings
import com.example.ui.components.AvatarIcons
import com.example.ui.components.EditProfileDialog
import com.example.ui.viewmodel.AmarAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AmarAppViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val lang = userProfile.language

    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        AppStrings.t("settings", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarIndex = userProfile.avatarId.coerceIn(0, AvatarIcons.lastIndex)
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = AvatarIcons[avatarIndex].first,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = userProfile.userTagline,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Theme Selection Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = AppStrings.t("choose_theme", lang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        AppThemeColor.values().forEach { theme ->
                            val isSelected = userProfile.themeColor == theme
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setThemeColor(theme) },
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.BANGLA) theme.labelBn else theme.labelEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.setThemeColor(theme) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Language & Dark Mode Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Language Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    AppStrings.t("language", lang),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (lang == AppLanguage.BANGLA) "বাংলা বা ইংরেজি ভাষা নির্বাচন" else "Select Bengali or English",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = lang == AppLanguage.BANGLA,
                                    onClick = {
                                        viewModel.updateProfile(
                                            userProfile.userName,
                                            userProfile.userTagline,
                                            userProfile.avatarId,
                                            AppLanguage.BANGLA,
                                            userProfile.themeColor,
                                            userProfile.isDarkMode,
                                            userProfile.currencySymbol
                                        )
                                    },
                                    label = { Text("বাংলা") }
                                )
                                FilterChip(
                                    selected = lang == AppLanguage.ENGLISH,
                                    onClick = {
                                        viewModel.updateProfile(
                                            userProfile.userName,
                                            userProfile.userTagline,
                                            userProfile.avatarId,
                                            AppLanguage.ENGLISH,
                                            userProfile.themeColor,
                                            userProfile.isDarkMode,
                                            userProfile.currencySymbol
                                        )
                                    },
                                    label = { Text("English") }
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Dark Mode Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    AppStrings.t("dark_mode", lang),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (lang == AppLanguage.BANGLA) "ডার্ক বা লাইট মুড সক্রিয় করুন" else "Toggle Dark or Light theme",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = userProfile.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() }
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Currency Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (lang == AppLanguage.BANGLA) "মুদ্রা প্রতীক" else "Currency Symbol",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    userProfile.currencySymbol,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("৳", "$", "€", "₹").forEach { sym ->
                                    FilterChip(
                                        selected = userProfile.currencySymbol == sym,
                                        onClick = {
                                            viewModel.updateProfile(
                                                userProfile.userName,
                                                userProfile.userTagline,
                                                userProfile.avatarId,
                                                userProfile.language,
                                                userProfile.themeColor,
                                                userProfile.isDarkMode,
                                                sym
                                            )
                                        },
                                        label = { Text(sym, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // About App Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.BANGLA) "আমার অ্যাপ সম্পর্কে" else "About Amar App",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.BANGLA)
                                "এই অ্যাপটি আপনার নিজের ইচ্ছেমতো কাস্টমাইজ করার জন্য তৈরি। নোট, কাজ, বাজেট, অভ্যাস ট্র্যাকিং ও অ্যাপ মেকার দিয়ে আপনি আপনার প্রতিদিনের জীবন পরিচালনা করতে পারবেন।"
                            else
                                "Crafted to be your ultimate personalized Android space. Organize your life with notes, habits, tasks, expenses, and design new mobile apps seamlessly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0.0 • Made with Jetpack Compose & Room",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            profile = userProfile,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, tagline, avatarId, language, theme, isDark, currency ->
                viewModel.updateProfile(name, tagline, avatarId, language, theme, isDark, currency)
                showEditDialog = false
            }
        )
    }
}
