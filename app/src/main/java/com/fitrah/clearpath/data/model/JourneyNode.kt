package com.fitrah.clearpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journey_nodes")
data class JourneyNode(
    @PrimaryKey
    val id: Int,
    val moduleId: Int,
    val stepOrder: Int,
    val title: String,
    val subtitle: String,
    val summary: String,
    val bodyText: String,
    val keyTakeaway: String,
    val reflectionPrompt: String,
    val actionButtonText: String,
    val imageUrl: String,
    var isCompleted: Boolean = false,
    var isLocked: Boolean = true
)
