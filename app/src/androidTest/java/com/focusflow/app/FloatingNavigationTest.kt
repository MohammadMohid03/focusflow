package com.focusflow.app

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.ThemeMode
import com.focusflow.app.navigation.BottomNavItem
import com.focusflow.app.presentation.components.*
import com.focusflow.app.presentation.home.HomeContent
import com.focusflow.app.presentation.home.HomeUiState
import com.focusflow.app.presentation.theme.FocusFlowTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class FloatingNavigationTest {
    @get:Rule val rule = createAndroidComposeRule<UiPreviewActivity>()
    private val items = BottomNavItem.entries.map { BottomNavItemData(it.route, it.icon, it.selectedIcon, it.label) }
    private val state = HomeUiState(userName = "Alex", focusMinutesToday = 45, completedCount = 2,
        totalCount = 8, currentStreak = 4,
        todayTasks = List(8) { Task(id = "preview-$it", title = listOf("Review lecture notes", "Practice Kotlin exercises", "Read the next chapter", "Prepare for tomorrow")[it % 4], estimatedDurationMinutes = 25) })

    @Test fun scrollCompactsDockAndAllDestinationsRemainReachable() {
        rule.setContent {
            var route by remember { mutableStateOf("home") }
            FocusFlowTheme(themeMode = ThemeMode.LIGHT_SAGE) {
                FloatingNavigationLayout(items, route, { route = it }) { HomeContent(state) }
            }
        }
        rule.onNodeWithTag("nav_home").assertIsSelected()
        val expanded = rule.onNodeWithTag("floating_navigation").fetchSemanticsNode().boundsInRoot.width
        screenshot("home-expanded")
        rule.onNodeWithTag("home_scroll").performTouchInput { swipeUp() }
        rule.waitForIdle()
        rule.onNodeWithText("FocusFlow").assertIsDisplayed()
        rule.onNodeWithTag("floating_navigation").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Compact navigation"))
        val compact = rule.onNodeWithTag("floating_navigation").fetchSemanticsNode().boundsInRoot.width
        assertTrue(compact < expanded)
        items.forEach { rule.onNodeWithTag("nav_${it.route}").assertHasClickAction().assertHeightIsAtLeast(48.dp) }
        screenshot("home-compact")
        rule.onNodeWithTag("home_scroll").performTouchInput { swipeDown() }
        rule.waitForIdle()
        rule.onNodeWithTag("floating_navigation").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded navigation"))
        rule.onNodeWithTag("nav_planner").performClick().assertIsSelected()
        rule.onNodeWithTag("floating_navigation").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded navigation"))
    }

    @Test fun dockHasNoFullWidthBackingStrip() {
        val backdrop = Color(0xFFB7D9C4)
        rule.setContent {
            FocusFlowTheme {
                FloatingNavigationLayout(items, "home", {}) {
                    Box(Modifier.fillMaxSize().background(backdrop))
                }
            }
        }
        val pixels = rule.onRoot().captureToImage().toPixelMap()
        val dock = rule.onNodeWithTag("floating_navigation").fetchSemanticsNode().boundsInRoot
        val y = (dock.top + dock.height / 2).toInt().coerceIn(0, pixels.height - 1)
        assertEquals(backdrop.toArgb(), pixels[8, y].toArgb())
        assertEquals(backdrop.toArgb(), pixels[pixels.width - 9, y].toArgb())
    }

    @Test fun keyboardHidesDockSoItCannotCoverInput() {
        rule.setContent {
            var input by remember { mutableStateOf("") }
            FocusFlowTheme {
                FloatingNavigationLayout(items, "ai_chat", {}) {
                    Column(Modifier.fillMaxSize().imePadding().padding(20.dp)) {
                        OutlinedTextField(input, { input = it }, Modifier.testTag("keyboard_input"))
                    }
                }
            }
        }
        rule.onNodeWithTag("keyboard_input").performClick().performTextInput("Study plan")
        rule.waitUntil(5_000) { rule.onAllNodesWithTag("floating_navigation").fetchSemanticsNodes().isEmpty() }
        rule.onNodeWithTag("keyboard_input").assertIsDisplayed()
        screenshot("keyboard-input")
    }

    @Test fun darkHomeRendersWithTheSameFloatingDock() {
        rule.setContent {
            FocusFlowTheme(themeMode = ThemeMode.DARK) {
                FloatingNavigationLayout(items, "home", {}) { HomeContent(state) }
            }
        }
        rule.onNodeWithTag("floating_navigation").assertIsDisplayed()
        screenshot("home-dark")
    }

    private fun screenshot(name: String) {
        rule.waitForIdle()
        val composeImage = rule.onRoot().captureToImage().asAndroidBitmap()
        // Compose can be idle just before SurfaceFlinger presents the first frame.
        android.os.SystemClock.sleep(250)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val folder = File(instrumentation.targetContext.getExternalFilesDir(null), "ui-previews").apply { mkdirs() }
        val imageFile = File(folder, "$name.png")
        val bitmap = instrumentation.uiAutomation.takeScreenshot() ?: composeImage
        imageFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        // Preserve previews after Gradle uninstalls its test APKs during cleanup.
        fun shell(command: String): String = instrumentation.uiAutomation.executeShellCommand(command)
            .use { descriptor -> java.io.FileInputStream(descriptor.fileDescriptor).use { it.readBytes().toString(Charsets.UTF_8) } }
        shell("mkdir -p /data/local/tmp/focusflow-ui-captures")
        shell("cp ${imageFile.absolutePath} /data/local/tmp/focusflow-ui-captures/$name.png")
        assertTrue("Preview must be preserved as a file", shell("ls -ld /data/local/tmp/focusflow-ui-captures/$name.png").startsWith("-"))
    }
}
