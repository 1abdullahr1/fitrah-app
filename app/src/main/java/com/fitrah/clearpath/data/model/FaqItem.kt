package com.fitrah.clearpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faq_items")
data class FaqItem(
    @PrimaryKey
    val id: Int,
    val category: String,
    val question: String,
    val conciseAnswer: String,
    val detailedAdvice: String,
    val practicalTip: String
)
