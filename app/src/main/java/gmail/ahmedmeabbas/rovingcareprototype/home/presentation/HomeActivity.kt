package gmail.ahmedmeabbas.rovingcareprototype.home.presentation

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ToggleButton
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import gmail.ahmedmeabbas.rovingcareprototype.R
import gmail.ahmedmeabbas.rovingcareprototype.authentication.SignInActivity
import gmail.ahmedmeabbas.rovingcareprototype.home.utils.CustomContextWrapper
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var appLanguage: String

    // Load shared preferences method is called inside attachBaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
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

        Log.d(TAG, "setUpLanguageToggleButton: After: $appLanguage")
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

        languageToggleButton.isChecked = appLanguage == "en"
        languageToggleButton.setOnClickListener {
            val newLang = if (appLanguage == "en") "ar" else "en"
            savePreferences(PREFERENCE_LANGUAGE, newLang)
            this@HomeActivity.recreate()
        }
    }

    private fun setUpDarkModeSwitch() {
        val menuItem = navigationView.menu.findItem(R.id.menu_theme)
        val switchCompat = menuItem.actionView as SwitchCompat
        if (isDarkModeOn()) {
            switchCompat.isChecked = true
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun isDarkModeOn(): Boolean {
        return when (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    private fun savePreferences(key: String, value: String) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }

    private fun savePreferences(key: String, value: Boolean) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun loadPreferences(context: Context?) {
        val sharedPreferences = context?.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val defaultLanguage = Locale.getDefault().language
        appLanguage = sharedPreferences?.getString(PREFERENCE_LANGUAGE, null) ?: defaultLanguage
    }

    override fun attachBaseContext(newBase: Context?) {
        loadPreferences(newBase)
        val locale = Locale(appLanguage)
        val context = CustomContextWrapper.wrap(newBase!!, locale)
        super.attachBaseContext(context)
    }

    companion object {
        private const val PREFERENCES_NAME = "sharedPrefs"
        private const val PREFERENCE_LANGUAGE = "PREFERENCE_LANGUAGE"
        private const val PREFERENCE_THEME = "PREFERENCE_THEME"
        private const val TAG = "HomeActivity"
    }
}