package gmail.ahmedmeabbas.rovingcareprototype

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import gmail.ahmedmeabbas.rovingcareprototype.databinding.ActivityWelcomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setUpViews()

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
            in 0 .. 11 -> greeting = resources.getString(R.string.greeting_morning)
            in 12 .. 17 -> greeting = resources.getString(R.string.greeting_afternoon)
            in 18 .. 24 -> greeting = resources.getString(R.string.greeting_evening)
        }
        return greeting
    }

    private fun setUpViews() {
        setUpUpdateEmailButton()
        setUpUpdatePasswordButton()
        setUpDeleteUserButton()
        setUpGreeting()
    }

    private fun setUpUpdatePasswordButton() {
        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.etUpdatePassword.text.toString()
            val newConfirmPassword = binding.etUpdatePasswordConfirm.text.toString()
            if (!validatePasswords(newPassword, newConfirmPassword)) {
                showSnackBar(resources.getString(R.string.passwords_do_not_match))
                return@setOnClickListener
            }
            val user = auth.currentUser
            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showSnackBar(resources.getString(R.string.password_update_success))
                    } else {
                        showSnackBar(resources.getString(R.string.password_update_failure))
                    }
                }
        }
    }

    private fun validatePasswords(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private fun setUpUpdateEmailButton() {
        binding.btnUpdateEmail.setOnClickListener {
            val email = binding.etUpdateEmail.text.toString()
            val user = auth.currentUser
            user!!.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "setUpEditEmailButton: ")
                        showSnackBar(resources.getString(R.string.email_update_success))
                    } else {
                        showSnackBar(resources.getString(R.string.email_update_failure))
                    }
                }
        }
    }

    private fun setUpDeleteUserButton() {
        binding.btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.delete_account_dialog_title))
                .setMessage(resources.getString(R.string.delete_account_dialog_message))
                .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                    deleteUser()
                }
                .setNegativeButton(resources.getString(R.string.cancel), null)
                .show()
        }
    }

    private fun deleteUser() {
        val user = auth.currentUser
        user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateToSignUpActivity()
                } else {
                    showSnackBar(resources.getString(R.string.delete_account_failure))
                }
            }
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setDisplayGreeting(greeting: String, displayName: String?) {
        if (displayName == null) {
            binding.tvWelcome.text = greeting
        }
        binding.tvWelcome.text = "$greeting, $displayName"
    }

    private fun getCurrentTime(): Long {
        val gmtHours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) % 24
        val offsetHours = TimeUnit.MILLISECONDS.toHours(TimeZone.getDefault().rawOffset.toLong()) % 24
        Log.d(TAG, "getCurrentTime: gmt hours: $gmtHours")
        Log.d(TAG, "getCurrentTime: time zone: ${TimeZone.getDefault()}")
        Log.d(TAG, "getCurrentTime: offset hours: $offsetHours")
        return gmtHours + offsetHours
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Welcome: onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "Welcome: onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Welcome: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Welcome: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Welcome: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Welcome: onDestroy")
    }



    companion object {
        private const val TAG = "WelcomeActivity"
    }
}