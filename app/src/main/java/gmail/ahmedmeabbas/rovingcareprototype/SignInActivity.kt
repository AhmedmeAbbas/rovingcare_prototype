package gmail.ahmedmeabbas.rovingcareprototype

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import gmail.ahmedmeabbas.rovingcareprototype.databinding.ActivitySignInBinding
import gmail.ahmedmeabbas.rovingcareprototype.util.NetworkManager
import java.util.*

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        auth.useAppLanguage()
        setUpViews()
    }

    private fun setUpViews() {
        setUpEditTextColor()
        setUpSignInButton()
        setUpSignUpText()
        setUpForgotPasswordText()
    }

    private fun setUpEditTextColor() {
        val primaryColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        val hintColor = ContextCompat.getColor(this, R.color.hint_color)
        binding.etEmailSignIn.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.emailSignInTIL.setStartIconTintList(ColorStateList.valueOf(color))
        }

        binding.etPasswordSignIn.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.passwordSignInTIL.apply {
                setStartIconTintList(ColorStateList.valueOf(color))
                setEndIconTintList(ColorStateList.valueOf(color))
            }
        }
    }

    private fun setUpSignInButton() {
        binding.btnSignIn.apply {
            tvButton.text = getString(R.string.sign_in)
            customButton.setOnClickListener {
                val email = binding.etEmailSignIn.text.toString()
                val password = binding.etPasswordSignIn.text.toString()
                if (!NetworkManager.hasInternetConnection(this@SignInActivity)) {
                    Toast.makeText(
                        this@SignInActivity,
                        resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                signIn(email, password)
            }
        }

    }

    private fun setUpSignUpText() {
        val text = resources.getString(R.string.sign_up_text)
        val textColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        var span1 = 0
        var span2 = 0
        if (Locale.getDefault().language.equals("en")) {
            span1 = 23
            span2 = 30
        } else if (Locale.getDefault().language.equals("ar")) {
            span1 = 15
            span2 = 25
        }
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = textColor
            }
        }
        spannableString.setSpan(clickableSpan, span1, span2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvSignUpText.text = spannableString
        binding.tvSignUpText.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun setUpForgotPasswordText() {
        val text = resources.getString(R.string.forgot_password)
        val textColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        val span1 = 0
        var span2 = 0
        if (Locale.getDefault().language.equals("en")) {
            span2 = 21
        } else if (Locale.getDefault().language.equals("ar")) {
            span2 = 20
        }
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                if (!NetworkManager.hasInternetConnection(this@SignInActivity)) {
                    Toast.makeText(this@SignInActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    return
                }
                showResetAlertDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = textColor
            }
        }
        spannableString.setSpan(clickableSpan, span1, span2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvForgotPassword.text = spannableString
        binding.tvForgotPassword.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showResetAlertDialog(){
        val resetEditText = EditText(this)
        resetEditText.scrollBarSize = 25
        resetEditText.highlightColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        resetEditText.isSingleLine = true
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.hint_reset_password))
            .setView(resetEditText)
            .setMessage(getString(R.string.reset_password_message))
            .setPositiveButton(getString(R.string.confirm)) { _,_ ->
                val email = resetEditText.text.toString()
                sendPasswordResetEmail(email)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
        resetEditText.requestFocus()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSnackBar(resources.getString(R.string.email_reset_success))
                } else {
                    Log.w(TAG, "sendPasswordResetEmail: Failure", task.exception)
                    showSnackBar(resources.getString(R.string.invalid_email_address))
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn: $email")
        if (!validateForm()) {
            return
        }

        startLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail: Success")
                    val user = auth.currentUser!!
                    checkVerificationAndNavigate(user)
                } else {
                    Log.w(TAG, "signInWithEmail: Failure", task.exception)
                    stopLoading()
                    showSnackBar(resources.getString(R.string.invalid_email_or_password))
                }
            }
    }

    private fun checkVerificationAndNavigate(user: FirebaseUser) {
        Log.d(TAG, "checkVerificationAndNavigate: ${user.email}")
        val isVerified = user.isEmailVerified
        stopLoading()
        if (!isVerified) {
            showSnackBar(resources.getString(R.string.email_not_verified))
            return
        }
        Log.d(TAG, "checkVerificationAndNavigate: Verified")
        navigateToWelcomeActivity()
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = binding.etEmailSignIn.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.etEmailSignIn.error = resources.getString(R.string.required)
            valid = false
        }

        val password = binding.etPasswordSignIn.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.etPasswordSignIn.error = resources.getString(R.string.required)
            valid = false
        }
        return valid
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this@SignInActivity, HomeActivity::class.java)
        val displayName = auth.currentUser?.displayName
        intent.putExtra("EXTRA_DISPLAY_NAME", displayName)
        startActivity(intent)
        this@SignInActivity.finish()
        Log.d(TAG, "navigateToWelcomeActivity: Launched Activity")
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun startLoading() {
        binding.btnSignIn.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.btnSignIn.progressBar.visibility = View.INVISIBLE
    }

    private fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    private fun clearSpannableStrings() {
        binding.tvSignUpText.text = ""
        binding.tvForgotPassword.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        clearSpannableStrings()
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}



