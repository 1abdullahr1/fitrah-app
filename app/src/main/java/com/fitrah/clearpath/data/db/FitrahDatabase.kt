package com.fitrah.clearpath.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fitrah.clearpath.data.model.FaqItem
import com.fitrah.clearpath.data.model.GlossaryTerm
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.data.model.SalahStep
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(
    entities = [
        JourneyNode::class,
        GlossaryTerm::class,
        SalahStep::class,
        FaqItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitrahDatabase : RoomDatabase() {

    abstract fun journeyDao(): JourneyDao
    abstract fun glossaryDao(): GlossaryDao
    abstract fun salahDao(): SalahDao
    abstract fun faqDao(): FaqDao

    companion object {
        @Volatile
        private var INSTANCE: FitrahDatabase? = null

        fun getInstance(context: Context): FitrahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitrahDatabase::class.java,
                    "fitrah_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateFromSeed(context.applicationContext, database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateFromSeed(context: Context, database: FitrahDatabase) {
            try {
                context.assets.open("fitrah_seed.json").use { stream ->
                    InputStreamReader(stream).use { reader ->
                        val gson = Gson()
                        val type = object : TypeToken<Map<String, Any>>() {}.type
                        val root: Map<String, Any> = gson.fromJson(reader, type)

                        val nodesJson = gson.toJson(root["journey_nodes"])
                        val nodeType = object : TypeToken<List<JourneyNode>>() {}.type
                        val nodes: List<JourneyNode> = gson.fromJson(nodesJson, nodeType)
                        database.journeyDao().insertAll(nodes)

                        val termsJson = gson.toJson(root["glossary_terms"])
                        val termType = object : TypeToken<List<GlossaryTerm>>() {}.type
                        val terms: List<GlossaryTerm> = gson.fromJson(termsJson, termType)
                        database.glossaryDao().insertAll(terms)

                        val stepsJson = gson.toJson(root["salah_steps"])
                        val stepType = object : TypeToken<List<SalahStep>>() {}.type
                        val steps: List<SalahStep> = gson.fromJson(stepsJson, stepType)
                        database.salahDao().insertAll(steps)

                        val faqJson = gson.toJson(root["faq_items"])
                        val faqType = object : TypeToken<List<FaqItem>>() {}.type
                        val faq: List<FaqItem> = gson.fromJson(faqJson, faqType)
                        database.faqDao().insertAll(faq)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
