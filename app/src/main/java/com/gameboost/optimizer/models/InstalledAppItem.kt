package com.gameboost.optimizer.models

import android.graphics.drawable.Drawable

/**
 * Represents an installed Android application for BOBO Launcher.
 */
data class InstalledAppItem(
    val packageName: String,
    val label: String,
    val icon: Drawable? = null,
    val isGame: Boolean = false,
    val isFavorite: Boolean = false,
    val versionName: String? = null,
    val isInstalled: Boolean = true
)
