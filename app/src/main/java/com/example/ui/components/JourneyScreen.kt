package com.example.ui.components

import android.net.Uri
import com.example.avatar.AvatarConfig
import com.example.avatar.AvatarCustomizer
import com.example.avatar.AvatarRenderer
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
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AvatarStyle
import com.example.ui.viewmodel.IdentityTitle
import kotlinx.coroutines.delay

// Futuristic Psychology-First Theme Palette: Deep Black with Subtle Electric Azure Accents
private val EvoBackground = Color(0xFF020407)
private val EvoCardBase = Color(0xFF070B14)
private val EvoCardElevated = Color(0xFF0B101E)
private val EvoBorderSubtle = Color(0x2200C8FF)
private val ElectricAzure = Color(0xFF00C8FF)
private val NeonCobalt = Color(0xFF0070F3)
private val CyanGlow = Color(0xFF38BDF8)

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
    avatarConfig: AvatarConfig = AvatarConfig.DEFAULT_MASCULINE,
    avatarStyle: AvatarStyle = AvatarStyle.MINIMAL_HOODIE,
    customAvatarPath: String? = null,
    identityTitle: IdentityTitle = IdentityTitle.THE_ARCHITECT,
    identityVocation: String = "Developer",
    currentFormTitle: String = "Focused Starter",
    nextEvolutionTitle: String = "Level 2 • Disciplined Builder",
    momentumDays: Int = 1,
    onClaimXp: (Int) -> Unit = {},
    onUpdateAvatarConfig: (AvatarConfig) -> Unit = {},
    onUpdateAvatarStyle: (AvatarStyle) -> Unit = {},
    onUpdateIdentityTitle: (IdentityTitle) -> Unit = {},
    onUpdateProfileName: (String) -> Unit = {},
    onUploadCustomAvatar: (Uri) -> Unit = {},
    onClearCustomAvatar: () -> Unit = {},
    onContinueMission: (TaskEntity?) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    // Dialog & Ceremony States
    var showAvatarCustomizer by remember { mutableStateOf(false) }
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

    // Badges
    val badges = remember {
        listOf(
            EvolutionBadge("first_mission", "First Mission", "The Spark", Icons.Default.Bolt, "Rare", "Day 1", "You conquered static inertia and finished your first mission."),
            EvolutionBadge("streak_7", "7 Day Streak", "The Baseline", Icons.Default.LocalFireDepartment, "Epic", "Active", "Seven days of unbroken momentum."),
            EvolutionBadge("streak_30", "30 Day Streak", "The Shift", Icons.Default.AutoAwesome, "Legendary", "In Progress", "Thirty days of daily execution."),
            EvolutionBadge("xp_500", "500 XP", "Initiate", Icons.Default.Star, "Rare", "Unlocked", "Amassed 500 units of deliberate momentum."),
            EvolutionBadge("early_bird", "Dawn Walker", "Pre 08:00 AM", Icons.Default.SelfImprovement, "Epic", "Active", "Won the morning before distractions could interfere."),
            EvolutionBadge("night_warrior", "Night Warrior", "Zero Postponing", Icons.Default.Shield, "Rare", "Unlocked", "Closed out the day with zero postponed priorities.")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EvoBackground)
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
            // =========================================================================
            // 1. TOP HEADER: IDENTITY EVOLUTION & USER TITLE
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElectricAzure)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IDENTITY EVOLUTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyanGlow
                    )
                }

                // Return Home
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EvoCardBase)
                        .border(1.dp, EvoBorderSubtle, RoundedCornerShape(20.dp))
                        .clickable { onClose() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("journey_close_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Return Home",
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Name and Identity Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showNameEditDialog = true }
                    ) {
                        Text(
                            text = profileName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$currentFormTitle • $identityVocation",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = CyanGlow
                    )
                }

                // Customize Identity Avatar Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElectricAzure)
                        .clickable { showAvatarCustomizer = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("open_customizer_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Customize",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // 2. HUGE CENTERED AVATAR (Takes most of the screen, very premium)
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF090E1A), Color(0xFF04060C))
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(
                            listOf(
                                ElectricAzure.copy(alpha = 0.8f),
                                Color(0x3300C8FF),
                                Color(0x080070F3)
                            )
                        ),
                        RoundedCornerShape(26.dp)
                    )
                    .clickable { showAvatarCustomizer = true }
                    .testTag("character_card")
            ) {
                // Modular Stylized Digital Avatar
                AvatarRenderer(
                    config = avatarConfig,
                    level = level,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    enableAnimation = true
                )

                // Customize Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC080C16))
                        .border(1.dp, Color(0x3300C8FF), RoundedCornerShape(12.dp))
                        .clickable { showAvatarCustomizer = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Customize",
                            tint = CyanGlow,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CUSTOMIZE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = CyanGlow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // 3. LEVEL & FORM EVOLUTION CARD
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(EvoCardBase)
                    .border(1.dp, EvoBorderSubtle, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LEVEL $level",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 22.sp,
                                letterSpacing = 1.sp
                            ),
                            color = TextPureWhite
                        )
                        Text(
                            text = "\"${identityTitle.label}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.5.sp
                            ),
                            color = ElectricAzure
                        )
                    }

                    // XP Progress Indicator
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$animatedXp / $currentLevelMaxXp XP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            ),
                            color = TextPureWhite
                        )
                        Text(
                            text = if (xpToNextLevel > 0) "$xpToNextLevel XP until next level" else "Level Progress Maxed",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animated glowing XP bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF101624))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedLevelProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonCobalt, ElectricAzure, CyanGlow)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Form Evolution: Current Form -> Next Evolution
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C111E))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT FORM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                        Text(
                            text = dynamicCurrentForm,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = TextPureWhite
                        )
                    }

                    // Animated arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ElectricAzure,
                        modifier = Modifier
                            .offset(x = arrowOffset.dp)
                            .size(16.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "NEXT EVOLUTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = CyanGlow
                        )
                        Text(
                            text = dynamicNextEvolution,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = ElectricAzure
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cinematic Level Up Demo Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1800C8FF))
                        .border(1.dp, Color(0x3300C8FF), RoundedCornerShape(12.dp))
                        .clickable {
                            showLevelUpCeremony = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Experience Cinematic Level Up Ceremony",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = CyanGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // 4. MOMENTUM SECTION (Instead of "Streak")
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(EvoCardBase)
                    .border(1.dp, EvoBorderSubtle, RoundedCornerShape(18.dp))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ElectricAzure.copy(alpha = 0.16f))
                        .border(1.dp, ElectricAzure, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Momentum",
                        tint = ElectricAzure,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MOMENTUM $momentumDays DAYS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        ),
                        color = TextPureWhite
                    )
                    Text(
                        text = if (momentumDays <= 1) "Day 1 of deliberate momentum. Forge your baseline identity." else "$momentumDays consecutive days of executing without breaking your identity.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // =========================================================================
            // 5. IDENTITY ATTRIBUTES: ELEGANT ANIMATED BARS
            // =========================================================================
            Text(
                text = "IDENTITY ATTRIBUTES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(EvoCardBase)
                    .border(1.dp, EvoBorderSubtle, RoundedCornerShape(22.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                identityAttributes.forEach { attr ->
                    IdentityAttributeRow(attribute = attr)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // =========================================================================
            // 6. TODAY'S GROWTH (REPLACES BORING STATISTICS)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S GROWTH",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = TextSecondary
                )

                if (!claimedTodayXp) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricAzure.copy(alpha = 0.2f))
                            .border(1.dp, ElectricAzure, RoundedCornerShape(12.dp))
                            .clickable {
                                onClaimXp(50)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Claim +50 XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = ElectricAzure
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                EvolutionGrowthCard(
                    xp = "+50 XP",
                    title = "Morning Mission",
                    subtitle = "Completed before friction could accumulate",
                    icon = Icons.Default.Bolt,
                    completed = true
                )
                EvolutionGrowthCard(
                    xp = "+40 XP",
                    title = "Deep Focus (45 min)",
                    subtitle = "Zero distraction cognitive flow state",
                    icon = Icons.Default.SelfImprovement,
                    completed = totalFocusMinutes >= 25
                )
                EvolutionGrowthCard(
                    xp = "+30 XP",
                    title = "Workout",
                    subtitle = "Physical stamina and energy baseline renewed",
                    icon = Icons.Default.FitnessCenter,
                    completed = true
                )
                EvolutionGrowthCard(
                    xp = "+20 XP",
                    title = "Reading (20 min)",
                    subtitle = "High-leverage knowledge synthesized",
                    icon = Icons.Default.MenuBook,
                    completed = notesCount > 0
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // =========================================================================
            // 7. MOTIVATIONAL SECTION: DAILY PERSONALIZED MESSAGE
            // =========================================================================
            Text(
                text = "DAILY REFLECTION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(EvoCardBase)
                    .border(1.dp, ElectricAzure.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "\"${motivationalMessages[activeMotivationalIndex]}\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        color = TextPureWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Psychological Identity Anchor",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyanGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // =========================================================================
            // 8. AVATAR EVOLUTION TIERS (REALISTIC MODERN PROGRESSION)
            // =========================================================================
            Text(
                text = "AVATAR EVOLUTION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Realistic modern apparel progression as discipline compounds.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                evolutionTiers.forEach { tier ->
                    val isUnlocked = level >= tier.levelReq
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isUnlocked) EvoCardElevated else EvoCardBase)
                            .border(
                                1.dp,
                                if (isUnlocked) ElectricAzure.copy(alpha = 0.5f) else Color(0x18FFFFFF),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) ElectricAzure.copy(alpha = 0.2f) else Color(0x11FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L${tier.levelReq}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isUnlocked) ElectricAzure else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tier.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                ),
                                color = if (isUnlocked) TextPureWhite else TextSecondary
                            )
                            Text(
                                text = tier.description,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = TextMuted
                            )
                        }

                        if (isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Unlocked",
                                tint = ElectricAzure,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Collectible RPG Badges
            Text(
                text = "MOMENTUM BADGES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(badges) { badge ->
                    BadgeCard(
                        badge = badge,
                        onClick = { selectedBadgeForInspection = badge }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // =========================================================================
        // CINEMATIC LEVEL UP MODAL
        // =========================================================================
        if (showLevelUpCeremony) {
            CinematicLevelUpModal(
                fromLevel = level,
                toLevel = level + 1,
                unlockedTitle = nextEvolutionTitle,
                onDismiss = { showLevelUpCeremony = false }
            )
        }

        // Modular Avatar Customizer Studio
        if (showAvatarCustomizer) {
            AvatarCustomizer(
                initialConfig = avatarConfig,
                userLevel = level,
                onSave = {
                    onUpdateAvatarConfig(it)
                    showAvatarCustomizer = false
                },
                onDismiss = { showAvatarCustomizer = false }
            )
        }

        // Name Edit Dialog
        if (showNameEditDialog) {
            NameEditDialog(
                currentName = profileName,
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
                onDismiss = { selectedBadgeForInspection = null }
            )
        }
    }
}

/**
 * Elegant Animated Identity Attribute Row.
 */
@Composable
private fun IdentityAttributeRow(attribute: IdentityAttributeItem) {
    val animatedPercent by animateFloatAsState(
        targetValue = attribute.value,
        animationSpec = spring(stiffness = 200f, dampingRatio = 0.85f),
        label = "attr_anim_${attribute.name}"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = attribute.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = TextPureWhite
            )
            Text(
                text = "${(animatedPercent * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                color = ElectricAzure
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Minimalist animated progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(Color(0xFF101726))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercent)
                    .height(5.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0070F3), Color(0xFF00C8FF))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = attribute.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
            color = TextMuted
        )
    }
}

/**
 * Rewarding Growth Card.
 */
@Composable
private fun EvolutionGrowthCard(
    xp: String,
    title: String,
    subtitle: String,
    icon: ImageVector,
    completed: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EvoCardBase)
            .border(
                1.dp,
                if (completed) ElectricAzure.copy(alpha = 0.35f) else Color(0x18FFFFFF),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (completed) ElectricAzure.copy(alpha = 0.2f) else Color(0x11FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (completed) ElectricAzure else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = TextPureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = TextMuted
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (completed) ElectricAzure.copy(alpha = 0.15f) else Color(0x18FFFFFF))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = xp,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = if (completed) ElectricAzure else TextMuted
            )
        }
    }
}

