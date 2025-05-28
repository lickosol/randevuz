package com.diplom.rande_vuz.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.diplom.rande_vuz.activities.LoginActivity
import com.diplom.rande_vuz.databinding.FragmentProfileBinding
import com.diplom.rande_vuz.ui.profile.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.diplom.rande_vuz.R
import java.io.File
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setupEditProfile()
        setupLogout()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUser()
    }

    private fun observeProfile() {
        viewModel.user.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.textViewName.text = listOfNotNull(it.name, it.specialization)
                    .joinToString(", ")

                val age = calculateAge(it.birthDate)
                binding.textViewBirthDate.text = listOfNotNull(age, it.vuzName)
                    .joinToString(", ")

                binding.textViewEmail.text = it.email
                val photoPath = it.profilePhotoPath
                if (!photoPath.isNullOrBlank()) {
                    val file = File(photoPath)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        binding.imageViewProfile.setImageBitmap(bitmap)
                    } else {
                        binding.imageViewProfile.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                } else {
                    binding.imageViewProfile.setImageResource(R.drawable.ic_profile_placeholder)
                }

                binding.textViewWork.text = it.work
                binding.textViewExtracurricular.text = it.extracurricular
                binding.textViewDescription.text = it.description


                binding.skillsContainer.removeAllViews()
                it.skills.split(",").map(String::trim)
                    .filter(String::isNotBlank)
                    .forEach { skill ->
                        val tv = TextView(requireContext()).apply {
                            setText(skill)
                            TextViewCompat.setTextAppearance(this, R.style.TagText)
                            val scale = resources.displayMetrics.density
                            val hPadding = (12 * scale).toInt()
                            val vPadding = (6 * scale).toInt()
                            setPadding(hPadding, vPadding, hPadding, vPadding)
                        }
                        binding.skillsContainer.addView(tv)
                    }

                binding.goalsContainer.removeAllViews()
                val goals = when (val g = it.goal) {
                    is String -> g.split(",").map(String::trim).filter(String::isNotBlank)
                    is List<*> -> g.filterIsInstance<String>()
                    else -> emptyList()
                }
                goals.forEach { goalText ->
                    val tv = TextView(requireContext()).apply {
                        setText(goalText)
                        TextViewCompat.setTextAppearance(this, R.style.TagText)
                        val scale = resources.displayMetrics.density
                        val hPadding = (8 * scale).toInt()
                        val vPadding = (4 * scale).toInt()
                        setPadding(hPadding, vPadding, hPadding, vPadding)
                    }
                    binding.goalsContainer.addView(tv)
                }
            }
        }
    }

    private fun calculateAge(birthDate: String): String? {
        return try {
            val parts = birthDate.split("/")
            if (parts.size != 3) return null

            val day = parts[0].toIntOrNull() ?: return null
            val monthString = parts[1].lowercase(Locale.getDefault())
            val year = parts[2].toIntOrNull() ?: return null

            val monthMap = mapOf(
                "январь" to 1, "февраль" to 2, "март" to 3, "апрель" to 4,
                "май" to 5, "июнь" to 6, "июль" to 7, "август" to 8,
                "сентябрь" to 9, "октябрь" to 10, "ноябрь" to 11, "декабрь" to 12
            )
            val month = monthMap[monthString] ?: return null

            val today = Calendar.getInstance()
            val birth = Calendar.getInstance().apply {
                set(year, month - 1, day)
            }

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            "$age " + when {
                age % 10 == 1 && age % 100 != 11 -> "год"
                age % 10 in 2..4 && (age % 100 !in 12..14) -> "года"
                else -> "лет"
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun setupEditProfile() {
        binding.buttonEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    private fun setupLogout() {
        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}