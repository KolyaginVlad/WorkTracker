package ru.kolyagin.worktracker.domain.models

data class WorkEvent(
    val id: Long,
    val timeStart: Time,
    val timeEnd: Time,
    val name: String,
    val isDinner:Boolean = false
)