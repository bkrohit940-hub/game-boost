package com.gameboost.optimizer.system

import java.util.regex.Pattern

/**
 * Strict security allowlist for Shizuku privileged shell commands.
 * Blocks any arbitrary, dangerous, or unvalidated commands.
 */
object CommandAllowlist {

    private val PACKAGE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$")
    private val NUMBER_OR_FLOAT_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$")

    private val ALLOWED_COMMAND_PATTERNS = listOf(
        // Display refresh rate commands
        Pattern.compile("^settings put system peak_refresh_rate [0-9]+(\\.[0-9]+)?$"),
        Pattern.compile("^settings put system min_refresh_rate [0-9]+(\\.[0-9]+)?$"),
        Pattern.compile("^settings put global peak_refresh_rate [0-9]+(\\.[0-9]+)?$"),
        Pattern.compile("^settings put global min_refresh_rate [0-9]+(\\.[0-9]+)?$"),
        Pattern.compile("^settings put secure user_refresh_rate [0-9]+$"),
        Pattern.compile("^settings get system peak_refresh_rate$"),
        Pattern.compile("^settings get system min_refresh_rate$"),
        Pattern.compile("^settings get global peak_refresh_rate$"),
        Pattern.compile("^settings get global min_refresh_rate$"),
        Pattern.compile("^settings get secure user_refresh_rate$"),
        Pattern.compile("^settings delete system peak_refresh_rate$"),
        Pattern.compile("^settings delete system min_refresh_rate$"),
        Pattern.compile("^settings delete global peak_refresh_rate$"),
        Pattern.compile("^settings delete global min_refresh_rate$"),
        Pattern.compile("^settings delete secure user_refresh_rate$"),
        Pattern.compile("^cmd display set-user-preferred-display-mode [0-9]+ [0-9]+ [0-9]+(\\.[0-9]+)?$"),
        Pattern.compile("^cmd display clear-user-preferred-display-mode$"),

        // Animation scale commands
        Pattern.compile("^settings put global window_animation_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings put global transition_animation_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings put global animator_duration_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings get global window_animation_scale$"),
        Pattern.compile("^settings get global transition_animation_scale$"),
        Pattern.compile("^settings get global animator_duration_scale$"),

        // Android Game Mode API commands (Android 12+)
        Pattern.compile("^cmd game mode (standard|performance|battery) [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game mode [a-zA-Z0-9_.]+$"),

        // Inspection / Diagnostics commands
        Pattern.compile("^dumpsys display | grep -E \"(mSupportedModes|mBaseDisplayInfo|mOverrideDisplayInfo)\"$"),
        Pattern.compile("^dumpsys power | grep -E \"mPowerSaveModeEnabled\"$"),
        Pattern.compile("^dumpsys activity top | grep ACTIVITY$")
    )

    fun isAllowed(command: String): Boolean {
        val trimmed = command.trim()
        return ALLOWED_COMMAND_PATTERNS.any { it.matcher(trimmed).matches() }
    }

    fun validatePackageName(packageName: String): Boolean {
        return PACKAGE_NAME_PATTERN.matcher(packageName).matches()
    }

    fun validateNumeric(value: String): Boolean {
        return NUMBER_OR_FLOAT_PATTERN.matcher(value).matches()
    }
}
