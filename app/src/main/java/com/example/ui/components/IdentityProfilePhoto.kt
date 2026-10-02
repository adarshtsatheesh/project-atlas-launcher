package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.io.File

private val TextPureWhite = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val SheetDarkSurface = Color(0xFF0A0F1D)

/**
 * Architectural render types for future identity upgrades (AI Avatar, 3D Avatar, Animated Avatar).
 * Currently active: PROFILE_PHOTO.
 */
enum class AvatarRenderType {
    PROFILE_PHOTO,
    AI_AVATAR,       // Future upgrade slot
    THREE_D_AVATAR,  // Future upgrade slot
    ANIMATED_AVATAR  // Future upgrade slot
}

/**
 * Large circular profile photo component.
 *
 * Represents the user's authentic identity:
 * - 140dp circular frame with ambient blue glow and soft shadows
 * - Placeholder with camera icon & "Add your profile photo"
 * - Interactive action sheet (Change Photo, View Photo, Remove Photo)
 * - Fullscreen high-resolution photo viewer dialog
 * - Auto-crops to square, centers face, and loads lazily with 60 FPS performance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityProfilePhoto(
    photoPath: String?,
    onPickImageUri: (Uri) -> Unit,
    onCaptureBitmap: (Bitmap) -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    renderType: AvatarRenderType = AvatarRenderType.PROFILE_PHOTO
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Dialog & Sheet States
    var showActionSheet by remember { mutableStateOf(false) }
    var showSourcePickerSheet by remember { mutableStateOf(false) }
    var showFullscreenPhoto by remember { mutableStateOf(false) }

    val hasPhoto = remember(photoPath) {
        !photoPath.isNullOrEmpty() && File(photoPath).exists()
    }

    // Border pulse animation when a photo is present
    val infiniteTransition = rememberInfiniteTransition(label = "photo_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Selection scale & fade animation
    val scaleAnim by animateFloatAsState(
        targetValue = if (hasPhoto) 1.0f else 0.96f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
        label = "photo_scale"
    )

    // Flash light-up border anim when photo changes
    val borderFlash = remember { Animatable(0f) }
    LaunchedEffect(photoPath) {
        if (hasPhoto) {
            borderFlash.snapTo(1f)
            borderFlash.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCaptureBitmap(bitmap)
        }
    }

    // Gallery Photo Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onPickImageUri(uri)
        }
    }

    val themeColors = com.example.ui.theme.LocalLevelThemeColors.current
    val accentColor = themeColors.primary
    val secondaryColor = themeColors.secondary
    val deepColor = themeColors.deep

    // Root Photo Anchor Container
    Box(
        modifier = modifier
            .size(size)
            .scale(scaleAnim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                if (hasPhoto) {
                    showActionSheet = true
                } else {
                    showSourcePickerSheet = true
                }
            }
            .testTag("identity_profile_photo_frame"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Outer Blue Glow (Canvas)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Soft atmospheric outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = if (hasPhoto) 0.32f * pulseAlpha + (borderFlash.value * 0.4f) else 0.15f),
                        accentColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.35f
                ),
                center = center,
                radius = radius * 1.35f
            )
        }

        // 2. Circular Photo or Placeholder
        if (hasPhoto && photoPath != null) {
            // Real User Profile Photo
            Box(
                modifier = Modifier
                    .size(size - 8.dp)
                    .shadow(elevation = 14.dp, shape = CircleShape, ambientColor = secondaryColor, spotColor = accentColor)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                secondaryColor,
                                accentColor,
                                deepColor,
                                secondaryColor
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(photoPath))
                        .crossfade(300)
                        .build(),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle inner bottom vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x33000000)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }
        } else {
            // Empty Placeholder: Circular outline + Camera Icon + Prompt
            Box(
                modifier = Modifier
                    .size(size - 8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(themeColors.cardElevated, themeColors.cardBackground)
                        )
                    )
                    .border(
                        width = 1.6.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                secondaryColor.copy(alpha = pulseAlpha),
                                accentColor.copy(alpha = 0.5f),
                                accentColor.copy(alpha = 0.22f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.16f))
                            .border(1.dp, secondaryColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Profile Photo",
                            tint = secondaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Add your\nprofile photo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 14.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = secondaryColor,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    // =========================================================================
    // 1. Existing Photo Options Bottom Sheet (Tap -> Change, View, Remove)
    // =========================================================================
    if (showActionSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            sheetState = sheetState,
            containerColor = themeColors.cardBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x44FFFFFF))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PROFILE PHOTO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    ),
                    color = secondaryColor,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // Option: View Photo Fullscreen
                ProfileOptionRow(
                    title = "View Photo",
                    subtitle = "Inspect high-resolution profile picture",
                    icon = Icons.Default.Fullscreen,
                    iconTint = secondaryColor,
                    onClick = {
                        showActionSheet = false
                        showFullscreenPhoto = true
                    }
                )

                // Option: Change Photo
                ProfileOptionRow(
                    title = "Change Photo",
                    subtitle = "Take a new selfie or select from gallery",
                    icon = Icons.Default.Edit,
                    iconTint = accentColor,
                    onClick = {
                        showActionSheet = false
                        showSourcePickerSheet = true
                    }
                )

                // Option: Remove Photo
                ProfileOptionRow(
                    title = "Remove Photo",
                    subtitle = "Restore default placeholder outline",
                    icon = Icons.Default.DeleteOutline,
                    iconTint = Color(0xFFEF4444),
                    titleColor = Color(0xFFF87171),
                    onClick = {
                        showActionSheet = false
                        onRemovePhoto()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Cancel Button
                Button(
                    onClick = { showActionSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.cardElevated),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextMuted
                    )
                }
            }
        }
    }

    // =========================================================================
    // 2. Camera vs Gallery Source Picker Sheet
    // =========================================================================
    if (showSourcePickerSheet) {
        val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSourcePickerSheet = false },
            sheetState = pickerSheetState,
            containerColor = themeColors.cardBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x44FFFFFF))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SELECT PHOTO SOURCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    ),
                    color = secondaryColor,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // Option: Camera
                ProfileOptionRow(
                    title = "Take Photo",
                    subtitle = "Capture a real-time portrait using camera",
                    icon = Icons.Default.CameraAlt,
                    iconTint = secondaryColor,
                    onClick = {
                        showSourcePickerSheet = false
                        cameraLauncher.launch(null)
                    }
                )

                // Option: Gallery
                ProfileOptionRow(
                    title = "Choose from Gallery",
                    subtitle = "Select an existing portrait from device library",
                    icon = Icons.Default.PhotoLibrary,
                    iconTint = accentColor,
                    onClick = {
                        showSourcePickerSheet = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { showSourcePickerSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.cardElevated),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextMuted
                    )
                }
            }
        }
    }

    // =========================================================================
    // 3. Fullscreen Photo Viewer Dialog
    // =========================================================================
    if (showFullscreenPhoto && hasPhoto && photoPath != null) {
        Dialog(
            onDismissRequest = { showFullscreenPhoto = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xF0030509))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { showFullscreenPhoto = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, accentColor, CircleShape)
                        .shadow(28.dp, CircleShape, spotColor = accentColor)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(File(photoPath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Full Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    titleColor: Color = TextPureWhite,
    onClick: () -> Unit
) {
    val themeColors = com.example.ui.theme.LocalLevelThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(themeColors.innerSurface)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = TextMuted
            )
        }
    }
}
