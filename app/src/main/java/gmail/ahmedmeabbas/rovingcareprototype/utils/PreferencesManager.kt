package gmail.ahmedmeabbas.rovingcareprototype.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

class PreferencesManager(
    private val context: Context
    ) {

    fun saveString(key: String, value: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getSavedString(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(
            SHARED_PREFS_NAME,
            MODE_PRIVATE
        )
        return sharedPreferences?.getString(key, null)
    }

    fun getSavedBoolean(key: String): Boolean? {
        val sharedPreferences = context.getSharedPreferences(
            SHARED_PREFS_NAME,
            MODE_PRIVATE
        )
        return sharedPreferences?.getBoolean(key, false)
    }

    companion object {
        const val SHARED_PREFS_NAME = "sharedPrefs"
        const val SHARED_PREFS_LANGUAGE = "SHARED_PREFS_LANGUAGE"
        const val SHARED_PREFS_THEME = "SHARED_PREFS_THEME"
    }
}