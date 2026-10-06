package com.gameboost.optimizer.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.gameboost.optimizer.theme.GameBoostTheme
import com.gameboost.optimizer.ui.components.BadgeState
import com.gameboost.optimizer.ui.components.StatusBadge
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for Game Boost components. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      GameBoostTheme {
        StatusBadge(text = "BOOST READY", state = BadgeState.SUCCESS)
      }
    }
  }

  @Test
  fun testStatusBadgeExists() {
    composeTestRule.onNodeWithText("BOOST READY").assertExists()
  }
}
