package com.fitrah.clearpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glossary_terms")
data class GlossaryTerm(
    @PrimaryKey
    val term: String,
    val arabicScript: String,
    val plainEnglishTitle: String,
    val simpleDefinition: String,
    val detailedExplanation: String
)
