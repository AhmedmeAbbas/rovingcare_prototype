package gmail.ahmedmeabbas.rovingcareprototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import gmail.ahmedmeabbas.rovingcareprototype.databinding.ActivityHomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGreeting()
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
            binding.tvWelcome.text = greeting
        }
        binding.tvWelcome.text = "$greeting, $displayName"
    }

    private fun logOut() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}