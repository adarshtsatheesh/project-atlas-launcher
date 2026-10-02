package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.MissionCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleMission = TaskEntity(
      id = 1L,
      title = "Launch Project Atlas MVP",
      description = "Execution-first Android launcher",
      isMission = true,
      microStepsRaw = "[ ] Inspect active mission card\n[ ] Start focus session"
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        MissionCard(
          mission = sampleMission,
          onStartFocus = {},
          onCompleteMission = {},
          onToggleMicroStep = { _, _ -> },
          onAddMicroStep = { _, _ -> },
          onOpenTaskEngine = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

