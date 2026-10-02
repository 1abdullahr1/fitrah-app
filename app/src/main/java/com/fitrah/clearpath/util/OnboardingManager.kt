package com.fitrah.clearpath.util

import android.content.Context
import android.content.SharedPreferences

class OnboardingManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_COMPLETED, value).apply()

    fun reset() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "fitrah_onboarding_prefs"
        private const val KEY_COMPLETED = "onboarding_completed"
    }
}
