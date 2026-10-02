package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.IdentityTitle

private val DeepBlack = Color(0xFF020407)
private val CardDark = Color(0xFF090E18)
private val ElectricAzure = Color(0xFF00C8FF)
private val CobaltNeon = Color(0xFF0070F3)
private val CyanGlow = Color(0xFF38BDF8)

/**
 * Clean, minimal, premium onboarding flow focused on authentic user identity.
 *
 * Step 1: Welcome & Philosophy
 * Step 2: Name & Identity Title Selection
 * Step 3: Profile Photo (Real Photo becomes the user's identity)
 */
@Composable
fun OnboardingSheet(
    onDismiss: () -> Unit,
    currentTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    customProfilePhotoPath: String? = null,
    onSelectTitle: (IdentityTitle) -> Unit = {},
    onUploadProfilePhotoUri: (Uri) -> Unit = {},
    onCaptureProfilePhotoBitmap: (Bitmap) -> Unit = {},
    onClearProfilePhoto: () -> Unit = {},
    onCompleteOnboarding: ((String, String) -> Unit)? = null,
    onSelectIntention: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    var currentStep by remember { mutableIntStateOf(1) } // 1: Welcome, 2: Name/Title, 3: Profile Photo
    var userNameInput by remember { mutableStateOf("Alex") }
    var selectedTitle by remember { mutableStateOf(currentTitle) }

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

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricAzure.copy(alpha = 0.12f),
                        CobaltNeon.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.42f),
                    radius = w * 0.72f
                ),
                center = Offset(w * 0.5f, h * 0.42f),
                radius = w * 0.72f
            )
        }

        // Active Step Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step Progress Indicator Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (s in 1..3) {
                    val isActive = s == currentStep
                    val isPast = s < currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(if (isActive) 28.dp else 8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isActive -> ElectricAzure
                                    isPast -> CyanGlow.copy(alpha = 0.6f)
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
                    2 -> Step2NameAndTitle(
                        name = userNameInput,
                        onNameChange = { userNameInput = it },
                        selectedTitle = selectedTitle,
                        onTitleSelect = {
                            selectedTitle = it
                            onSelectTitle(it)
                        },
                        onContinue = { currentStep = 3 }
                    )
                    3 -> Step3ProfilePhoto(
                        photoPath = customProfilePhotoPath,
                        onUploadUri = onUploadProfilePhotoUri,
                        onCaptureBitmap = onCaptureProfilePhotoBitmap,
                        onRemovePhoto = onClearProfilePhoto,
                        onComplete = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCompleteOnboarding?.invoke(userNameInput.ifBlank { "Alex" }, selectedTitle.label)
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
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(ElectricAzure.copy(alpha = 0.12f))
                .border(1.2.dp, ElectricAzure.copy(alpha = 0.7f), CircleShape),
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
                fontSize = 38.sp,
                letterSpacing = (-1).sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "An execution-first launcher designed for calm focus and continuous discipline.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp
            ),
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
                .testTag("onboard_welcome_btn")
        ) {
            Text(
                text = "Begin Setup",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = DeepBlack
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = DeepBlack,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * STEP 2: Name Entry & Title Selection
 */
@Composable
private fun Step2NameAndTitle(
    name: String,
    onNameChange: (String) -> Unit,
    selectedTitle: IdentityTitle,
    onTitleSelect: (IdentityTitle) -> Unit,
    onContinue: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "YOUR IDENTITY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            ),
            color = CyanGlow
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Minimalist Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .border(1.dp, ElectricAzure.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = TextPureWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Default
                ),
                cursorBrush = SolidColor(ElectricAzure),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboard_name_input")
            )
            if (name.isEmpty()) {
                Text(
                    text = "Enter your name",
                    color = TextMuted,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Select Starting Title",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Titles grid
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val primaryTitles = listOf(
                IdentityTitle.THE_ARCHITECT,
                IdentityTitle.THE_BUILDER,
                IdentityTitle.THE_STRATEGIST
            )

            primaryTitles.forEach { title ->
                val isSelected = title == selectedTitle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricAzure.copy(alpha = 0.16f) else CardDark)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ElectricAzure else Color(0x18FFFFFF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTitleSelect(title)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = title.label,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) TextPureWhite else TextSecondary
                        )
                        Text(
                            text = title.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ElectricAzure,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(52.dp)
                .testTag("onboard_name_continue_btn")
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = DeepBlack
            )
        }
    }
}

/**
 * STEP 3: Profile Photo Selection
 *
 * Features the large circular frame (140dp), camera/gallery bottom sheet,
 * face centering, compression, and local storage.
 */
@Composable
private fun Step3ProfilePhoto(
    photoPath: String?,
    onUploadUri: (Uri) -> Unit,
    onCaptureBitmap: (Bitmap) -> Unit,
    onRemovePhoto: () -> Unit,
    onComplete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hasPhoto = remember(photoPath) {
        !photoPath.isNullOrEmpty() && java.io.File(photoPath).exists()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "PROFILE PHOTO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            ),
            color = CyanGlow
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasPhoto) "Identity Established" else "Add Your Profile Photo",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (hasPhoto) "Your authentic photo is now your identity." else "Tap the circular frame to choose from Camera or Gallery.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Large Circular Profile Photo Component (140dp)
        IdentityProfilePhoto(
            photoPath = photoPath,
            onPickImageUri = onUploadUri,
            onCaptureBitmap = onCaptureBitmap,
            onRemovePhoto = onRemovePhoto,
            size = 140.dp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onComplete()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(52.dp)
                .testTag("onboard_enter_atlas_btn")
        ) {
            Text(
                text = if (hasPhoto) "Enter Atlas" else "Continue with Placeholder",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = DeepBlack
            )
        }
    }
}
