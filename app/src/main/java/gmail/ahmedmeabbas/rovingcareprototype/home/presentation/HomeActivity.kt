package gmail.ahmedmeabbas.rovingcareprototype.home.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import gmail.ahmedmeabbas.rovingcareprototype.R
import gmail.ahmedmeabbas.rovingcareprototype.authentication.SignInActivity
import gmail.ahmedmeabbas.rovingcareprototype.databinding.ActivityHomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.navigationView).setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpGreeting() {
        val currentTime = getCurrentTime()
        val greeting = getGreeting(currentTime)
        val displayName = intent.getStringExtra("EXTRA_DISPLAY_NAME")
        setDisplayGreeting(greeting, displayName)
    }

    private fun getGreeting(currentTime: Long): String {
        var greeting = resources.getString(R.string.greeting_dafault)
        when (currentTime) {
            in 0..11 -> greeting = resources.getString(R.string.greeting_morning)
            in 12..17 -> greeting = resources.getString(R.string.greeting_afternoon)
            in 18..24 -> greeting = resources.getString(R.string.greeting_evening)
        }
        return greeting
    }

    private fun getCurrentTime(): Long {
        val gmtHours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) % 24
        val offsetHours =
            TimeUnit.MILLISECONDS.toHours(TimeZone.getDefault().rawOffset.toLong()) % 24
        return gmtHours + offsetHours
    }

    private fun setDisplayGreeting(greeting: String, displayName: String?) {
        if (displayName == null) {
            //tvWelcome.text = greeting
        }
        //tvWelcome.text = "$greeting, $displayName"
    }

    private fun logOut() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}