package com.example.p2pchat.core.theme

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.p2pchat.R
import kotlinx.coroutines.*
import java.util.*
// import javax.inject.Inject
// import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin

/**
 * Advanced Dynamic Theme Manager
 * Handles dynamic color schemes, time-based themes, and mood-based adaptations
 */
// @Singleton
class DynamicThemeManager /* @Inject constructor() */ {

    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLORS = "dynamic_colors"
        private const val KEY_TIME_BASED_THEME = "time_based_theme"
        private const val KEY_MOOD_ADAPTATION = "mood_adaptation"
        private const val KEY_CUSTOM_ACCENT = "custom_accent"
    }

    private lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Theme state
    private val _currentTheme = MutableLiveData<ThemeData>()
    val currentTheme: LiveData<ThemeData> = _currentTheme

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    // Theme modes
    enum class ThemeMode {
        LIGHT, DARK, AUTO, TIME_BASED, MOOD_ADAPTIVE
    }

    // Mood types for adaptive theming
    enum class MoodType {
        ENERGETIC, CALM, FOCUSED, ROMANTIC, PLAYFUL, PROFESSIONAL
    }

    data class ThemeData(
        @ColorInt val primaryColor: Int,
        @ColorInt val secondaryColor: Int,
        @ColorInt val accentColor: Int,
        @ColorInt val backgroundColor: Int,
        @ColorInt val surfaceColor: Int,
        @ColorInt val textColor: Int,
        @ColorInt val textSecondaryColor: Int,
        val isDark: Boolean,
        val mood: MoodType = MoodType.CALM
    )

    fun initialize(context: Context) {
        this.context = context
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load saved theme preferences
        loadThemePreferences()

        // Start dynamic theme updates
        startDynamicUpdates()
    }

    private fun loadThemePreferences() {
        val themeMode = ThemeMode.valueOf(
            preferences.getString(KEY_THEME_MODE, ThemeMode.AUTO.name) ?: ThemeMode.AUTO.name
        )

        val isDynamicColors = preferences.getBoolean(KEY_DYNAMIC_COLORS, true)
        val isTimeBasedTheme = preferences.getBoolean(KEY_TIME_BASED_THEME, true)
        val isMoodAdaptation = preferences.getBoolean(KEY_MOOD_ADAPTATION, false)

        updateTheme(themeMode, isDynamicColors, isTimeBasedTheme, isMoodAdaptation)
    }

    private fun startDynamicUpdates() {
        scope.launch {
            while (true) {
                if (preferences.getBoolean(KEY_TIME_BASED_THEME, true)) {
                    updateTimeBasedTheme()
                }
                delay(60_000) // Update every minute
            }
        }
    }

    private fun updateTimeBasedTheme() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val themeData = when (hour) {
            in 6..11 -> createMorningTheme() // Morning - Energetic
            in 12..17 -> createAfternoonTheme() // Afternoon - Focused
            in 18..21 -> createEveningTheme() // Evening - Calm
            else -> createNightTheme() // Night - Dark & Relaxing
        }

        _currentTheme.postValue(themeData)
        _isDarkMode.postValue(themeData.isDark)
    }

    private fun createMorningTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#FF6B35"), // Warm orange
            secondaryColor = Color.parseColor("#F7931E"),
            accentColor = Color.parseColor("#FFD23F"),
            backgroundColor = Color.parseColor("#FFFEF7"),
            surfaceColor = Color.parseColor("#FFFFFF"),
            textColor = Color.parseColor("#2C3E50"),
            textSecondaryColor = Color.parseColor("#7F8C8D"),
            isDark = false,
            mood = MoodType.ENERGETIC
        )
    }

    private fun createAfternoonTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#3498DB"), // Professional blue
            secondaryColor = Color.parseColor("#2980B9"),
            accentColor = Color.parseColor("#E74C3C"),
            backgroundColor = Color.parseColor("#FFFFFF"),
            surfaceColor = Color.parseColor("#F8F9FA"),
            textColor = Color.parseColor("#2C3E50"),
            textSecondaryColor = Color.parseColor("#7F8C8D"),
            isDark = false,
            mood = MoodType.FOCUSED
        )
    }

    private fun createEveningTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#9B59B6"), // Calm purple
            secondaryColor = Color.parseColor("#8E44AD"),
            accentColor = Color.parseColor("#E67E22"),
            backgroundColor = Color.parseColor("#F4F4F4"),
            surfaceColor = Color.parseColor("#FFFFFF"),
            textColor = Color.parseColor("#2C3E50"),
            textSecondaryColor = Color.parseColor("#7F8C8D"),
            isDark = false,
            mood = MoodType.CALM
        )
    }

    private fun createNightTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#BB86FC"), // Material Dark Purple
            secondaryColor = Color.parseColor("#03DAC6"),
            accentColor = Color.parseColor("#CF6679"),
            backgroundColor = Color.parseColor("#121212"),
            surfaceColor = Color.parseColor("#1E1E1E"),
            textColor = Color.parseColor("#FFFFFF"),
            textSecondaryColor = Color.parseColor("#B3B3B3"),
            isDark = true,
            mood = MoodType.CALM
        )
    }

    fun updateTheme(
        mode: ThemeMode,
        dynamicColors: Boolean = true,
        timeBasedTheme: Boolean = true,
        moodAdaptation: Boolean = false
    ) {
        // Save preferences
        preferences.edit()
            .putString(KEY_THEME_MODE, mode.name)
            .putBoolean(KEY_DYNAMIC_COLORS, dynamicColors)
            .putBoolean(KEY_TIME_BASED_THEME, timeBasedTheme)
            .putBoolean(KEY_MOOD_ADAPTATION, moodAdaptation)
            .apply()

        // Apply theme based on mode
        when (mode) {
            ThemeMode.LIGHT -> applyLightTheme()
            ThemeMode.DARK -> applyDarkTheme()
            ThemeMode.AUTO -> applyAutoTheme()
            ThemeMode.TIME_BASED -> updateTimeBasedTheme()
            ThemeMode.MOOD_ADAPTIVE -> applyMoodAdaptiveTheme()
        }
    }

    private fun applyLightTheme() {
        val themeData = ThemeData(
            primaryColor = ContextCompat.getColor(context, R.color.primary_light),
            secondaryColor = ContextCompat.getColor(context, R.color.secondary_light),
            accentColor = ContextCompat.getColor(context, R.color.accent_light),
            backgroundColor = ContextCompat.getColor(context, R.color.background_light),
            surfaceColor = ContextCompat.getColor(context, R.color.surface_light),
            textColor = ContextCompat.getColor(context, R.color.text_primary_light),
            textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_light),
            isDark = false
        )
        _currentTheme.postValue(themeData)
        _isDarkMode.postValue(false)
    }

    private fun applyDarkTheme() {
        val themeData = ThemeData(
            primaryColor = ContextCompat.getColor(context, R.color.primary_dark),
            secondaryColor = ContextCompat.getColor(context, R.color.secondary_dark),
            accentColor = ContextCompat.getColor(context, R.color.accent_dark),
            backgroundColor = ContextCompat.getColor(context, R.color.background_dark),
            surfaceColor = ContextCompat.getColor(context, R.color.surface_dark),
            textColor = ContextCompat.getColor(context, R.color.text_primary_dark),
            textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary_dark),
            isDark = true
        )
        _currentTheme.postValue(themeData)
        _isDarkMode.postValue(true)
    }

    private fun applyAutoTheme() {
        val isSystemDark = context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isSystemDark) {
            applyDarkTheme()
        } else {
            applyLightTheme()
        }
    }

    private fun applyMoodAdaptiveTheme() {
        // This would integrate with AI sentiment analysis
        // For now, we'll use a default calm theme
        val themeData = createMoodTheme(MoodType.CALM)
        _currentTheme.postValue(themeData)
        _isDarkMode.postValue(themeData.isDark)
    }

    private fun createMoodTheme(mood: MoodType): ThemeData {
        return when (mood) {
            MoodType.ENERGETIC -> createMorningTheme()
            MoodType.CALM -> createEveningTheme()
            MoodType.FOCUSED -> createAfternoonTheme()
            MoodType.ROMANTIC -> createRomanticTheme()
            MoodType.PLAYFUL -> createPlayfulTheme()
            MoodType.PROFESSIONAL -> createProfessionalTheme()
        }
    }

    private fun createRomanticTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#E91E63"), // Pink
            secondaryColor = Color.parseColor("#F06292"),
            accentColor = Color.parseColor("#FF5722"),
            backgroundColor = Color.parseColor("#FFF0F5"),
            surfaceColor = Color.parseColor("#FFFFFF"),
            textColor = Color.parseColor("#2C3E50"),
            textSecondaryColor = Color.parseColor("#7F8C8D"),
            isDark = false,
            mood = MoodType.ROMANTIC
        )
    }

    private fun createPlayfulTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#FF9800"), // Orange
            secondaryColor = Color.parseColor("#FFC107"),
            accentColor = Color.parseColor("#4CAF50"),
            backgroundColor = Color.parseColor("#FFFEF7"),
            surfaceColor = Color.parseColor("#FFFFFF"),
            textColor = Color.parseColor("#2C3E50"),
            textSecondaryColor = Color.parseColor("#7F8C8D"),
            isDark = false,
            mood = MoodType.PLAYFUL
        )
    }

    private fun createProfessionalTheme(): ThemeData {
        return ThemeData(
            primaryColor = Color.parseColor("#607D8B"), // Blue Grey
            secondaryColor = Color.parseColor("#78909C"),
            accentColor = Color.parseColor("#FF5722"),
            backgroundColor = Color.parseColor("#FAFAFA"),
            surfaceColor = Color.parseColor("#FFFFFF"),
            textColor = Color.parseColor("#263238"),
            textSecondaryColor = Color.parseColor("#546E7A"),
            isDark = false,
            mood = MoodType.PROFESSIONAL
        )
    }

    fun setCustomAccentColor(@ColorInt color: Int) {
        preferences.edit().putInt(KEY_CUSTOM_ACCENT, color).apply()
        // Update current theme with new accent color
        _currentTheme.value?.let { current ->
            val updated = current.copy(accentColor = color)
            _currentTheme.postValue(updated)
        }
    }

    fun adaptThemeToMood(mood: MoodType) {
        val themeData = createMoodTheme(mood)
        _currentTheme.postValue(themeData)
        _isDarkMode.postValue(themeData.isDark)
    }

    fun cleanup() {
        scope.cancel()
    }
}
