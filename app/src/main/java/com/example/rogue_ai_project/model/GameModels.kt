package com.example.rogue_ai_project.model

data class PlayerBoard(
    val board: Board,
    val instruction: Instruction,
    val threat: Int
)

data class Board(
    val commands: List<Command>
)

data class Command(
    val id: String,
    val name: String,
    val type: String,
    val styleType: String,
    val actualStatus: String,
    val actionPossible: List<String>
)

data class Instruction(
    val commandId: String,
    val timeout: Long,
    val timestampCreation: Long,
    val commandType: String,
    val instructionText: String,
    val expectedStatus: String
)

