package gmail.ahmedmeabbas.rovingcareprototype.home.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class CustomContextWrapper(base: Context): ContextWrapper(base) {

    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
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