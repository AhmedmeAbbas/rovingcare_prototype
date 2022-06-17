package gmail.ahmedmeabbas.rovingcareprototype.authentication

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import gmail.ahmedmeabbas.rovingcareprototype.R
import gmail.ahmedmeabbas.rovingcareprototype.databinding.ActivitySignUpBinding
import gmail.ahmedmeabbas.rovingcareprototype.utils.ThemeManager
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    @Inject lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setUpViews()
    }

    private fun setUpViews() {
        setUpEditTextColor()
        setUpSignUpButton()
        setUpToSText()
    }

    private fun setUpSignUpButton() {
        binding.btnSignUp.apply {
            tvButton.text = getString(R.string.sign_up)
            tvButton.setTextColor(Color.WHITE)
            customButton.setOnClickListener {
                val email = binding.etEmailSignUp.text.toString()
                val password = binding.etPasswordSignUp.text.toString()
                val confirmPassword = binding.etConfirmPasswordSignUp.text.toString()
                signUp(email, password, confirmPassword)
            }
        }
    }

    private fun setUpEditTextColor() {
        val primaryColor = themeManager.colorPrimary
        val hintColor = ContextCompat.getColor(this, R.color.hint_color)
        binding.etFirstName.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.firstNameTIL.setStartIconTintList(ColorStateList.valueOf(color))
        }

        binding.etLastName.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.lastNameTIL.setStartIconTintList(ColorStateList.valueOf(color))
        }

        binding.etPhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.phoneNumberTIL.setStartIconTintList(ColorStateList.valueOf(color))
        }

        binding.etEmailSignUp.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.emailSignUpTIL.setStartIconTintList(ColorStateList.valueOf(color))
        }

        binding.etPasswordSignUp.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.passwordSignUpTIL.apply {
                setStartIconTintList(ColorStateList.valueOf(color))
                setEndIconTintList(ColorStateList.valueOf(color))
            }
        }

        binding.etConfirmPasswordSignUp.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) primaryColor else hintColor
            binding.confirmPasswordSignUpTIL.apply {
                setStartIconTintList(ColorStateList.valueOf(color))
                setEndIconTintList(ColorStateList.valueOf(color))
            }
        }
    }

    private fun setUpToSText() {
        val text = resources.getString(R.string.ToS)
        val textColor = themeManager.colorPrimary
        var click1Span1 = 0
        var click1Span2 = 0
        var click2Span1 = 0
        var click2Span2 = 0
        if (Locale.getDefault().language.equals("en")) {
            click1Span1 = 37; click1Span2 = 53; click2Span1 = 58; click2Span2 = 72
        } else if (Locale.getDefault().language.equals("ar")) {
            click1Span1 = 38; click1Span2 = 49; click2Span1 = 52; click2Span2 = 65
        }
        val spannableString = SpannableString(text)
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                TODO("Navigate to Terms and Conditions screen")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = textColor
            }
        }
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                TODO("Navigate to Privacy Statement screen")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = textColor
            }
        }
        spannableString.setSpan(clickableSpan1, click1Span1, click1Span2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan2, click2Span1, click2Span2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvToS.text = spannableString
        binding.tvToS.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun resendEmailVerification(currentUser: FirebaseUser?) {
        Snackbar.make(
            binding.root, resources.getString(R.string.email_verification_failure),
            Snackbar.LENGTH_LONG)
            .setAction(resources.getString(R.string.email_resend_action)) {
                sendVerificationEmail(currentUser)
            }.show()
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        if (!validateForm()) {
            return
        }
        if (!validatePassword(password, confirmPassword)) {
            showSnackBar(resources.getString(R.string.passwords_do_not_match))
            return
        }

        startLoading()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    updateUserProfile(currentUser)
                } else {
                    stopLoading()
                    showSnackBar(task.exception?.message!!)
                }
            }
    }

    private fun sendVerificationEmail(currentUser: FirebaseUser?) {
        auth.useAppLanguage()
        currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(
                        binding.root,
                        resources.getString(R.string.verification_email_sent),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    resendEmailVerification(currentUser)
                }
                stopLoading()
            }
    }

    private fun updateUserProfile(user: FirebaseUser?) {
        val firstName = binding.etFirstName.text.toString()
        val profileUpdates = userProfileChangeRequest {
            displayName = firstName
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(user)
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val requiredText = resources.getString(R.string.required)

        val firstName = binding.etFirstName.text.toString()
        if (TextUtils.isEmpty(firstName)) {
            binding.etFirstName.error = requiredText
            valid = false
        }

        val lastName = binding.etLastName.text.toString()
        if (TextUtils.isEmpty(lastName)) {
            binding.etLastName.error = requiredText
            valid = false
        }

        val email = binding.etEmailSignUp.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.etEmailSignUp.error = requiredText
            valid = false
        }

        val phoneNumber = binding.etPhoneNumber.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.etPhoneNumber.error = requiredText
            valid = false
        } else if (phoneNumber.length !in 10 .. 14) {
            binding.etPhoneNumber.error = resources.getString(R.string.invalid_phone_number)
            valid = false
        }


        val password = binding.etPasswordSignUp.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.etPasswordSignUp.error = requiredText
            valid = false
        }

        val confirmPassword = binding.etConfirmPasswordSignUp.text.toString()
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etConfirmPasswordSignUp.error = requiredText
            valid = false
        }
        return valid
    }

    private fun validatePassword(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun startLoading() {
        binding.btnSignUp.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.btnSignUp.progressBar.visibility = View.GONE
    }
}