/**
 * Collectible RPG Badge Card.
 */
@Composable
private fun BadgeCard(
    badge: EvolutionBadge,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(EvoCardBase)
            .border(1.dp, EvoBorderSubtle, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ElectricAzure.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = badge.title,
                tint = ElectricAzure,
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
            color = CyanGlow
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
            // Blue ambient particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricAzure.copy(alpha = 0.28f), Color.Transparent),
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
                        .background(ElectricAzure.copy(alpha = 0.2f))
                        .border(1.5.dp, ElectricAzure, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricAzure,
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
                    color = ElectricAzure
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Unlock list
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF090E18))
                        .border(1.dp, Color(0x3300C8FF), RoundedCornerShape(18.dp))
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
                        color = CyanGlow
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "New Identity Title: $unlockedTitle", color = TextPureWhite, fontSize = 13.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "New Avatar Clothing Tier Unlocked", color = TextPureWhite, fontSize = 13.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "+25 Maximum Energy & Focus Capacity", color = TextPureWhite, fontSize = 13.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
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
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF090E18))
                .border(1.dp, ElectricAzure.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
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
                    focusedBorderColor = ElectricAzure,
                    unfocusedBorderColor = Color(0x3300C8FF),
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
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure)
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
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF090E18))
                .border(1.dp, ElectricAzure.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(ElectricAzure.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = badge.title,
                    tint = ElectricAzure,
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
                color = CyanGlow
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
                colors = ButtonDefaults.buttonColors(containerColor = ElectricAzure),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
