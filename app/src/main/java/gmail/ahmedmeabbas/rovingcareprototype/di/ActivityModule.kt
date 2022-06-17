package gmail.ahmedmeabbas.rovingcareprototype.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import gmail.ahmedmeabbas.rovingcareprototype.utils.PreferencesManager
import gmail.ahmedmeabbas.rovingcareprototype.utils.ThemeManager

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun providePreferencesManager(
        @ActivityContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    fun provideThemeManager(
        @ActivityContext context: Context
    ): ThemeManager {
        return ThemeManager(context)
    }
}