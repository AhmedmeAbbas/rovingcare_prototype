package gmail.ahmedmeabbas.rovingcareprototype.home.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import gmail.ahmedmeabbas.rovingcareprototype.utils.PreferencesManager
import java.util.*

class CustomContextWrapper(base: Context): ContextWrapper(base) {

    companion object {
        fun wrap(context: Context): ContextWrapper {
            val defaultLanguage = Locale.getDefault().language
            val newLanguage =
                PreferencesManager(context).getSavedString(PreferencesManager.SHARED_PREFS_LANGUAGE)
                    ?: defaultLanguage
            val newLocale = Locale(newLanguage)
            val config = context.resources.configuration

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(newLocale)

                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
                context.createConfigurationContext(config)
            } else {
                config.setLocale(newLocale)
                context.createConfigurationContext(config)
            }
            return CustomContextWrapper(context)
        }
    }
}