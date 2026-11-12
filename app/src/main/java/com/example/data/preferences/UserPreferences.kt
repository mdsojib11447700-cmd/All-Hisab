package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppLanguage
import com.example.data.model.AppThemeColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfileState(
    val userName: String = "সজীব",
    val userTagline: String = "আমার জীবন, আমার লক্ষ্য, আমার অ্যাপ",
    val avatarId: Int = 0,
    val language: AppLanguage = AppLanguage.BANGLA,
    val themeColor: AppThemeColor = AppThemeColor.ROYAL_INDIGO,
    val isDarkMode: Boolean = false,
    val currencySymbol: String = "৳"
)

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("amar_app_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfileState {
        val langCode = prefs.getString("language", AppLanguage.BANGLA.code) ?: AppLanguage.BANGLA.code
        val lang = if (langCode == "en") AppLanguage.ENGLISH else AppLanguage.BANGLA

        val themeId = prefs.getString("theme_color", AppThemeColor.ROYAL_INDIGO.id) ?: AppThemeColor.ROYAL_INDIGO.id
        val theme = AppThemeColor.values().find { it.id == themeId } ?: AppThemeColor.ROYAL_INDIGO

        return UserProfileState(
            userName = prefs.getString("user_name", "আমার নাম") ?: "আমার নাম",
            userTagline = prefs.getString("user_tagline", "আমার জীবন, আমার লক্ষ্য, আমার অ্যাপ") ?: "আমার জীবন, আমার লক্ষ্য, আমার অ্যাপ",
            avatarId = prefs.getInt("avatar_id", 0),
            language = lang,
            themeColor = theme,
            isDarkMode = prefs.getBoolean("dark_mode", false),
            currencySymbol = prefs.getString("currency", "৳") ?: "৳"
        )
    }

    fun updateProfile(
        userName: String,
        userTagline: String,
        avatarId: Int,
        language: AppLanguage,
        themeColor: AppThemeColor,
        isDarkMode: Boolean,
        currencySymbol: String
    ) {
        prefs.edit()
            .putString("user_name", userName)
            .putString("user_tagline", userTagline)
            .putInt("avatar_id", avatarId)
            .putString("language", language.code)
            .putString("theme_color", themeColor.id)
            .putBoolean("dark_mode", isDarkMode)
            .putString("currency", currencySymbol)
            .apply()

        _userProfile.value = UserProfileState(
            userName = userName,
            userTagline = userTagline,
            avatarId = avatarId,
            language = language,
            themeColor = themeColor,
            isDarkMode = isDarkMode,
            currencySymbol = currencySymbol
        )
    }

    fun toggleLanguage() {
        val current = _userProfile.value
        val newLang = if (current.language == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
        updateProfile(
            userName = current.userName,
            userTagline = current.userTagline,
            avatarId = current.avatarId,
            language = newLang,
            themeColor = current.themeColor,
            isDarkMode = current.isDarkMode,
            currencySymbol = current.currencySymbol
        )
    }

    fun setThemeColor(theme: AppThemeColor) {
        val current = _userProfile.value
        updateProfile(
            userName = current.userName,
            userTagline = current.userTagline,
            avatarId = current.avatarId,
            language = current.language,
            themeColor = theme,
            isDarkMode = current.isDarkMode,
            currencySymbol = current.currencySymbol
        )
    }

    fun toggleDarkMode() {
        val current = _userProfile.value
        updateProfile(
            userName = current.userName,
            userTagline = current.userTagline,
            avatarId = current.avatarId,
            language = current.language,
            themeColor = current.themeColor,
            isDarkMode = !current.isDarkMode,
            currencySymbol = current.currencySymbol
        )
    }
}
