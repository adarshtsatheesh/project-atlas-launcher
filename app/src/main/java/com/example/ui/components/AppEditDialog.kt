package com.example.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.AppItem
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary

/**
 * Popup shown by the drawer's "Edit" action: rename the app and pick a new icon
 * (built-in preset or an image from the gallery), then Save — or Reset to put the
 * real system name/icon back.
 */
@Composable
fun AppEditDialog(
    app: AppItem,
    onDismiss: () -> Unit,
    onSave: (customLabel: String?, iconPresetId: Int?, iconUri: String?) -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    var label by remember(app.uniqueKey) { mutableStateOf(app.customLabel ?: "") }
    var presetId by remember(app.uniqueKey) { mutableStateOf(app.iconPresetId) }
    var iconUri by remember(app.uniqueKey) { mutableStateOf(app.customIconUri) }

    val galleryPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            iconUri = uri.toString()
            presetId = null
        }
    }

    val previewApp = app.copy(
        customLabel = label.takeIf { it.isNotBlank() },
        iconPresetId = presetId,
        customIconUri = iconUri
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF214141B))
                .border(1.dp, GlassBorderWhite, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("app_edit_dialog")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Edit app",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GlassSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live icon preview
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!AtlasCustomAppIcon(previewApp, 78.dp, RoundedCornerShape(24.dp))) {
                    if (!AtlasAppIcon(app, 78.dp, RoundedCornerShape(24.dp))) {
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF455A64)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NAME",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = label,
                onValueChange = { if (it.length <= 28) label = it },
                placeholder = {
                    Text(text = app.name, color = TextMuted, fontSize = 14.sp)
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonElectric,
                    unfocusedBorderColor = GlassBorderSubtle,
                    focusedContainerColor = GlassSurfaceElevated,
                    unfocusedContainerColor = GlassSurfaceElevated,
                    focusedTextColor = TextPureWhite,
                    unfocusedTextColor = TextPureWhite,
                    cursorColor = CrimsonElectric
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_edit_name_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ICON",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Original system icon
                IconChoice(
                    selected = presetId == null && iconUri == null,
                    onClick = {
                        presetId = null
                        iconUri = null
                    }
                ) {
                    if (!AtlasAppIcon(app, 46.dp, RoundedCornerShape(14.dp))) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF455A64)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 2. Built-in presets
                AppIconPresets.all.forEach { preset ->
                    IconChoice(
                        selected = presetId == preset.id && iconUri == null,
                        onClick = {
                            presetId = preset.id
                            iconUri = null
                        }
                    ) {
                        AtlasCustomAppIcon(
                            app = previewApp.copy(iconPresetId = preset.id, customIconUri = null),
                            sizeDp = 46.dp,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                // 3. Image from the device
                IconChoice(
                    selected = iconUri != null,
                    onClick = { galleryPicker.launch(arrayOf("image/*")) }
                ) {
                    if (iconUri != null) {
                        AtlasCustomAppIcon(
                            app = previewApp.copy(iconPresetId = null),
                            sizeDp = 46.dp,
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = "Pick an image",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onReset()
                        label = ""
                        presetId = null
                        iconUri = null
                    },
                    modifier = Modifier.testTag("app_edit_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RestartAlt,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Reset", color = TextSecondary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel", color = TextSecondary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(CrimsonElectric)
                        .clickable {
                            onSave(
                                label.trim().takeIf { it.isNotEmpty() && it != app.name },
                                presetId,
                                iconUri
                            )
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .testTag("app_edit_save_button")
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun IconChoice(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(GlassSurfaceElevated)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) CrimsonElectric else GlassBorderSubtle,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
