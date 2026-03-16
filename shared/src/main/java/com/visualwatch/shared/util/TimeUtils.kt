package com.visualwatch.shared.util

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
    else String.format("%02d:%02d", m, s)
}

/**
 * Parse user input string into total seconds.
 * Supported formats: "5" (5 min), "90" (90 min), "4:25" (4min 25sec), "1:30:00" (1h 30min)
 */
fun parseTimeInput(input: String): Long? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null

    val parts = trimmed.split(":")
    return try {
        when (parts.size) {
            1 -> {
                val mins = parts[0].toLong()
                if (mins in 1..600) mins * 60 else null
            }
            2 -> {
                val mins = parts[0].toLong()
                val secs = parts[1].toLong()
                if (secs in 0..59 && mins >= 0 && (mins * 60 + secs) in 1..36000) {
                    mins * 60 + secs
                } else null
            }
            3 -> {
                val hours = parts[0].toLong()
                val mins = parts[1].toLong()
                val secs = parts[2].toLong()
                if (mins in 0..59 && secs in 0..59 && hours >= 0) {
                    val total = hours * 3600 + mins * 60 + secs
                    if (total in 1..36000) total else null
                } else null
            }
            else -> null
        }
    } catch (_: NumberFormatException) {
        null
    }
}
