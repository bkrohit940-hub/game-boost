package com.gameboost.optimizer.system

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandAllowlistTest {

    @Test
    fun testAllowedDisplayCommands() {
        assertTrue(CommandAllowlist.isAllowed("settings put system peak_refresh_rate 120"))
        assertTrue(CommandAllowlist.isAllowed("settings put system peak_refresh_rate 120.0"))
        assertTrue(CommandAllowlist.isAllowed("settings put system min_refresh_rate 120.0"))
        assertTrue(CommandAllowlist.isAllowed("settings put global peak_refresh_rate 120.0"))
        assertTrue(CommandAllowlist.isAllowed("settings put secure user_refresh_rate 120"))
        assertTrue(CommandAllowlist.isAllowed("settings get system peak_refresh_rate"))
        assertTrue(CommandAllowlist.isAllowed("settings delete system peak_refresh_rate"))
        assertTrue(CommandAllowlist.isAllowed("cmd display clear-user-preferred-display-mode"))
    }

    @Test
    fun testAllowedAnimationCommands() {
        assertTrue(CommandAllowlist.isAllowed("settings put global window_animation_scale 0.5"))
        assertTrue(CommandAllowlist.isAllowed("settings put global window_animation_scale 0"))
        assertTrue(CommandAllowlist.isAllowed("settings put global window_animation_scale 1.0"))
        assertTrue(CommandAllowlist.isAllowed("settings put global transition_animation_scale 0.5"))
        assertTrue(CommandAllowlist.isAllowed("settings put global animator_duration_scale 0.5"))
        assertTrue(CommandAllowlist.isAllowed("settings get global window_animation_scale"))
    }

    @Test
    fun testAllowedGameModeCommands() {
        assertTrue(CommandAllowlist.isAllowed("cmd game mode performance com.tencent.ig"))
        assertTrue(CommandAllowlist.isAllowed("cmd game mode standard com.pubg.imobile"))
        assertTrue(CommandAllowlist.isAllowed("cmd game mode battery com.pubg.krmobile"))
        assertTrue(CommandAllowlist.isAllowed("cmd game mode com.tencent.ig"))
    }

    @Test
    fun testRejectsDangerousOrArbitraryCommands() {
        // Arbitrary shell commands
        assertFalse(CommandAllowlist.isAllowed("rm -rf /"))
        assertFalse(CommandAllowlist.isAllowed("su"))
        assertFalse(CommandAllowlist.isAllowed("reboot"))
        assertFalse(CommandAllowlist.isAllowed("pm uninstall com.tencent.ig"))
        assertFalse(CommandAllowlist.isAllowed("cat /data/system/users/0/settings_system.xml"))

        // Command chaining / shell injection attempts
        assertFalse(CommandAllowlist.isAllowed("settings put system peak_refresh_rate 120; rm -rf /"))
        assertFalse(CommandAllowlist.isAllowed("settings put system peak_refresh_rate 120 && reboot"))
        assertFalse(CommandAllowlist.isAllowed("cmd game mode performance com.tencent.ig | ls"))

        // Disallowed animation scales
        assertFalse(CommandAllowlist.isAllowed("settings put global window_animation_scale 999.0"))
    }

    @Test
    fun testPackageNameValidation() {
        assertTrue(CommandAllowlist.validatePackageName("com.tencent.ig"))
        assertTrue(CommandAllowlist.validatePackageName("com.pubg.imobile"))
        assertTrue(CommandAllowlist.validatePackageName("com.pubg.krmobile"))
        assertTrue(CommandAllowlist.validatePackageName("com.vng.pubgmobile"))

        assertFalse(CommandAllowlist.validatePackageName("com.tencent.ig; reboot"))
        assertFalse(CommandAllowlist.validatePackageName("com.tencent.ig && rm -rf"))
        assertFalse(CommandAllowlist.validatePackageName("`whoami`"))
    }

    @Test
    fun testNumericValidation() {
        assertTrue(CommandAllowlist.validateNumeric("60"))
        assertTrue(CommandAllowlist.validateNumeric("90.0"))
        assertTrue(CommandAllowlist.validateNumeric("120.0"))
        assertTrue(CommandAllowlist.validateNumeric("144"))

        assertFalse(CommandAllowlist.validateNumeric("abc"))
        assertFalse(CommandAllowlist.validateNumeric("120; echo hacked"))
    }
}
