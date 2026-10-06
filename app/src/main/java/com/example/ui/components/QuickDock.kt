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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LauncherUiState

@Composable
fun QuickDock(
    pinnedApps: List<AppItem>,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabels: Boolean = true,
    isEditMode: Boolean = false,
    onLaunchApp: (AppItem) -> Unit,
    onRemoveApp: (AppItem) -> Unit,
    onOpenAppDrawer: () -> Unit,
    /**
     * Reports the dock's screen (root) bounds so the app drawer knows where to
     * drop an app that is dragged out of the drawer onto the home screen.
     */
    onDockBounds: (Rect) -> Unit = {},
    /** False inside the editor's phone mockup: show, never react. */
    interactive: Boolean = true,
    modifier: Modifier = Modifier
) {
    // The dock renders exactly the user's pinned list, in the user's order.
    // There is deliberately no auto-generated fallback: the dock only changes
    // when the user adds, removes or rearranges something.
    val dockApps = remember(pinnedApps) {
        pinnedApps.take(LauncherUiState.MAX_DOCK_APPS)
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
        // Essential apps inside a luxury floating frosted glass dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dockRadius))
                .background(GlassCardBackgroundBrush)
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(dockRadius))
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .onGloballyPositioned {
                    if (interactive) onDockBounds(it.boundsInRoot().inflate(16f))
                }
                .testTag("home_dock")
        ) {
            if (dockApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onOpenAppDrawer() }
                        .padding(vertical = 12.dp)
                        .testTag("empty_dock_hint"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open the drawer and drag apps here to pin them",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockApps.forEachIndexed { index, app ->
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
                            interactive = interactive,
                            onClick = { onLaunchApp(app) },
                            onRemove = { onRemoveApp(app) }
                        )
                    }
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

@Composable
private fun DockItem(
    app: AppItem,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabel: Boolean = true,
    floatOffsetY: Float = 0f,
    wiggleRotationZ: Float = 0f,
    cornerRadiusPercent: Int = 24,
    interactive: Boolean = true,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    // The dock MUST follow the editor's icon-size setting so the preview is
    // WYSIWYG with the home screen (drawer already uses iconSize.dpSize).
    val effectiveDp = iconSize.dpSize.dp
    val boxSize = effectiveDp + 6.dp
    val iconDp = effectiveDp
    val shapeRadius = (cornerRadiusPercent * 0.26f).dp.coerceAtLeast(4.dp)

    var menuOpen by remember(app.uniqueKey) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                translationY = floatOffsetY
                rotationZ = wiggleRotationZ
            }
            .clip(RoundedCornerShape(18.dp))
            // Tap = open. Long press = "Remove from dock" menu — never an
            // instant, accidental unpin. Handled in ONE gesture so a long press
            // can't also trigger the launch on release.
            .pointerInput(app.uniqueKey, interactive) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var longPressed = false
                    val upOrCancel = try {
                        withTimeout(viewConfiguration.longPressTimeoutMillis) {
                            waitForUpOrCancellation()
                        }
                    } catch (e: PointerEventTimeoutCancellationException) {
                        longPressed = true
                        null
                    }
                    if (longPressed) {
                        if (interactive) {
                            menuOpen = true
                            // Swallow the rest of this gesture (no launch on release).
                            waitForUpOrCancellation()
                        }
                    } else if (upOrCancel != null) {
                        upOrCancel.consume()
                        onClick()
                    }
                }
            }
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("dock_app_${app.uniqueKey}")
    ) {
        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            if (!AtlasAppIcon(app, iconDp, RoundedCornerShape(shapeRadius))) {
                // Authentic colorful Android app representation for apps whose
                // drawable could not be read (Phone green, Messages blue, ...)
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
                                contentDescription = app.displayName,
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
                                contentDescription = app.displayName,
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
                                contentDescription = app.displayName,
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
                                contentDescription = app.displayName,
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
                                text = app.displayName.take(1).uppercase(),
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
                text = app.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }

    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = { menuOpen = false },
        modifier = Modifier
            .border(1.dp, GlassBorderWhite, RoundedCornerShape(18.dp))
            .testTag("dock_menu_${app.uniqueKey}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!AtlasAppIcon(app, 26.dp, RoundedCornerShape(8.dp))) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF455A64)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.displayName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = app.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = TextPureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider(color = GlassBorderSubtle)
        DropdownMenuItem(
            text = { Text("Remove from dock", color = TextPureWhite, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircleOutline,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onRemove()
            }
        )
    }
}
