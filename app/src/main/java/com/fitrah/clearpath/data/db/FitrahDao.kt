package com.fitrah.clearpath.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitrah.clearpath.data.model.FaqItem
import com.fitrah.clearpath.data.model.GlossaryTerm
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.data.model.SalahStep
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journey_nodes ORDER BY id ASC")
    fun getAllNodesFlow(): Flow<List<JourneyNode>>

    @Query("SELECT * FROM journey_nodes WHERE moduleId = :moduleId ORDER BY stepOrder ASC")
    fun getNodesByModuleFlow(moduleId: Int): Flow<List<JourneyNode>>

    @Query("SELECT * FROM journey_nodes WHERE id = :id LIMIT 1")
    suspend fun getNodeById(id: Int): JourneyNode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<JourneyNode>)

    @Update
    suspend fun updateNode(node: JourneyNode)

    @Query("SELECT COUNT(*) FROM journey_nodes")
    suspend fun getCount(): Int

    @Transaction
    suspend fun completeAndUnlock(currentId: Int, nextId: Int?) {
        val current = getNodeById(currentId) ?: return
        updateNode(current.copy(isCompleted = true))
        if (nextId != null) {
            val next = getNodeById(nextId)
            if (next != null) {
                updateNode(next.copy(isLocked = false))
            }
        }
    }

    @Query("UPDATE journey_nodes SET isCompleted = 0, isLocked = CASE WHEN id = 1 THEN 0 ELSE 1 END")
    suspend fun resetAllProgress()
}

@Dao
interface GlossaryDao {
    @Query("SELECT * FROM glossary_terms ORDER BY term ASC")
    fun getAllTermsFlow(): Flow<List<GlossaryTerm>>

    @Query("SELECT * FROM glossary_terms WHERE LOWER(term) = LOWER(:term) LIMIT 1")
    suspend fun getTermByName(term: String): GlossaryTerm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<GlossaryTerm>)

    @Query("SELECT COUNT(*) FROM glossary_terms")
    suspend fun getCount(): Int
}

@Dao
interface SalahDao {
    @Query("SELECT * FROM salah_steps ORDER BY stepNumber ASC")
    fun getAllStepsFlow(): Flow<List<SalahStep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(steps: List<SalahStep>)

    @Query("SELECT COUNT(*) FROM salah_steps")
    suspend fun getCount(): Int
}

@Dao
interface FaqDao {
    @Query("SELECT * FROM faq_items ORDER BY id ASC")
    fun getAllFaqFlow(): Flow<List<FaqItem>>

    @Query("SELECT * FROM faq_items WHERE category = :category ORDER BY id ASC")
    fun getFaqByCategoryFlow(category: String): Flow<List<FaqItem>>

    @Query("SELECT * FROM faq_items WHERE question LIKE '%' || :query || '%' OR conciseAnswer LIKE '%' || :query || '%' OR detailedAdvice LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchFaqFlow(query: String): Flow<List<FaqItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(faq: List<FaqItem>)

    @Query("SELECT COUNT(*) FROM faq_items")
    suspend fun getCount(): Int
}
