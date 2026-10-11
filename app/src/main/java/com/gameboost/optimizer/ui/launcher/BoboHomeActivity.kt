package com.gameboost.optimizer.ui.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gameboost.optimizer.GameBoostApp
import com.gameboost.optimizer.MainNavigation
import com.gameboost.optimizer.theme.GameBoostTheme

/**
 * Optional Android Home Launcher Activity for BOBO Launcher.
 * Declares android.intent.category.HOME and android.intent.category.DEFAULT in AndroidManifest.xml.
 * Allows the user to select BOBO Launcher as their device's default Home app through
 * standard Android Settings > Apps > Default apps > Home app.
 */
class BoboHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GameBoostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            GameBoostApp.instance.shizukuManager.refreshStatus()
        } catch (_: Throwable) {}
    }
}
