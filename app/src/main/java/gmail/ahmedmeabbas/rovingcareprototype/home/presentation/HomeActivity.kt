package gmail.ahmedmeabbas.rovingcareprototype.home.presentation

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Home"

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out -> logOut()
        }
        return true
    }

    private fun setUpLanguageToggleButton() {
        val menuItem = navigationView.menu.findItem(R.id.menu_language)
        val toggleButton = menuItem.actionView as ToggleButton
        toggleButton.apply {
            textOff = "English"
            textOn = "العربية"
            isAllCaps = false
            isChecked = true
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            )
            val colors = intArrayOf(android.R.color.transparent, android.R.color.transparent)
            backgroundTintList = ColorStateList(states, colors)
        }

        if (Locale.getDefault().language.equals("en")) {
            toggleButton.isChecked = false
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
}