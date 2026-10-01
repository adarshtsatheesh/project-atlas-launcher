package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NoteEntity
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
import com.example.ui.theme.WarmSand
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun InfiniteNotesScreen(
    notes: List<NoteEntity>,
    initialPage: Int = 0,
    onSaveNote: (pageIndex: Int, title: String, content: String) -> Unit,
    onNewPage: ((Int) -> Unit) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPages = maxOf(notes.size, 8)
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage && initialPage < totalPages) {
            pagerState.scrollToPage(initialPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("infinite_notes_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Minimal luxury Notebook Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to home
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
                        .clickable { onClose() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("notes_back_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Launcher",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Atlas Home",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                // Signature Page Indicator (e.g. "01 / 08")
                val currentPageFormatted = String.format(Locale.US, "%02d", pagerState.currentPage + 1)
                val totalPagesFormatted = String.format(Locale.US, "%02d", totalPages)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("notes_page_indicator")
                ) {
                    Text(
                        text = "$currentPageFormatted / $totalPagesFormatted",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = CrimsonElectric
                    )
                }

                // New page action
                IconButton(
                    onClick = {
                        onNewPage { newPageIndex ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(newPageIndex)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GlassCardBackgroundBrush)
                        .border(1.dp, GlassBorderBrush, CircleShape)
                        .testTag("notes_new_page_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Page",
                        tint = CrimsonElectric,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Minimal Subtle Paper Spine Guide
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AtlasBorder)
            )

            // Horizontal Pager: Infinite Notebook Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("notes_horizontal_pager")
            ) { pageIndex ->
                val note = notes.firstOrNull { it.pageIndex == pageIndex }
                NotebookPage(
                    pageIndex = pageIndex,
                    initialTitle = note?.title ?: "Page ${pageIndex + 1}",
                    initialContent = note?.content ?: "",
                    onContentChange = { newTitle, newContent ->
                        onSaveNote(pageIndex, newTitle, newContent)
                    },
                    onTriggerSnack = { msg ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                )
            }

            // Bottom Notebook Quick Accessory Bar
            NotebookAccessoryBar(
                currentPage = pagerState.currentPage,
                totalPages = totalPages,
                onPreviousPage = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    } else {
                        onClose()
                    }
                },
                onNextPage = {
                    if (pagerState.currentPage < totalPages - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }
}

@Composable
private fun NotebookPage(
    pageIndex: Int,
    initialTitle: String,
    initialContent: String,
    onContentChange: (String, String) -> Unit,
    onTriggerSnack: (String) -> Unit
) {
    var title by remember(pageIndex) { mutableStateOf(initialTitle) }
    var content by remember(pageIndex) { mutableStateOf(initialContent) }
    var isSynced by remember { mutableStateOf(true) }

    // Keep state in sync if external note loads later, only if user hasn't edited
    LaunchedEffect(initialTitle, initialContent) {
        if (isSynced) {
            title = initialTitle
            content = initialContent
        }
    }

    // Debounce auto-save by 500ms so typing never hammers SQLite or triggers recomposition storms
    LaunchedEffect(title, content) {
        if (title != initialTitle || content != initialContent) {
            isSynced = false
            delay(500L)
            onContentChange(title, content)
            isSynced = true
        } else {
            isSynced = true
        }
    }

    // Immediate flush on page flip or screen close to guarantee zero data loss
    DisposableEffect(pageIndex) {
        onDispose {
            if (title != initialTitle || content != initialContent) {
                onContentChange(title, content)
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("notebook_page_$pageIndex")
    ) {
        // Subtle Notebook Page Date & Auto-save Status Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PAGE ${pageIndex + 1} • AUTO-SAVING",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = TextMuted
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSynced) SoftSageGreen else CrimsonElectric)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSynced) "Synced" else "Saving...",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSynced) SoftSageGreen else CrimsonElectric
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Title (Luxury minimal typography)
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            textStyle = TextStyle(
                color = TextPureWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 34.sp
            ),
            cursorBrush = SolidColor(SoftSageGreen),
            decorationBox = { innerTextField ->
                if (title.isBlank()) {
                    Text(
                        text = "Title...",
                        color = TextMuted,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_title_input_$pageIndex")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Distraction-free Body Editor
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            textStyle = TextStyle(
                color = WarmSand,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 27.sp
            ),
            cursorBrush = SolidColor(SoftSageGreen),
            decorationBox = { innerTextField ->
                if (content.isBlank()) {
                    Text(
                        text = "Start writing your thoughts, checklist items, or blueprints...\n\nSwipe horizontally to flip pages like a real notebook.",
                        color = TextMuted,
                        fontSize = 17.sp,
                        lineHeight = 27.sp
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .testTag("note_content_input_$pageIndex")
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun NotebookAccessoryBar(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(GlassCardBackgroundBrush)
                .border(1.dp, GlassBorderBrush, RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Page or Return to Home
            IconButton(
                onClick = onPreviousPage,
                enabled = true,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = if (currentPage > 0) "Previous Page" else "Return to Home",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Quick Formatting / Paper Tools
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Checklists",
                        tint = SoftSageGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Checklist",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.FormatListBulleted,
                        contentDescription = "Bullets",
                        tint = WarmSand,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Bullets",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Visuals",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Media",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            // Next Page
            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages - 1,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Page",
                    tint = if (currentPage < totalPages - 1) TextSecondary else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
