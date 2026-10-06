package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import com.example.ui.components.IdentityProfilePhoto
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.ui.viewmodel.DayActivity
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.ColorPalette
import com.example.ui.theme.LevelThemeColors
import com.example.ui.theme.LocalLevelThemeColors
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.getLevelThemeColors
import com.example.ui.viewmodel.IdentityTitle
import kotlinx.coroutines.delay

data class EvolutionBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val rarity: String,
    val unlockedDate: String,
    val lore: String
)

data class EvolutionGearTier(
    val levelReq: Int,
    val name: String,
    val phase: String,
    val description: String,
    val visualDetail: String
)

data class IdentityAttributeItem(
    val name: String,
    val value: Float,
    val description: String
)

/**
 * Identity Operating System: Identity Evolution Screen.
 *
 * Designed to make the user feel like they are becoming a better version of themselves
 * every single day through psychological reinforcement of identity, progress, and consistency.
 */
@Composable
fun JourneyScreen(
    profileName: String,
    activeMission: TaskEntity?,
    completedTasksCount: Int,
    totalFocusMinutes: Int,
    notesCount: Int,
    userTotalXp: Int = 0,
    claimedTodayXp: Boolean = false,
    customAvatarPath: String? = null,
    identityTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    identityVocation: String = "Developer",
    currentFormTitle: String = "Focused Starter",
    nextEvolutionTitle: String = "Level 2 • Disciplined Builder",
    momentumDays: Int = 1,
    unlockedBadges: Set<String> = emptySet(),
    colorPalette: ColorPalette? = null,
    /** Today's real quests, shown as the Daily Quest checklist. */
    todayTasks: List<TaskEntity> = emptyList(),
    /** Last 7 days of real activity for the momentum strip (oldest first). */
    weekActivity: List<DayActivity> = emptyList(),
    onToggleTask: (TaskEntity) -> Unit = {},
    onClaimXp: (Int) -> Unit = {},
    onUpdateIdentityTitle: (IdentityTitle) -> Unit = {},
    onUpdateProfileName: (String) -> Unit = {},
    onUploadCustomAvatar: (Uri) -> Unit = {},
    onCaptureCustomAvatarBitmap: ((Bitmap) -> Unit)? = null,
    onClearCustomAvatar: () -> Unit = {},
    onContinueMission: (TaskEntity?) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localLevelColors = LocalLevelThemeColors.current
    val themeColors = remember(colorPalette, localLevelColors) {
        if (colorPalette != null) getLevelThemeColors(colorPalette) else localLevelColors
    }
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    // Dialog & Ceremony States
    var showLevelUpCeremony by remember { mutableStateOf(false) }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var selectedBadgeForInspection by remember { mutableStateOf<EvolutionBadge?>(null) }

    // Accurate Level 1 Start: 100 XP per level
    val xpPerLevel = 100
    val level = (userTotalXp / xpPerLevel) + 1
    val currentLevelMinXp = (level - 1) * xpPerLevel
    val currentLevelMaxXp = level * xpPerLevel
    val currentLevelXp = (userTotalXp - currentLevelMinXp).coerceAtLeast(0)
    val xpToNextLevel = (currentLevelMaxXp - userTotalXp).coerceAtLeast(0)
    val levelProgress = (currentLevelXp.toFloat() / xpPerLevel.toFloat()).coerceIn(0.04f, 1f)

    val dynamicCurrentForm = when {
        level >= 100 -> "Living Legend"
        level >= 75 -> "Grandmaster"
        level >= 50 -> "Elite Visionary"
        level >= 35 -> "Strategic Architect"
        level >= 20 -> "The Strategist"
        level >= 10 -> "Consistent Executor"
        level >= 5 -> "Disciplined Apprentice"
        level >= 2 -> "Disciplined Builder"
        else -> currentFormTitle
    }

    val dynamicNextEvolution = when {
        level >= 100 -> "Absolute Sovereignty Reached"
        level >= 75 -> "Level 100 • Living Legend"
        level >= 50 -> "Level 75 • Grandmaster"
        level >= 35 -> "Level 50 • Elite Visionary"
        level >= 20 -> "Level 35 • Strategic Architect"
        level >= 10 -> "Level 20 • The Strategist"
        level >= 5 -> "Level 10 • Consistent Executor"
        level >= 2 -> "Level 5 • Disciplined Apprentice"
        else -> nextEvolutionTitle
    }

    // Animated Level Progress
    val animatedLevelProgress by animateFloatAsState(
        targetValue = levelProgress,
        animationSpec = spring(stiffness = 220f, dampingRatio = 0.85f),
        label = "level_progress_anim"
    )

    val animatedXp by animateIntAsState(
        targetValue = userTotalXp,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "xp_counter_anim"
    )

    // Infinite pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "evo_aura_loop")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow_anim"
    )

    // Rotating Motivational Message
    var activeMotivationalIndex by remember { mutableIntStateOf(0) }
    val motivationalMessages = remember {
        listOf(
            "You are becoming someone who finishes what they start.",
            "You kept a promise to yourself today.",
            "Discipline compounds silently into extraordinary leverage.",
            "Your future self noticed today's focused work."
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            activeMotivationalIndex = (activeMotivationalIndex + 1) % motivationalMessages.size
        }
    }

    // 7 Core Identity Attributes growing with Level
    val identityAttributes = remember(level, userTotalXp) {
        val growth = ((level - 1) * 0.08f + (levelProgress * 0.08f)).coerceIn(0f, 0.70f)
        listOf(
            IdentityAttributeItem("Focus", (0.28f + growth).coerceIn(0.20f, 0.98f), "Ability to sustain single-task deep immersion"),
            IdentityAttributeItem("Discipline", (0.24f + growth * 1.05f).coerceIn(0.20f, 0.98f), "Executing irrespective of fleeting emotion"),
            IdentityAttributeItem("Execution", (0.26f + growth * 0.95f).coerceIn(0.20f, 0.98f), "Closing open loops and finishing commitments"),
            IdentityAttributeItem("Consistency", (0.22f + growth * 1.1f).coerceIn(0.20f, 0.98f), "Unbroken cadence of baseline daily habits"),
            IdentityAttributeItem("Knowledge", (0.25f + growth * 0.9f).coerceIn(0.20f, 0.98f), "Systematic synthesis of lessons & notes"),
            IdentityAttributeItem("Leadership", (0.20f + growth * 1.0f).coerceIn(0.20f, 0.98f), "Commanding priorities and rejecting distractions"),
            IdentityAttributeItem("Energy", (0.32f + growth * 0.85f).coerceIn(0.25f, 0.98f), "Physical stamina and recovery optimization")
        )
    }

    // Realistic Modern Evolution Apparel Tiers (Levels 1, 10, 20, 35, 50, 75, 100)
    val evolutionTiers = remember {
        listOf(
            EvolutionGearTier(1, "Basic Tech Hoodie", "Foundation", "Simple clothing, neutral posture. Raw discipline with nothing to prove.", "Standard matte dark weave"),
            EvolutionGearTier(10, "Cleaner Structured Apparel", "Discipline", "Crisp minimalist tech crewneck, confident posture.", "Clean tailored cargo seams"),
            EvolutionGearTier(20, "Premium Modern Jacket", "Mastery", "Tailored bomber jacket, subtle cyan seam lighting, athletic stance.", "Volumetric rim lighting"),
            EvolutionGearTier(35, "Professional Overcoat", "Authority", "Architectural tailored blazer, focused commanding presence.", "Sharp minimalist collar line"),
            EvolutionGearTier(50, "Elite Luxury Techwear", "Excellence", "Precision-cut silhouette, subtle smart cuff telemetry.", "Sleek illuminated lining"),
            EvolutionGearTier(75, "Master Executive Apparel", "Sovereignty", "Refined executive technical silhouette, effortless presence.", "Refined micro-lighting"),
            EvolutionGearTier(100, "Legend Ascendant Presence", "Timeless", "Timeless monochrome perfection. Complete mastery over focus and time.", "Ascendant subtle glow")
        )
    }

    // Badges: every status is derived from real progression (streak / XP / unlocks)
    val badges = remember(unlockedBadges, momentumDays, userTotalXp, completedTasksCount) {
        fun status(unmetLabel: String, met: Boolean, badgeId: String): String = when {
            met || badgeId in unlockedBadges -> "Unlocked"
            else -> unmetLabel
        }
        listOf(
            EvolutionBadge(
                "first_mission", "First Mission", "The Spark", Icons.Default.Bolt, "Rare",
                status("Complete 1 task", completedTasksCount > 0, "first_mission"),
                "You conquered static inertia and finished your first mission."
            ),
            EvolutionBadge(
                "streak_7", "7 Day Streak", "The Baseline", Icons.Default.LocalFireDepartment, "Epic",
                status("$momentumDays/7 days", momentumDays >= 7, "streak_7"),
                "Seven days of unbroken momentum."
            ),
            EvolutionBadge(
                "streak_30", "30 Day Streak", "The Shift", Icons.Default.AutoAwesome, "Legendary",
                status("$momentumDays/30 days", momentumDays >= 30, "streak_30"),
                "Thirty days of daily execution."
            ),
            EvolutionBadge(
                "xp_500", "500 XP", "Initiate", Icons.Default.Star, "Rare",
                status("$userTotalXp/500 XP", userTotalXp >= 500, "xp_500"),
                "Amassed 500 units of deliberate momentum."
            ),
            EvolutionBadge(
                "early_bird", "Dawn Walker", "Pre 08:00 AM", Icons.Default.SelfImprovement, "Epic",
                status("Focus before 08:00", false, "early_bird"),
                "Won the morning before distractions could interfere."
            ),
            EvolutionBadge(
                "night_warrior", "Night Warrior", "Zero Postponing", Icons.Default.Shield, "Rare",
                status("Finish every task today", false, "night_warrior"),
                "Closed out the day with zero postponed priorities."
            )
        )
    }

    CompositionLocalProvider(LocalLevelThemeColors provides themeColors) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(themeColors.screenBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("journey_screen")
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp, vertical = 14.dp)
        ) {
            // ------------------------------------------------------------------
            // Live System state: quest progress, reset countdown, directive voice
            // ------------------------------------------------------------------
            // Fixed success green: reads as "cleared" against every palette, unlike
            // the global accent alias which follows the app theme colour.
            val sageGreen = Color(0xFF3DDC97)
            val penaltyRed = Color(0xFFFF4D6D)
            val rewardXp = 140

            val questTotal = todayTasks.size
            val questDone = todayTasks.count { it.isCompleted }
            val questCleared = questTotal > 0 && questDone >= questTotal
            val activeDaysThisWeek = weekActivity.count { it.active }
            val nextGearTier = remember(level) { evolutionTiers.firstOrNull { it.levelReq > level } }

            var titleMenuOpen by remember { mutableStateOf(false) }

            val animatedQuestProgress by animateFloatAsState(
                targetValue = if (questTotal == 0) 0f else questDone.toFloat() / questTotal.toFloat(),
                animationSpec = spring(stiffness = 220f, dampingRatio = 0.9f),
                label = "quest_progress_anim"
            )

            // Seconds until the System closes the day (local midnight).
            var resetSeconds by remember { mutableIntStateOf(secondsUntilMidnight()) }
            LaunchedEffect(Unit) {
                while (true) {
                    resetSeconds = secondsUntilMidnight()
                    delay(1000)
                }
            }
            val countdownText = "%02d:%02d:%02d".format(
                resetSeconds / 3600,
                (resetSeconds % 3600) / 60,
                resetSeconds % 60
            )

            val streakVerdict = when {
                weekActivity.isEmpty() -> "NO DATA"
                activeDaysThisWeek >= weekActivity.size -> "UNBROKEN"
                activeDaysThisWeek * 2 >= weekActivity.size -> "ON TRACK"
                activeDaysThisWeek >= 1 -> "AT RISK"
                else -> "STALLED"
            }

            val directiveText = when {
                questCleared ->
                    "Daily quest cleared. The system logged today as earned — " +
                            "this is what a stronger version of you looks like."
                questTotal > 0 && resetSeconds < 3 * 3600 ->
                    "Time critical. ${questTotal - questDone} quest" +
                            (if (questTotal - questDone == 1) "" else "s") +
                            " still open and the reset closes in $countdownText. " +
                            "Execute now or forfeit the chain."
                questTotal > 0 ->
                    "${questTotal - questDone} quests stand between you and today's " +
                            "reward. Clear the first one — momentum does the rest."
                totalFocusMinutes == 0 ->
                    "Focus protocol vacant. One 25-minute block is all it takes " +
                            "to register today as executed."
                else ->
                    "The system does not negotiate and it does not flatter. " +
                            "It only records what you actually did."
            }

            // =========================================================================
            // 1. SYSTEM STATUS BAR — live clock, "system online", reset countdown
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary.copy(alpha = pulseGlow))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "THE SYSTEM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 2.4.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ONLINE · DAY $momentumDays",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.6.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RESETS IN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.6.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = countdownText,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = themeColors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Return Home",
                            tint = TextPureWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // 2. HUNTER CARD — identity, level, XP and today's raw numbers
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(themeColors, 28.dp)
                    .padding(18.dp)
            ) {
                // Fixed heights keep every block tight: the avatar and the oversized
                // level numerals both reserve more vertical space than they draw.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IdentityProfilePhoto(
                        photoPath = customAvatarPath,
                        onPickImageUri = onUploadCustomAvatar,
                        onCaptureBitmap = onCaptureCustomAvatarBitmap ?: {},
                        onRemovePhoto = onClearCustomAvatar,
                        size = 70.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profileName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.2.sp
                                ),
                                color = TextPureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { showNameEditDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Name",
                                    tint = themeColors.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(themeColors.primary.copy(alpha = 0.14f))
                                    .border(
                                        1.dp,
                                        themeColors.primary.copy(alpha = 0.34f),
                                        RoundedCornerShape(50)
                                    )
                                    .clickable { titleMenuOpen = true }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = identityTitle.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        letterSpacing = 1.4.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = themeColors.primary
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = null,
                                    tint = themeColors.primary,
                                    modifier = Modifier.size(11.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = titleMenuOpen,
                                onDismissRequest = { titleMenuOpen = false }
                            ) {
                                IdentityTitle.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.label,
                                                color = if (option == identityTitle) themeColors.primary else TextPureWhite,
                                                fontSize = 13.sp,
                                                fontWeight = if (option == identityTitle) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            titleMenuOpen = false
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onUpdateIdentityTitle(option)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "LV",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$level",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 30.sp,
                                lineHeight = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = dynamicCurrentForm,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextPureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "NEXT · $dynamicNextEvolution",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = "$currentLevelXp/$xpPerLevel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = themeColors.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(themeColors.trackBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedLevelProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(themeColors.progressBarBrush)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TOTAL $animatedXp XP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            letterSpacing = 1.2.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextMuted
                    )
                    Text(
                        text = if (xpToNextLevel > 0) "+$xpToNextLevel XP → LV ${level + 1}" else "MAXIMUM TIER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = themeColors.secondary
                    )
                }

                if (nextGearTier != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "NEXT FORM · LV ${nextGearTier.levelReq} — ${nextGearTier.name.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            lineHeight = 13.sp,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SystemStatChip(
                        icon = Icons.Filled.LocalFireDepartment,
                        value = "$momentumDays",
                        label = "DAY STREAK",
                        accent = themeColors.primary,
                        track = themeColors.innerSurface,
                        modifier = Modifier.weight(1f)
                    )
                    SystemStatChip(
                        icon = Icons.Filled.Bolt,
                        value = "$totalFocusMinutes",
                        label = "FOCUS MIN",
                        accent = themeColors.secondary,
                        track = themeColors.innerSurface,
                        modifier = Modifier.weight(1f)
                    )
                    SystemStatChip(
                        icon = Icons.Filled.CheckCircle,
                        value = "$completedTasksCount",
                        label = "QUESTS DONE",
                        accent = themeColors.primary,
                        track = themeColors.innerSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================================================================
            // 3. DAILY QUEST — the core loop: real tasks, real deadline, real stakes
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(themeColors, 26.dp)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY QUEST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 2.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (questCleared) sageGreen else themeColors.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "PREPARATION TO BECOME STRONG",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            ),
                            color = TextPureWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$questDone/$questTotal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (questCleared) sageGreen else themeColors.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (questCleared) "CLEARED" else "REMAINING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.4.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(themeColors.trackBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedQuestProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (questCleared) {
                                    Brush.horizontalGradient(listOf(sageGreen, sageGreen))
                                } else {
                                    themeColors.progressBarBrush
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (todayTasks.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(themeColors.innerSurface.copy(alpha = 0.7f))
                            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(16.dp))
                            .clickable { onContinueMission(null) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NorthEast,
                            contentDescription = null,
                            tint = themeColors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NO QUESTS ASSIGNED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.6.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextPureWhite
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Tap to assign today's preparation.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todayTasks.take(6).forEach { task ->
                            SystemQuestRow(
                                task = task,
                                accent = themeColors.primary,
                                track = themeColors.innerSurface,
                                onToggle = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onToggleTask(task)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (questCleared) sageGreen.copy(alpha = 0.10f)
                            else penaltyRed.copy(alpha = 0.10f)
                        )
                        .border(
                            1.dp,
                            if (questCleared) sageGreen.copy(alpha = 0.32f)
                            else penaltyRed.copy(alpha = 0.32f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (questCleared) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (questCleared) sageGreen else penaltyRed,
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (questCleared) "QUEST CLEARED" else "PENALTY PROTOCOL ARMED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 1.6.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (questCleared) sageGreen else penaltyRed
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = if (questCleared) {
                                "Reward unlocked: +$rewardXp XP banked and the chain extended. " +
                                        "The system recorded today as earned."
                            } else if (questTotal == 0) {
                                "A day without a quest cannot be counted. The streak only " +
                                        "survives days that were actually executed."
                            } else {
                                "Clear every quest before $countdownText or the momentum " +
                                        "streak resets to day one. The system does not grade intentions."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================================================================
            // 4. MOMENTUM — the last seven days, earned or not
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(themeColors, 26.dp)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MOMENTUM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 2.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = themeColors.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$momentumDays-day chain",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPureWhite
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = themeColors.primary.copy(alpha = pulseGlow),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekActivity.forEach { day ->
                        SystemDayCell(
                            day = day,
                            accent = themeColors.primary,
                            track = themeColors.innerSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$activeDaysThisWeek of ${weekActivity.size} days executed",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = streakVerdict,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (activeDaysThisWeek >= weekActivity.size / 2) sageGreen else penaltyRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================================================================
            // 5. SYSTEM DIRECTIVE — the voice that will not let you off the hook
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                themeColors.primary.copy(alpha = 0.16f),
                                themeColors.cardBackground.copy(alpha = 0.82f),
                                Color.White.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        themeColors.primary.copy(alpha = 0.30f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "[ SYSTEM ]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 2.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = themeColors.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary.copy(alpha = pulseGlow))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = directiveText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 21.sp
                    ),
                    color = TextPureWhite
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(themeColors.primary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = motivationalMessages[activeMotivationalIndex],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================================================================
            // 6. IDENTITY ATTRIBUTES — what the quests are quietly building
            // =========================================================================
            SystemSectionLabel(
                title = "IDENTITY ATTRIBUTES",
                hint = "LEVEL ${level}",
                accent = themeColors.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(themeColors, 26.dp)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                identityAttributes.forEach { attribute ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = attribute.name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    letterSpacing = 1.6.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextPureWhite
                            )
                            Text(
                                text = "${(attribute.value * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = themeColors.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(7.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(themeColors.trackBackground)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(attribute.value)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(themeColors.progressBarBrush)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================================================================
            // 7. TITLES & MEDALS
            // =========================================================================
            SystemSectionLabel(
                title = "TITLES & MEDALS",
                hint = "${badges.count { it.unlockedDate == "Unlocked" }}/${badges.size} EARNED",
                accent = themeColors.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(badges) { badge ->
                    BadgeCard(
                        badge = badge,
                        themeColors = themeColors,
                        onClick = { selectedBadgeForInspection = badge }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // 8. DAILY REWARD + MISSION CTA
            // =========================================================================
            if (!claimedTodayXp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassPanel(themeColors, 24.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary.copy(alpha = 0.16f))
                            .border(1.dp, themeColors.primary.copy(alpha = 0.32f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = themeColors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY REWARD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+$rewardXp XP · claiming also marks today as active",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClaimXp(rewardXp)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColors.primary,
                            contentColor = Color(0xFF08040A)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "CLAIM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp
                            )
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassPanel(themeColors, 24.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = sageGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "DAILY REWARD CLAIMED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 1.8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = sageGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Come back tomorrow and extend the chain.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(themeColors, 24.dp)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (activeMission != null) "ACTIVE MISSION" else "NO ACTIVE MISSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = activeMission?.title ?: "Assign one mission and the system will track every hour you give it.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 19.sp
                    ),
                    color = TextPureWhite
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onContinueMission(activeMission)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.primary,
                        contentColor = Color(0xFF08040A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (activeMission != null) "CONTINUE MISSION" else "ASSIGN TODAY'S QUEST",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            fontSize = 12.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = arrowOffset.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showLevelUpCeremony = true
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PREVIEW NEXT EVOLUTION CEREMONY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.5.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

        }

        // =========================================================================
        // CINEMATIC LEVEL UP MODAL
        // =========================================================================
        if (showLevelUpCeremony) {
            CinematicLevelUpModal(
                fromLevel = level,
                toLevel = level + 1,
                unlockedTitle = nextEvolutionTitle,
                themeColors = themeColors,
                onDismiss = { showLevelUpCeremony = false }
            )
        }

        // Name Edit Dialog
        if (showNameEditDialog) {
            NameEditDialog(
                currentName = profileName,
                themeColors = themeColors,
                onConfirm = {
                    onUpdateProfileName(it)
                    showNameEditDialog = false
                },
                onDismiss = { showNameEditDialog = false }
            )
        }

        // Badge Inspection Dialog
        if (selectedBadgeForInspection != null) {
            BadgeInspectionDialog(
                badge = selectedBadgeForInspection!!,
                themeColors = themeColors,
                onDismiss = { selectedBadgeForInspection = null }
            )
        }
    }
}
}

/**
 * Collectible RPG Badge Card.
 */
@Composable
private fun BadgeCard(
    badge: EvolutionBadge,
    themeColors: LevelThemeColors,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(themeColors.cardBackground)
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(themeColors.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = badge.title,
                tint = themeColors.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = badge.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = TextPureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = badge.rarity,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = themeColors.accentGlow
        )
    }
}

/**
 * Cinematic Level Up Modal (Everything pauses, dark background, blue particles, glowing avatar).
 * No confetti — completely cinematic and disciplined.
 */
@Composable
private fun CinematicLevelUpModal(
    fromLevel: Int,
    toLevel: Int,
    unlockedTitle: String,
    themeColors: LevelThemeColors,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Theme ambient particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(themeColors.primary.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.45f),
                        radius = w * 0.8f
                    ),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.8f
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(26.dp)
            ) {
                // Glowing beacon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(themeColors.primary.copy(alpha = 0.2f))
                        .border(1.5.dp, themeColors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = themeColors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "LEVEL UP",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 3.sp,
                        fontSize = 32.sp
                    ),
                    color = TextPureWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Level $fromLevel → $toLevel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp
                    ),
                    color = themeColors.primary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Unlock list
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(themeColors.innerSurface)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(18.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "UNLOCKED REWARDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = themeColors.accentGlow
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = themeColors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "New Identity Title: $unlockedTitle", color = TextPureWhite, fontSize = 13.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = themeColors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "New Identity Evolution Tier Unlocked", color = TextPureWhite, fontSize = 13.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = themeColors.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "+25 Maximum Energy & Focus Capacity", color = TextPureWhite, fontSize = 13.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(52.dp)
                ) {
                    Text(
                        text = "Continue Evolution",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * Name Edit Dialog.
 */
@Composable
private fun NameEditDialog(
    currentName: String,
    themeColors: LevelThemeColors,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(themeColors.innerSurface)
                .border(1.dp, themeColors.primary.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Edit Identity Name",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColors.primary,
                    unfocusedBorderColor = themeColors.cardBorder,
                    focusedTextColor = TextPureWhite,
                    unfocusedTextColor = TextPureWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Cancel",
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onConfirm(text) },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary)
                ) {
                    Text(text = "Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Badge Inspection Dialog.
 */
@Composable
private fun BadgeInspectionDialog(
    badge: EvolutionBadge,
    themeColors: LevelThemeColors,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(themeColors.innerSurface)
                .border(1.dp, themeColors.primary.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(themeColors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = badge.title,
                    tint = themeColors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPureWhite
            )

            Text(
                text = "${badge.rarity} • ${badge.subtitle}",
                style = MaterialTheme.typography.bodySmall,
                color = themeColors.accentGlow
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = badge.lore,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
// ==========================================================================
// THE SYSTEM PANEL — shared building blocks
// ==========================================================================

/** Frosted glass surface: layered translucency, hairline light edge, palette glow. */
private fun Modifier.glassPanel(themeColors: LevelThemeColors, radius: Dp = 26.dp): Modifier = this
    .clip(RoundedCornerShape(radius))
    .background(
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.075f),
                themeColors.cardBackground.copy(alpha = 0.74f),
                Color.White.copy(alpha = 0.028f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.20f),
                themeColors.primary.copy(alpha = 0.26f),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        shape = RoundedCornerShape(radius)
    )

/** Monospace section label with an optional stat hint on the same baseline. */
@Composable
private fun SystemSectionLabel(title: String, hint: String? = null, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                letterSpacing = 2.2.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (hint != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = accent,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}

/** Compact stat read-out used inside the hunter card. */
@Composable
private fun SystemStatChip(
    icon: ImageVector,
    value: String,
    label: String,
    accent: Color,
    track: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(track.copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            ),
            color = TextPureWhite,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                letterSpacing = 1.1.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = TextMuted,
            maxLines = 1
        )
    }
}

/** One real task rendered as a System quest line. Tap to clear it. */
@Composable
private fun SystemQuestRow(
    task: TaskEntity,
    accent: Color,
    track: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(track.copy(alpha = 0.80f))
            .border(
                1.dp,
                if (task.isCompleted) accent.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) accent else Color.Transparent)
                .border(
                    1.5.dp,
                    if (task.isCompleted) accent else Color.White.copy(alpha = 0.22f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF08040A),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            color = if (task.isCompleted) TextMuted else TextPureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = if (task.isCompleted) "CLEARED" else "+${if (task.isMission) 100 else 20} XP",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = if (task.isCompleted) accent else TextMuted
        )
    }
}

/** One day of the seven-day momentum strip. */
@Composable
private fun SystemDayCell(
    day: DayActivity,
    accent: Color,
    track: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = if (day.isToday) accent else TextMuted
        )

        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (day.active) accent.copy(alpha = 0.16f) else track.copy(alpha = 0.85f))
                .border(
                    width = if (day.isToday) 1.5.dp else 1.dp,
                    color = when {
                        day.isToday -> accent
                        day.active -> accent.copy(alpha = 0.32f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${day.dayOfMonth}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (day.active) TextPureWhite else TextMuted
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (day.active) accent else Color.Transparent)
        )
    }
}

/** Seconds remaining until the System's daily reset (local midnight). */
private fun secondsUntilMidnight(): Int {
    val now = java.util.Calendar.getInstance()
    val next = (now.clone() as java.util.Calendar).apply {
        add(java.util.Calendar.DAY_OF_YEAR, 1)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return ((next.timeInMillis - now.timeInMillis) / 1000L).toInt()
}
