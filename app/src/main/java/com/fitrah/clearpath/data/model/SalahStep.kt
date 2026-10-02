package com.fitrah.clearpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salah_steps")
data class SalahStep(
    @PrimaryKey
    val stepNumber: Int,
    val postureName: String,
    val arabicName: String,
    val postureDescription: String,
    val arabicRecitation: String,
    val transliteration: String,
    val englishMeaning: String,
    val guidanceTip: String
)
