package gmail.ahmedmeabbas.rovingcareprototype.home.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import gmail.ahmedmeabbas.rovingcareprototype.R
import gmail.ahmedmeabbas.rovingcareprototype.home.utils.CustomContextWrapper
import gmail.ahmedmeabbas.rovingcareprototype.utils.PreferencesManager
import gmail.ahmedmeabbas.rovingcareprototype.utils.PreferencesManager.Companion.SHARED_PREFS_THEME
import gmail.ahmedmeabbas.rovingcareprototype.utils.ThemeManager
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    @Inject lateinit var themeManager: ThemeManager
    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Slight keyboard lag
        setUpTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navigationView = findViewById(R.id.navigationView)
        navigationView.setupWithNavController(navController)

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setupWithNavController(
            navController
        )
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        setUpLanguageToggleButton()
        setUpDarkModeSwitch()
    }

    private fun setUpLanguageToggleButton() {
        val menuItem = navigationView.menu.findItem(R.id.menu_language)
        val languageToggleButton = menuItem.actionView as ToggleButton
        languageToggleButton.apply {
            textOff = "العربية"
            textOn = "ُEnglish"
            isAllCaps = false
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            )
            val colors = intArrayOf(android.R.color.transparent, android.R.color.transparent)
            backgroundTintList = ColorStateList(states, colors)
        }

        val appLanguage = Locale.getDefault().language
        languageToggleButton.isChecked = appLanguage == "en"
        languageToggleButton.setOnClickListener {
            val newLang = if (appLanguage == "en") "ar" else "en"
            preferencesManager.saveString(PreferencesManager.SHARED_PREFS_LANGUAGE, newLang)
            this@HomeActivity.recreate()
        }
    }

    private fun setUpDarkModeSwitch() {
        val menuItem = navigationView.menu.findItem(R.id.menu_theme)
        val switch = menuItem.actionView as SwitchCompat
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        )
        val thumbColors = intArrayOf(Color.LTGRAY, themeManager.colorPrimary)
        val trackColors = intArrayOf(Color.GRAY, themeManager.colorPrimaryVariant)
        switch.thumbTintList = ColorStateList(states, thumbColors)
        switch.trackTintList = ColorStateList(states, trackColors)
        val isNightThemeSaved = preferencesManager.getSavedBoolean(SHARED_PREFS_THEME) ?: themeManager.isDarkModeOn()
        if (isNightThemeSaved) {
            switch.isChecked = true
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                preferencesManager.saveBoolean(SHARED_PREFS_THEME, true)
                return@setOnCheckedChangeListener
            }
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            preferencesManager.saveBoolean(SHARED_PREFS_THEME, false)
        }
    }

    private fun setUpTheme() {
        // A new object of PreferencesManager is used here instead of the injected one because the
        // injected object cannot be initialized before super.onCreate(savedInstanceState) is called
        val isNightThemeSaved = PreferencesManager(this).getSavedBoolean(SHARED_PREFS_THEME)
            ?: false
        if (isNightThemeSaved) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            return
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CustomContextWrapper.wrap(newBase!!))
    }
}