package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.AppStrings
import com.example.ui.components.EditProfileDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AmarAppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AmarAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            var currentTab by remember { mutableIntStateOf(0) }
            var showProfileDialog by remember { mutableStateOf(false) }

            val lang = userProfile.language

            // Handle back navigation to Home tab
            BackHandler(enabled = currentTab != 0) {
                currentTab = 0
            }

            MyApplicationTheme(
                themeColor = userProfile.themeColor,
                darkTheme = userProfile.isDarkMode
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_navigation_bar"),
                            tonalElevation = 6.dp
                        ) {
                            val items = listOf(
                                Triple(AppStrings.t("home", lang), Icons.Filled.Home, Icons.Outlined.Home),
                                Triple(AppStrings.t("notes", lang), Icons.Filled.StickyNote2, Icons.Outlined.StickyNote2),
                                Triple(AppStrings.t("tasks", lang), Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
                                Triple(AppStrings.t("expenses", lang), Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
                                Triple(AppStrings.t("studio", lang), Icons.Filled.Architecture, Icons.Outlined.Architecture)
                            )

                            items.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                                val selected = currentTab == index
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = index },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) filledIcon else outlinedIcon,
                                            contentDescription = label,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_tab_$index")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        label = "tabTransition"
                    ) { tab ->
                        when (tab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { currentTab = it },
                                onOpenProfileSettings = { showProfileDialog = true }
                            )
                            1 -> NotesScreen(viewModel = viewModel)
                            2 -> TasksScreen(viewModel = viewModel)
                            3 -> ExpensesScreen(viewModel = viewModel)
                            4 -> AppStudioScreen(viewModel = viewModel)
                            5 -> SettingsScreen(viewModel = viewModel)
                            else -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { currentTab = it },
                                onOpenProfileSettings = { showProfileDialog = true }
                            )
                        }
                    }
                }

                if (showProfileDialog) {
                    EditProfileDialog(
                        profile = userProfile,
                        onDismiss = { showProfileDialog = false },
                        onConfirm = { name, tagline, avatarId, language, theme, isDark, currency ->
                            viewModel.updateProfile(name, tagline, avatarId, language, theme, isDark, currency)
                            showProfileDialog = false
                        }
                    )
                }
            }
        }
    }
}
