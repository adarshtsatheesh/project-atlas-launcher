package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.MockupScreen

@Composable
fun GestureOverviewSheet(
    onSelectMockup: (MockupScreen) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("gesture_overview_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GESTURE SYSTEM",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftSageGreen,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Spatial OS Navigation",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        ),
                        color = TextPureWhite
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassSurfaceElevated)
                        .border(1.dp, GlassBorderWhite, CircleShape)
                        .testTag("close_gesture_overview_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Project Atlas replaces traditional widget clutter with intentional spatial gestures. Fluid, 120Hz physics ensure you never feel lost.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Swipe Left -> Infinite Notes
            GestureCard(
                icon = Icons.Filled.KeyboardArrowLeft,
                accentColor = SoftSageGreen,
                gestureName = "Swipe Left",
                destinationName = "Infinite Notes (01 / 08)",
                description = "Tactile paper notebook feeling. Zero friction, instant auto-saving. Sliding left seamlessly brings in your notebook pages.",
                actionLabel = "Open Notes (01/08)",
                onClick = { onSelectMockup(MockupScreen.NOTES_P1) },
                testTag = "gesture_card_swipe_left"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Swipe Right -> Journey (Growth OS)
            GestureCard(
                icon = Icons.Filled.KeyboardArrowRight,
                accentColor = TextSecondary,
                gestureName = "Swipe Right",
                destinationName = "Journey (Growth & Identity)",
                description = "Answers 'Who am I becoming?' Tracks momentum, active mission, consistency streak, milestones, and your yearly journey map.",
                actionLabel = "Open Journey",
                onClick = { onSelectMockup(MockupScreen.HOME) },
                testTag = "gesture_card_swipe_right"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Long Press -> Launcher Settings
            GestureCard(
                icon = Icons.Outlined.TouchApp,
                accentColor = SoftSageGreen,
                gestureName = "Long Press",
                destinationName = "Launcher Settings",
                description = "Minimal preferences: Clock style selection, haptic triggers, adaptive icon monochrome filters, and focus presets.",
                actionLabel = "Settings",
                onClick = { onSelectMockup(MockupScreen.HOME) },
                testTag = "gesture_card_long_press"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Double Tap -> Lock Mobile Device
            GestureCard(
                icon = Icons.Outlined.Lock,
                accentColor = CrimsonElectric,
                gestureName = "Double Tap",
                destinationName = "Lock Mobile Device",
                description = "Double tap in the free area of the home screen to turn off the screen and lock the phone directly to the system lock screen.",
                actionLabel = "System Lock",
                onClick = { onSelectMockup(MockupScreen.HOME) },
                testTag = "gesture_card_double_tap"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mockup Showcase Selector Pills
            Text(
                text = "SIGNATURE MOCKUPS",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            MockupScreen.entries.forEach { screen ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AtlasCard)
                        .border(1.dp, AtlasBorder, RoundedCornerShape(14.dp))
                        .clickable { onSelectMockup(screen) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPureWhite,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = screen.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtlasCardElevated)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "View",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftSageGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun GestureCard(
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    gestureName: String,
    destinationName: String,
    description: String,
    actionLabel: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassCardBackgroundBrush)
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = gestureName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = accentColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AtlasCardElevated)
                        .clickable { onClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = destinationName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
