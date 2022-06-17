package gmail.ahmedmeabbas.rovingcareprototype.utils

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.annotation.AttrRes

class ThemeManager(
    private val context: Context
) {

    val colorPrimary = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
    val colorPrimaryVariant = getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant)
    //val colorSecondary = getColorFromAttr(com.google.android.material.R.attr.colorSecondary)
    //val colorSecondaryVariant = getColorFromAttr(com.google.android.material.R.attr.colorSecondaryVariant)

    private fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun isDarkModeOn(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }
}