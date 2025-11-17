package com.example.rogue_ai_project.util

private val ROOM_CODE_REGEX = Regex(pattern = "[A-Z0-9]{6}")

fun normalizeRoomCode(input: String): String =
    input.uppercase().filter { it.isLetterOrDigit() }

fun isValidRoomCode(input: String): Boolean =
    ROOM_CODE_REGEX.matches(input = normalizeRoomCode(input))

