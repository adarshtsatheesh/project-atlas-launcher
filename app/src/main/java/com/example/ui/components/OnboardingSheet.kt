package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avatar.AvatarConfig
import com.example.avatar.AvatarEngine
import com.example.avatar.AvatarGenerator
import com.example.avatar.AvatarRenderer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AvatarStyle
import com.example.ui.viewmodel.IdentityTitle
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.sin

private val DeepBlack = Color(0xFF020407)
private val CardDark = Color(0xFF090E18)
private val ElectricAzure = Color(0xFF00C8FF)
private val CobaltNeon = Color(0xFF0070F3)
private val CyanGlow = Color(0xFF38BDF8)

/**
 * Brand-New First Time User Experience & Avatar Creation Flow.
 *
 * Step 1: Welcome Screen (Dark background, ambient blue glow, "Welcome. Let's build your identity.")
 * Step 2: User Name Entry ("What should we call you?")
 * Step 3: Photo Upload (Camera / Gallery)
 * Step 4: Avatar Generation Loading (Scanning particles, rotating messages)
 * Step 5: Reveal of stylized digital avatar (Bitmoji / Memoji inspired modern character)
 */
@Composable
fun OnboardingSheet(
    onDismiss: () -> Unit,
    onSelectIntention: (String) -> Unit = {},
    currentTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    currentStyle: AvatarStyle = AvatarStyle.MINIMAL_HOODIE,
    avatarConfig: AvatarConfig = AvatarConfig.DEFAULT_MASCULINE,
    customSelfiePath: String? = null,
    onSelectTitle: (IdentityTitle) -> Unit = {},
    onSelectStyle: (AvatarStyle) -> Unit = {},
    onUpdateAvatarConfig: (AvatarConfig) -> Unit = {},
    onUploadSelfie: (Uri) -> Unit = {},
    onClearSelfie: () -> Unit = {},
    onCompleteOnboarding: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var currentStep by remember { mutableIntStateOf(1) } // 1 through 5
    var userNameInput by remember { mutableStateOf("Alex") }
    var activeConfig by remember { mutableStateOf(avatarConfig) }

    // Step 4 Loading Experience State
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var scanMessageIndex by remember { mutableIntStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }

    val scanMessages = remember {
        listOf(
            "Analyzing your face...",
            "Detecting hairstyle...",
            "Matching beard...",
            "Building your avatar...",
            "Creating your identity...",
            "Almost done..."
        )
    }

    LaunchedEffect(currentStep) {
        if (currentStep == 4) {
            scanProgress = 0f
            isRevealed = false
            for (i in 0..100) {
                delay(30)
                scanProgress = i / 100f
                if (i == 15) scanMessageIndex = 1
                if (i == 35) scanMessageIndex = 2
                if (i == 55) scanMessageIndex = 3
                if (i == 75) scanMessageIndex = 4
                if (i == 90) scanMessageIndex = 5
            }
            delay(350)
            isRevealed = true
            currentStep = 5
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val generated = AvatarGenerator.generateFromSelfie(bitmap)
            activeConfig = generated
            onUpdateAvatarConfig(generated)

            // Save locally
            try {
                val f = File(context.filesDir, "selfie_${System.currentTimeMillis()}.jpg")
                FileOutputStream(f).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                onUploadSelfie(Uri.fromFile(f))
            } catch (e: Exception) {}

            currentStep = 4 // Begin loading ceremony
        }
    }

    // Gallery Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadSelfie(uri)
            // Generate avatar from URI
            try {
                val engine = AvatarEngine(context)
                val f = File(context.cacheDir, "temp_selfie.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(f).use { output -> input.copyTo(output) }
                }
                val bmp = android.graphics.BitmapFactory.decodeFile(f.absolutePath)
                if (bmp != null) {
                    val generated = AvatarGenerator.generateFromSelfie(bmp)
                    activeConfig = generated
                    onUpdateAvatarConfig(generated)
                    bmp.recycle()
                }
            } catch (e: Exception) {}

            currentStep = 4 // Begin loading ceremony
        }
    }

    // Ambient floating particles animation
    val infiniteTransition = rememberInfiniteTransition(label = "onboard_ambient")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("onboarding_sheet")
    ) {
        // Ambient Blue Particle Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Radial ambient blue aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricAzure.copy(alpha = 0.14f * pulseGlow),
                        CobaltNeon.copy(alpha = 0.05f * pulseGlow),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.75f
                ),
                center = Offset(w * 0.5f, h * 0.45f),
                radius = w * 0.75f
            )

            // Animated subtle floating particles
            val pOffsets = listOf(
                Pair(0.2f, 0.3f), Pair(0.8f, 0.25f), Pair(0.3f, 0.7f),
                Pair(0.7f, 0.65f), Pair(0.5f, 0.18f), Pair(0.15f, 0.85f)
            )
            for (p in pOffsets) {
                drawCircle(
                    color = CyanGlow.copy(alpha = 0.4f * pulseGlow),
                    radius = 2.2f,
                    center = Offset(w * p.first, h * p.second)
                )
            }
        }

        // Active Step Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (s in 1..5) {
                    val isActive = s == currentStep
                    val isPast = s < currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(if (isActive) 24.dp else 7.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isActive -> ElectricAzure
                                    isPast -> CyanGlow.copy(alpha = 0.5f)
                                    else -> Color(0x33FFFFFF)
                                }
                            )
                    )
                }
            }

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    1 -> Step1Welcome { currentStep = 2 }
                    2 -> Step2NameInput(
                        name = userNameInput,
                        onNameChange = { userNameInput = it },
                        onContinue = { currentStep = 3 }
                    )
                    3 -> Step3UploadPhoto(
                        onCamera = { cameraLauncher.launch(null) },
                        onGallery = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onSkip = {
                            // Baseline digital avatar
                            activeConfig = AvatarConfig.DEFAULT_MASCULINE
                            onUpdateAvatarConfig(activeConfig)
                            currentStep = 4
                        }
                    )
                    4 -> Step4LoadingExperience(
                        progress = scanProgress,
                        message = scanMessages[scanMessageIndex.coerceIn(0, scanMessages.lastIndex)]
                    )
                    5 -> Step5RevealAvatar(
                        userName = userNameInput,
                        config = activeConfig,
                        onEnter = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCompleteOnboarding?.invoke(userNameInput, "Builder")
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * STEP 1: Welcome Screen
 */
@Composable
private fun Step1Welcome(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(ElectricAzure.copy(alpha = 0.15f))
                .border(1.2.dp, ElectricAzure, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ElectricAzure,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome.",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                letterSpacing = (-1).sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Let's build your identity.",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                color = ElectricAzure
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
                .testTag("welcome_continue_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * STEP 2: Name Input
 */
@Composable
private fun Step2NameInput(
    name: String,
    onNameChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = (-0.5).sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your identity starts with a name.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(38.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.5.dp, ElectricAzure, RoundedCornerShape(18.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                textStyle = TextStyle(
                    color = TextPureWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Default
                ),
                cursorBrush = SolidColor(ElectricAzure),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input")
            )
        }

        Spacer(modifier = Modifier.height(42.dp))

        Button(
            onClick = onContinue,
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
                .testTag("name_continue_button")
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}

/**
 * STEP 3: Upload Photo (Camera & Gallery)
 */
@Composable
private fun Step3UploadPhoto(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Upload Photo",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = (-0.5).sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We will generate a stylized digital avatar\nthat matches your hairstyle, facial features & skin tone.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 20.sp),
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Take Photo with Camera Button
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.dp, ElectricAzure.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .clickable { onCamera() }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElectricAzure.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = ElectricAzure,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Take a Selfie",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPureWhite
                )
                Text(
                    text = "Snap a photo using your front camera",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Choose from Gallery Button
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                .clickable { onGallery() }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Gallery",
                    tint = CyanGlow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Choose from Gallery",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPureWhite
                )
                Text(
                    text = "Select any portrait from your library",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Continue with Baseline Avatar",
            style = MaterialTheme.typography.labelMedium.copy(color = CyanGlow, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .clickable { onSkip() }
                .padding(8.dp)
        )
    }
}

/**
 * STEP 4: Beautiful Avatar Generation Loading Experience
 */
@Composable
private fun Step4LoadingExperience(
    progress: Float,
    message: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Glowing Scanning Hologram Orb
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricAzure.copy(alpha = 0.35f),
                            CobaltNeon.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.5.dp, ElectricAzure, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CyanGlow,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${(progress * 100).toInt()}% synthesized",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                color = ElectricAzure,
                fontFamily = FontFamily.Monospace
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Smooth Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1E293B))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(listOf(CobaltNeon, ElectricAzure, CyanGlow))
                    )
            )
        }
    }
}

/**
 * STEP 5: Reveal of Newly Forged Stylized Avatar
 */
@Composable
private fun Step5RevealAvatar(
    userName: String,
    config: AvatarConfig,
    onEnter: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "YOUR IDENTITY IS READY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                fontSize = 11.5.sp
            ),
            color = ElectricAzure
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = TextPureWhite
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Large Full Body Digital Avatar Preview
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(380.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF090E1A), Color(0xFF030509))
                    )
                )
                .border(1.5.dp, Color(0x3300C8FF), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            AvatarRenderer(
                config = config,
                level = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                enableAnimation = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEnter,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
                .testTag("enter_identity_os_button")
        ) {
            Text(
                text = "Enter Identity OS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}
