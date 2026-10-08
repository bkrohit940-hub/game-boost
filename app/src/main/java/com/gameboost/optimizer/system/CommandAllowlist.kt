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
        Pattern.compile("^cmd display get-user-preferred-display-mode$"),

        // OEM specific refresh rate controls (Xiaomi / Samsung / OnePlus / ColorOS)
        Pattern.compile("^settings put secure miui_refresh_rate [0-9]+$"),
        Pattern.compile("^settings get secure miui_refresh_rate$"),
        Pattern.compile("^settings delete secure miui_refresh_rate$"),
        Pattern.compile("^settings put system refresh_rate_mode [0-9]+$"),
        Pattern.compile("^settings get system refresh_rate_mode$"),
        Pattern.compile("^settings delete system refresh_rate_mode$"),
        Pattern.compile("^settings put secure refresh_rate_setting [0-9]+$"),
        Pattern.compile("^settings get secure refresh_rate_setting$"),
        Pattern.compile("^settings delete secure refresh_rate_setting$"),

        // Animation scale commands
        Pattern.compile("^settings put global window_animation_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings put global transition_animation_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings put global animator_duration_scale (0|0\\.0|0\\.5|1|1\\.0)$"),
        Pattern.compile("^settings get global window_animation_scale$"),
        Pattern.compile("^settings get global transition_animation_scale$"),
        Pattern.compile("^settings get global animator_duration_scale$"),

        // Android Game Mode and Frame Rate Override API commands (Android 12+, 13+)
        Pattern.compile("^cmd game mode (standard|performance|battery) [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game mode [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game fps (30|60|90|120|144) [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game fps [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game downscale (0\\.[0-9]+|1\\.0) [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game list-configs [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game list-modes [a-zA-Z0-9_.]+$"),
        Pattern.compile("^cmd game reset (mode|fps|all) [a-zA-Z0-9_.]+$"),
        Pattern.compile("^device_config get game_overlay [a-zA-Z0-9_.]+$"),
        Pattern.compile("^device_config put game_overlay [a-zA-Z0-9_.]+ [a-zA-Z0-9_=,:]+$"),
        Pattern.compile("^device_config delete game_overlay [a-zA-Z0-9_.]+$"),

        // Memory optimization commands (non-destructive trim memory for eligible background workloads)
        Pattern.compile("^am trim-memory [a-zA-Z0-9_.]+ (RUNNING_MODERATE|RUNNING_LOW|RUNNING_CRITICAL|COMPLETE|MODERATE|HIDDEN)$"),
        Pattern.compile("^cmd activity trim-memory [a-zA-Z0-9_.]+ (RUNNING_MODERATE|RUNNING_LOW|RUNNING_CRITICAL|COMPLETE|MODERATE|HIDDEN)$"),

        // Verified Power & Performance mode commands
        Pattern.compile("^cmd power set-mode [0-1]$"),

        // Inspection / Diagnostics commands
        Pattern.compile("^id$"),
        Pattern.compile("^whoami$"),
        Pattern.compile("^getprop ro\\.build\\.version\\.release$"),
        Pattern.compile("^getprop ro\\.product\\.model$"),
        Pattern.compile("^dumpsys display \\| grep -E \"(mSupportedModes|mBaseDisplayInfo|mOverrideDisplayInfo)\"$"),
        Pattern.compile("^dumpsys display.*$"),
        Pattern.compile("^dumpsys power \\| grep -E \"mPowerSaveModeEnabled\"$"),
        Pattern.compile("^dumpsys activity top \\| grep ACTIVITY$"),
        Pattern.compile("^dumpsys thermalservice$"),
        Pattern.compile("^dumpsys battery$"),
        Pattern.compile("^dumpsys game$"),
        Pattern.compile("^dumpsys SurfaceFlinger --latency( [a-zA-Z0-9_.]+)?$")
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
