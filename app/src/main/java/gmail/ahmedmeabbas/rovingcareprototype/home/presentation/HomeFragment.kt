package gmail.ahmedmeabbas.rovingcareprototype.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import gmail.ahmedmeabbas.rovingcareprototype.R
import gmail.ahmedmeabbas.rovingcareprototype.databinding.FragmentHomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        auth = Firebase.auth
        setUpGreeting()
    }

    private fun setUpGreeting() {
        val currentTime = getCurrentTime()
        val greeting = getGreeting(currentTime)
        val displayName = auth.currentUser?.displayName
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
            binding.tvHomeFragment.text = greeting
        }
        binding.tvHomeFragment.text = "$greeting, $displayName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}