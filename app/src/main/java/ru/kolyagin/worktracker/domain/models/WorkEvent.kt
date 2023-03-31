package ru.kolyagin.worktracker.domain.models

import ru.kolyagin.worktracker.data.database.entities.WorkEventEntity

data class WorkEvent(
	val id: Long,
	val timeStart: Time,
	val timeEnd: Time,
	val name: String,
	val isDinner: Boolean = false
)

fun WorkEvent.mapToEntity() = WorkEventEntity(
	id = id,
	name = name,
	timeStart = timeStart,
	timeEnd = timeEnd,
	isDinner = isDinner,
	day = -1
)