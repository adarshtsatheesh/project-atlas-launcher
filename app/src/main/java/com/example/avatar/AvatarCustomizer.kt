package com.example.avatar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val DeepStudioBg = Color(0xFF030509)
private val CardSurface = Color(0xFF0C111C)
private val AzureAccent = Color(0xFF00C8FF)
private val TextPureWhite = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)

private enum class CustomizerTab(val label: String) {
    FACE_SKIN("Face & Skin"),
    HAIR_EYES("Hair & Eyes"),
    OUTFIT("Outfit & Shoes"),
    ACCESSORIES("Accessories")
}

/**
 * High-end Avatar Customizer studio.
 * Gives the user full real-time control over their digital character appearance.
 */
@Composable
fun AvatarCustomizer(
    initialConfig: AvatarConfig,
    userLevel: Int = 1,
    onSave: (AvatarConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var currentConfig by remember { mutableStateOf(initialConfig) }
    var selectedTab by remember { mutableStateOf(CustomizerTab.FACE_SKIN) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepStudioBg)
                .testTag("avatar_customizer_modal")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = TextPureWhite
                        )
                    }

                    Text(
                        text = "IDENTITY STUDIO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        ),
                        color = TextPureWhite
                    )

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSave(currentConfig)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AzureAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }

                // Centered Live Avatar Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF080D1A), Color(0xFF030509))
                            )
                        )
                        .border(1.dp, Color(0x2200C8FF), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarRenderer(
                        config = currentConfig,
                        level = userLevel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp),
                        enableAnimation = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomizerTab.entries.forEach { tab ->
                        val isSelected = tab == selectedTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AzureAccent else CardSurface)
                                .clickable {
                                    selectedTab = tab
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.Black else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Customization Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    when (selectedTab) {
                        CustomizerTab.FACE_SKIN -> {
                            // Skin Tone Picker
                            Text(
                                text = "SKIN TONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SkinTone.entries.forEach { tone ->
                                    val isSel = tone == currentConfig.skinTone
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(tone.baseColor)
                                            .border(
                                                width = if (isSel) 2.5.dp else 1.dp,
                                                color = if (isSel) AzureAccent else Color(0x33FFFFFF),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                currentConfig = currentConfig.copy(skinTone = tone)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Face Shape
                            Text(
                                text = "FACE SHAPE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FaceShape.entries.forEach { shape ->
                                    val isSel = shape == currentConfig.faceShape
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.2f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                            .clickable {
                                                currentConfig = currentConfig.copy(faceShape = shape)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = shape.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            color = if (isSel) AzureAccent else TextMuted
                                        )
                                    }
                                }
                            }

                            // Beard & Facial Hair
                            Text(
                                text = "BEARD & FACIAL HAIR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                BeardStyle.entries.forEach { bStyle ->
                                    val isUnlocked = userLevel >= bStyle.requiredLevel
                                    val isSel = bStyle == currentConfig.beardStyle
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.22f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(beardStyle = bStyle)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = bStyle.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.45f) else TextPureWhite
                                        )
                                        if (!isUnlocked) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Locked",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Lvl ${bStyle.requiredLevel}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        CustomizerTab.HAIR_EYES -> {
                            // Hairstyle
                            Text(
                                text = "HAIRSTYLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                HairStyle.entries.forEach { hStyle ->
                                    val isUnlocked = userLevel >= hStyle.requiredLevel
                                    val isSel = hStyle == currentConfig.hairStyle
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.22f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(hairStyle = hStyle)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = hStyle.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.45f) else TextPureWhite
                                        )
                                        if (!isUnlocked) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Locked",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Lvl ${hStyle.requiredLevel}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Hair Color
                            Text(
                                text = "HAIR COLOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HairColor.entries.forEach { color ->
                                    val isSel = color == currentConfig.hairColor
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(color.baseColor)
                                            .border(
                                                width = if (isSel) 2.5.dp else 1.dp,
                                                color = if (isSel) AzureAccent else Color(0x33FFFFFF),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                currentConfig = currentConfig.copy(hairColor = color, beardColor = color)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Eye Color
                            Text(
                                text = "EYE COLOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                EyeColor.entries.forEach { eyeCol ->
                                    val isSel = eyeCol == currentConfig.eyeColor
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(eyeCol.irisColor)
                                            .border(
                                                width = if (isSel) 2.5.dp else 1.dp,
                                                color = if (isSel) AzureAccent else Color(0x33FFFFFF),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                currentConfig = currentConfig.copy(eyeColor = eyeCol)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSel) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        CustomizerTab.OUTFIT -> {
                            // Upper Body Outfit
                            Text(
                                text = "UPPER APPAREL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutfitType.entries.forEach { oType ->
                                    val isUnlocked = userLevel >= oType.requiredLevel
                                    val isSel = oType == currentConfig.outfitType
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.22f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(outfitType = oType)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = oType.label,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                                ),
                                                color = if (!isUnlocked) TextMuted.copy(alpha = 0.45f) else TextPureWhite
                                            )
                                            Text(
                                                text = oType.subtitle,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = TextMuted
                                            )
                                        }
                                        if (!isUnlocked) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Locked",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Lvl ${oType.requiredLevel}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Shoes / Footwear
                            Text(
                                text = "FOOTWEAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FootwearType.entries.forEach { fType ->
                                    val isUnlocked = userLevel >= fType.requiredLevel
                                    val isSel = fType == currentConfig.footwear
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.2f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(footwear = fType)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(vertical = 10.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = fType.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.4f) else if (isSel) AzureAccent else TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        CustomizerTab.ACCESSORIES -> {
                            // Glasses
                            Text(
                                text = "GLASSES & EYEWEAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                GlassesType.entries.forEach { gType ->
                                    val isUnlocked = userLevel >= gType.requiredLevel
                                    val isSel = gType == currentConfig.glasses
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.22f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(glasses = gType)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = gType.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.45f) else TextPureWhite
                                        )
                                        if (!isUnlocked) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Locked",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Lvl ${gType.requiredLevel}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Watch
                            Text(
                                text = "WRISTWEAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WatchType.entries.forEach { wType ->
                                    val isUnlocked = userLevel >= wType.requiredLevel
                                    val isSel = wType == currentConfig.watch
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.2f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(watch = wType)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(vertical = 10.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = wType.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.4f) else if (isSel) AzureAccent else TextMuted
                                        )
                                    }
                                }
                            }

                            // Hats
                            Text(
                                text = "HEADWEAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HatType.entries.forEach { hType ->
                                    val isUnlocked = userLevel >= hType.requiredLevel
                                    val isSel = hType == currentConfig.hat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) AzureAccent.copy(alpha = 0.2f) else CardSurface)
                                            .border(1.dp, if (isSel) AzureAccent else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                            .clickable(enabled = isUnlocked) {
                                                currentConfig = currentConfig.copy(hat = hType)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .padding(vertical = 10.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = hType.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            color = if (!isUnlocked) TextMuted.copy(alpha = 0.4f) else if (isSel) AzureAccent else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
