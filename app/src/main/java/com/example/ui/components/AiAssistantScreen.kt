package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.SoftSageGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.util.GeminiClient
import com.example.util.GeminiException
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    activeMission: TaskEntity?,
    onClose: () -> Unit,
    onAddMicroStepToMission: (TaskEntity, String) -> Unit,
    coachContext: String = "",
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var queryText by remember { mutableStateOf("") }
    var currentAiReply by remember { mutableStateOf<String?>(null) }
    var suggestedActions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isThinking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Breathing floating animation for the Luminous Orb
    val infiniteTransition = rememberInfiniteTransition(label = "orbFloat")
    val orbFloatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbFloatY"
    )
    val orbGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbGlowScale"
    )

    fun systemInstruction(): String = buildString {
        appendLine("You are Atlas, the execution-first focus coach built into the Atlas Android launcher.")
        appendLine("Your job is to help the user DO, not just plan: be direct, concrete and calm.")
        appendLine("Answer in at most 5 short sentences. No markdown headings, no bullet characters, no emojis.")
        appendLine("Whenever possible, suggest an action that takes 5 minutes or less.")
        if (coachContext.isNotBlank()) {
            appendLine("Context about the user:\n$coachContext")
        }
    }

    fun submit(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || isThinking) return

        scope.launch {
            isThinking = true
            errorMessage = null
            suggestedActions = emptyList()
            try {
                val mission = activeMission
                val wantsBreakdown = Regex("(?i)\\b(break|steps?|micro|split)").containsMatchIn(trimmed)
                if (wantsBreakdown && mission != null) {
                    val steps = GeminiClient.suggestMicroSteps(
                        taskTitle = mission.title,
                        taskDescription = mission.description,
                        existingSteps = mission.getMicroSteps().map { it.first }
                    )
                    if (steps.isNotEmpty()) {
                        currentAiReply = "Three frictionless next actions for \"${mission.title}\". Tap any of them to add it to the mission:"
                        suggestedActions = steps
                    } else {
                        currentAiReply = GeminiClient.generate(systemInstruction(), trimmed)
                    }
                } else {
                    currentAiReply = GeminiClient.generate(systemInstruction(), trimmed)
                }
            } catch (e: GeminiException) {
                currentAiReply = null
                suggestedActions = emptyList()
                errorMessage = e.message ?: "Couldn't reach Gemini. Try again."
            } catch (e: Exception) {
                currentAiReply = null
                suggestedActions = emptyList()
                errorMessage = "Something went wrong talking to Gemini. Try again."
            } finally {
                isThinking = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ai_assistant_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Back Arrow & "Atlas" title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AtlasCard)
                        .border(1.dp, AtlasBorder, CircleShape)
                        .testTag("close_ai_assistant_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Atlas",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPureWhite,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Luminous 3D Animated Orb (Screen 9 design)
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer(
                        translationY = orbFloatY,
                        scaleX = orbGlowScale,
                        scaleY = orbGlowScale
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Soft outer glowing halo
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(SoftSageGreenGlow, Color.Transparent),
                                radius = 240f
                            ),
                            shape = CircleShape
                        )
                )

                // The Luminous Core Orb
                val orbAccent = SoftSageGreen
                Canvas(modifier = Modifier.size(110.dp)) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFC7E2D1),
                                orbAccent,
                                Color(0xFF2E3E34),
                                Color(0xFF0F1813)
                            ),
                            center = center - Offset(radius * 0.3f, radius * 0.3f),
                            radius = radius * 1.3f
                        ),
                        radius = radius,
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle (Screen 9: "Atlas • Here to help you do, not just plan.")
            Text(
                text = "Here to help you do, not just plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Thinking indicator while Gemini is generating
            if (isThinking) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(AtlasCardElevated)
                        .border(1.dp, SoftSageGreen.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = SoftSageGreen,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Atlas is thinking…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Error card (missing key, offline, rate limit, ...)
            if (errorMessage != null && !isThinking) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(AtlasCardElevated)
                        .border(1.dp, CrimsonElectric.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Connection problem",
                        style = MaterialTheme.typography.labelMedium,
                        color = CrimsonElectric,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // AI Responses Card if active
            if (currentAiReply != null && !isThinking) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(AtlasCardElevated)
                        .border(1.dp, SoftSageGreen.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Text(
                        text = currentAiReply!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPureWhite,
                        lineHeight = 22.sp
                    )

                    if (suggestedActions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        suggestedActions.forEach { action ->
                            Button(
                                onClick = {
                                    if (activeMission != null) {
                                        onAddMicroStepToMission(activeMission, action)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftSageGreen,
                                    contentColor = AtlasBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ Add: $action",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Screen 9 Suggestion Cards:
            val prompts = listOf(
                "Break this task into steps >",
                "Plan my day >",
                "Suggest focus time >",
                "Why do I keep procrastinating? >"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                prompts.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AtlasCard)
                            .border(1.dp, AtlasBorder, RoundedCornerShape(18.dp))
                            .clickable { submit(prompt) }
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPureWhite
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Prompt Input Bar: "Ask Atlas..." (Screen 9 layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AtlasCard)
                    .border(1.dp, AtlasBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = { Text("Ask Atlas...", color = TextMuted, fontSize = 14.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite
                    ),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (queryText.isNotBlank() && !isThinking) {
                            val prompt = queryText
                            queryText = ""
                            submit(prompt)
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (queryText.isNotBlank() && !isThinking) SoftSageGreen else AtlasCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (queryText.isNotBlank() && !isThinking) AtlasBackground else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
