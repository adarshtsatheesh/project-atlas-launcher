package com.example.data.repository

import com.example.data.local.dao.NoteDao
import com.example.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun saveNote(pageIndex: Int, title: String, content: String) {
        val existing = noteDao.getNoteByPageIndex(pageIndex)
        if (existing != null) {
            noteDao.updateNote(
                existing.copy(
                    title = title,
                    content = content,
                    updatedAt = System.currentTimeMillis()
                )
            )
        } else {
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = pageIndex,
                    title = title,
                    content = content,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun createNewPage(): Int {
        val notes = noteDao.getAllNotes().first()
        val nextIndex = if (notes.isEmpty()) 0 else (notes.maxOf { it.pageIndex } + 1)
        noteDao.insertNote(
            NoteEntity(
                pageIndex = nextIndex,
                title = "Page ${nextIndex + 1}",
                content = "",
                updatedAt = System.currentTimeMillis()
            )
        )
        return nextIndex
    }

    suspend fun seedInitialNotesIfEmpty() {
        val count = noteDao.getAllNotes().first().size
        if (count == 0) {
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 0,
                    title = "Project Atlas Vision",
                    content = "“Bridge the gap between planning and doing.”\n\nChecklist:\n[x] Distraction-free launcher design system\n[ ] Implement 120Hz physics spring animations\n[ ] Infinite Notebook auto-save engine\n\n• Principle: Action over endless contemplation\n• Minimal luxury: Deep charcoal & Soft Sage Green\n• Every pixel exists with singular intention.",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 1,
                    title = "Architecture & Sprint Focus",
                    content = "Sprint 42 Deliverables:\n• Zero-latency local Room database\n• Pager-based gesture navigation\n• Category chips: Work, Social, Media, Tools\n\nKey notes:\n[x] Seamless gesture dispatching\n[ ] Custom adaptive monochrome icons\n• Status: Production ready for startup launch.",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 2,
                    title = "Psychological Impact Matrix",
                    content = "Why typical launchers fail:\n1. Endless notification badges produce dopamine loops\n2. Widget clutter increases cognitive friction\n3. Cluttered app drawers encourage mindless scrolling\n\nAtlas Solution:\n• 1 Primary Mission card with single large Execute button\n• 4 Essential dock apps (Phone, Messages, Browser, Camera)\n• Infinite Notes just one swipe away.",
                    updatedAt = System.currentTimeMillis()
                )
            )
            // Seed a few more pages up to 8 as mentioned in the prompt example ("01 / 08")
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 3,
                    title = "Design Tokens & Typography",
                    content = "Background: #0A0A0A\nCards: #161616\nSecondary: #111111\nAccent: Soft Sage Green (#7FA28C)\nSecondary Accent: Warm Beige (#E6DFD5)\nText: Pure White & Secondary #A2A2A2",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 4,
                    title = "Daily Retrospective",
                    content = "Questions to ask at sunset:\n• Did I move the needle on my core mission?\n• Where was energy dissipated?\n• What is the single focus for tomorrow?",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 5,
                    title = "Reading List & References",
                    content = "• The Design of Everyday Things — Don Norman\n• Essentialism — Greg McKeown\n• Deep Work — Cal Newport\n• Nothing OS & Linear Design System RFCs",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 6,
                    title = "Hardware & Sensor Notes",
                    content = "Haptic feedback rules:\n• Subtle click on checkbox toggle\n• Soft spring resistance on edge swipes\n• Breathing pulse synchronization in Focus Mode",
                    updatedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNote(
                NoteEntity(
                    pageIndex = 7,
                    title = "Future Horizon Ideas",
                    content = "• AI-driven task duration estimation\n• Ambient soundscapes (white noise, rain, forest)\n• Biometric focus lock",
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
