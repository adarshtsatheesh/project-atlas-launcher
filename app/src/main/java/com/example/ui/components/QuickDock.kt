package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary

@Composable
fun QuickDock(
    pinnedApps: List<AppItem>,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabels: Boolean = true,
    isEditMode: Boolean = false,
    onLaunchApp: (AppItem) -> Unit,
    onTogglePin: (AppItem) -> Unit,
    onOpenAppDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 4 Essential Apps: Phone, Messages, Browser, Camera
    val essentialApps = if (pinnedApps.isNotEmpty()) {
        pinnedApps.take(4)
    } else {
        listOf(
            AppItem("com.google.android.dialer", "Phone", null, isPinned = true),
            AppItem("com.google.android.apps.messaging", "Messages", null, isPinned = true),
            AppItem("com.android.chrome", "Browser", null, isPinned = true),
            AppItem("com.google.android.GoogleCamera", "Camera", null, isPinned = true)
        )
    }

    val appearance = com.example.ui.appearance.LocalAppearanceState.current
    val dockRadius = (appearance.cardRadiusDp + 4).dp

    // Spring physics dock scaling in edit mode
    val dockScale by animateFloatAsState(
        targetValue = if (isEditMode) 0.93f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dock_scale"
    )

    // iOS iconic gentle icon wiggle & float — ONLY while in edit mode.
    // Gate the infinite transitions behind isEditMode so the dock stops
    // invalidating/recomposing 6-8 times per second on the home screen.
    val wiggleAngle: Float
    val floatAnim: Float
    if (isEditMode) {
        val infiniteTransition = rememberInfiniteTransition(label = "dock_icon_wiggle")
        wiggleAngle = infiniteTransition.animateFloat(
            initialValue = -2.2f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(150, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_wiggle"
        ).value
        floatAnim = infiniteTransition.animateFloat(
            initialValue = -1.2f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(170, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_float"
        ).value
    } else {
        wiggleAngle = 0f
        floatAnim = 0f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .padding(bottom = 6.dp)
            .graphicsLayer {
                scaleX = dockScale
                scaleY = dockScale
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Essential 4-5 apps inside a luxury floating frosted glass dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dockRadius))
                .background(GlassCardBackgroundBrush)
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(dockRadius))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                essentialApps.forEachIndexed { index, app ->
                    val itemFloat = if (isEditMode) {
                        if (index % 2 == 0) floatAnim else -floatAnim
                    } else 0f
                    val itemAngle = if (isEditMode) {
                        if (index % 2 == 0) wiggleAngle else -wiggleAngle
                    } else 0f

                    DockItem(
                        app = app,
                        iconSize = iconSize,
                        // Hide when EITHER the dock-label toggle is off OR the
                        // global label switch is off (&& made the dock toggle a no-op).
                        hideLabel = hideLabels || !appearance.iconShowLabels,
                        floatOffsetY = itemFloat,
                        wiggleRotationZ = itemAngle,
                        cornerRadiusPercent = appearance.iconCornerRadiusPercent,
                        onClick = { onLaunchApp(app) },
                        onLongClick = { onTogglePin(app) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Gentle Swipe-Up Pill Guide for App Drawer (Zero clutter, elegant tactile affordance)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenAppDrawer() }
                .padding(horizontal = 24.dp, vertical = 6.dp)
                .testTag("swipe_up_drawer_hint"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x66FFFFFF))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DockItem(
    app: AppItem,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabel: Boolean = true,
    floatOffsetY: Float = 0f,
    wiggleRotationZ: Float = 0f,
    cornerRadiusPercent: Int = 24,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // The dock MUST follow the editor's icon-size setting so the preview is
    // WYSIWYG with the home screen (drawer already uses iconSize.dpSize).
    val effectiveDp = iconSize.dpSize.dp
    val boxSize = effectiveDp + 6.dp
    val iconDp = effectiveDp
    val shapeRadius = (cornerRadiusPercent * 0.26f).dp.coerceAtLeast(4.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                translationY = floatOffsetY
                rotationZ = wiggleRotationZ
            }
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("dock_app_${app.uniqueKey}")
    ) {
        val imageBitmap = remember(app.uniqueKey) {
            com.example.util.AppIconCache.getOrConvert(app.uniqueKey, app.icon)
        }

        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                // Authentic system app icon in original vibrant colors (No monochrome filter or card frame)
                Image(
                    bitmap = imageBitmap,
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(iconDp)
                        .clip(RoundedCornerShape(shapeRadius))
                )
            } else {
                // Authentic colorful Android app representation (Phone green, Messages blue, Browser, Camera)
                when {
                    app.name.contains("Phone", ignoreCase = true) -> {
                        Box(
                            modifier = Modifier
                                .size(iconDp)
                                .clip(RoundedCornerShape(shapeRadius))
                                .background(Color(0xFF2E7D32)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = app.name,
                                tint = Color.White,
                                modifier = Modifier.size((iconDp.value * 0.5f).dp)
                            )
                        }
                    }
                    app.name.contains("Message", ignoreCase = true) -> {
                        Box(
                            modifier = Modifier
                                .size(iconDp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Message,
                                contentDescription = app.name,
                                tint = Color.White,
                                modifier = Modifier.size((iconDp.value * 0.5f).dp)
                            )
                        }
                    }
                    app.name.contains("Browser", ignoreCase = true) || app.name.contains("Chrome", ignoreCase = true) -> {
                        Box(
                            modifier = Modifier
                                .size(iconDp)
                                .clip(CircleShape)
                                .background(Color(0xFF0288D1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = app.name,
                                tint = Color.White,
                                modifier = Modifier.size((iconDp.value * 0.52f).dp)
                            )
                        }
                    }
                    app.name.contains("Camera", ignoreCase = true) -> {
                        Box(
                            modifier = Modifier
                                .size(iconDp)
                                .clip(CircleShape)
                                .background(Color(0xFF37474F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = app.name,
                                tint = Color.White,
                                modifier = Modifier.size((iconDp.value * 0.5f).dp)
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(iconDp)
                                .clip(CircleShape)
                                .background(Color(0xFF455A64)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dual / Cloned app badge indicator
            if (app.isCloned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.LocalAtlasAccent.current)
                        .border(1.dp, AtlasBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (app.isWorkProfile) "W" else "2",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }

        if (!hideLabel) {
            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = app.name,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

