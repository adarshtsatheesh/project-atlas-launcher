package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.random.Random

/**
 * Procedural ambient soundscapes for Focus Mode.
 *
 * Instead of shipping (or faking) audio assets, this synthesises a different
 * noise texture per track on a background thread through [AudioTrack]:
 *
 *  - Forest      : soft band-limited wind with a slow 0.1 Hz swell
 *  - Rain        : bright white noise gently rolled off, with light flutter
 *  - Deep Space  : dark brownian rumble at low volume
 *  - Lo-fi Cafe  : pink-ish noise with a warm 0.3 Hz breathing modulation
 *
 * All output is deliberately quiet (roughly -20 dBFS) so it stays background ambience.
 */
class AmbientSoundPlayer {

    enum class Track(val label: String) {
        FOREST("Forest"),
        RAIN("Rain"),
        DEEP_SPACE("Deep Space"),
        LOFI_CAFE("Lo-fi Cafe")
    }

    private val sampleRate = 22050
    private val bufferSize = 2048

    private var worker: Thread? = null
    private var audioTrack: AudioTrack? = null

    @Volatile
    private var playing = false

    /** Starts (or restarts) the given soundscape. Safe to call when already running. */
    @Synchronized
    fun start(track: Track) {
        stopInternal()
        try {
            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackBuffer = maxOf(minBuffer, bufferSize * 2)

            val audio = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(trackBuffer)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (audio.state != AudioTrack.STATE_INITIALIZED) {
                audio.release()
                return
            }

            audioTrack = audio
            playing = true
            audio.play()

            worker = Thread({ renderLoop(audio, track) }, "atlas-ambient-audio").apply {
                isDaemon = true
                start()
            }
        } catch (e: Exception) {
            // Audio is a nice-to-have: never let it crash the focus screen.
            playing = false
        }
    }

    /** Stops playback but keeps the player reusable. */
    @Synchronized
    fun stop() {
        stopInternal()
    }

    /** Stops playback and releases the underlying audio resources. */
    @Synchronized
    fun release() {
        stopInternal()
    }

    private fun stopInternal() {
        playing = false
        worker?.let {
            try {
                it.join(400)
            } catch (e: Exception) {
                // Ignore: thread may already be gone
            }
        }
        worker = null

        audioTrack?.let { audio ->
            try {
                audio.pause()
                audio.flush()
            } catch (e: Exception) {
                // Ignore: track may already be stopped
            }
            audio.release()
        }
        audioTrack = null
    }

    private fun renderLoop(audio: AudioTrack, track: Track) {
        val buffer = ShortArray(bufferSize)
        val random = Random(track.ordinal + 42)

        // Per-track filter + modulation state
        var lowpass = 0.0
        var highpassState = 0.0
        var previousInput = 0.0
        var integrator = 0.0
        var lfoPhase = 0.0
        val lfoStep = when (track) {
            Track.FOREST -> 2.0 * Math.PI * 0.10 / sampleRate
            Track.RAIN -> 2.0 * Math.PI * 0.70 / sampleRate
            Track.DEEP_SPACE -> 2.0 * Math.PI * 0.05 / sampleRate
            Track.LOFI_CAFE -> 2.0 * Math.PI * 0.30 / sampleRate
        }

        // One-pole cutoff (0..1) per track
        val cutoff = when (track) {
            Track.FOREST -> 0.035
            Track.RAIN -> 0.45
            Track.DEEP_SPACE -> 0.012
            Track.LOFI_CAFE -> 0.12
        }

        val gain = when (track) {
            Track.FOREST -> 0.16
            Track.RAIN -> 0.11
            Track.DEEP_SPACE -> 0.20
            Track.LOFI_CAFE -> 0.15
        }

        while (playing) {
            for (i in buffer.indices) {
                val white = random.nextFloat() * 2f - 1f
                var sample: Double = when (track) {
                    Track.FOREST -> {
                        // Band-limited wind: lowpass then remove DC with a light highpass
                        lowpass += cutoff * (white - lowpass)
                        highpassState = 0.995 * (highpassState + lowpass - previousInput)
                        previousInput = lowpass
                        highpassState
                    }
                    Track.RAIN -> {
                        lowpass += cutoff * (white - lowpass)
                        lowpass + 0.35 * (white - lowpass)
                    }
                    Track.DEEP_SPACE -> {
                        // Brown noise: integrate white noise, leak to stay stable
                        integrator = 0.995 * integrator + 0.05 * white
                        integrator
                    }
                    Track.LOFI_CAFE -> {
                        // Pink-ish: blend of two one-pole filters
                        lowpass += 0.08 * (white - lowpass)
                        val second = 0.02 * (white - lowpass)
                        lowpass + second
                    }
                }

                lfoPhase += lfoStep
                if (lfoPhase > 2.0 * Math.PI) lfoPhase -= 2.0 * Math.PI
                val lfo = when (track) {
                    Track.RAIN -> 0.85 + 0.15 * kotlin.math.sin(lfoPhase)
                    else -> 0.70 + 0.30 * kotlin.math.sin(lfoPhase)
                }

                val scaled = (sample * gain * lfo).coerceIn(-1.0, 1.0)
                buffer[i] = (scaled * Short.MAX_VALUE).toInt().toShort()
            }

            try {
                var written = 0
                while (written < buffer.size && playing) {
                    val result = audio.write(buffer, written, buffer.size - written)
                    if (result <= 0) {
                        playing = false
                        break
                    }
                    written += result
                }
            } catch (e: Exception) {
                playing = false
            }
        }

        try {
            audio.stop()
        } catch (e: Exception) {
            // Ignore: already stopped
        }
    }
}
