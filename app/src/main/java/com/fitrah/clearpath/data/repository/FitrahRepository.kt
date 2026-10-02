package com.fitrah.clearpath.data.repository

import android.content.Context
import com.fitrah.clearpath.data.db.FitrahDatabase
import com.fitrah.clearpath.data.model.FaqItem
import com.fitrah.clearpath.data.model.GlossaryTerm
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.data.model.SalahStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FitrahRepository(private val context: Context) {

    private val database = FitrahDatabase.getInstance(context)
    private val journeyDao = database.journeyDao()
    private val glossaryDao = database.glossaryDao()
    private val salahDao = database.salahDao()
    private val faqDao = database.faqDao()

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        if (journeyDao.getCount() == 0) {
            FitrahDatabase.populateFromSeed(context, database)
        }
    }

    val allJourneyNodes: Flow<List<JourneyNode>> = journeyDao.getAllNodesFlow()

    fun getNodesByModule(moduleId: Int): Flow<List<JourneyNode>> =
        journeyDao.getNodesByModuleFlow(moduleId)

    suspend fun getNodeById(id: Int): JourneyNode? = withContext(Dispatchers.IO) {
        journeyDao.getNodeById(id)
    }

    suspend fun completeAndUnlock(currentId: Int, nextId: Int?) = withContext(Dispatchers.IO) {
        journeyDao.completeAndUnlock(currentId, nextId)
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        journeyDao.resetAllProgress()
    }

    val allGlossaryTerms: Flow<List<GlossaryTerm>> = glossaryDao.getAllTermsFlow()

    suspend fun getGlossaryTerm(term: String): GlossaryTerm? = withContext(Dispatchers.IO) {
        glossaryDao.getTermByName(term)
    }

    val allSalahSteps: Flow<List<SalahStep>> = salahDao.getAllStepsFlow()

    val allFaqItems: Flow<List<FaqItem>> = faqDao.getAllFaqFlow()

    fun getFaqByCategory(category: String): Flow<List<FaqItem>> =
        faqDao.getFaqByCategoryFlow(category)

    fun searchFaq(query: String): Flow<List<FaqItem>> =
        faqDao.searchFaqFlow(query)
}
