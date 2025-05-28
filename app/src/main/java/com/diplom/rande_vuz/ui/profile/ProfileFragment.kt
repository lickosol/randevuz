package com.diplom.rande_vuz.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.diplom.rande_vuz.activities.LoginActivity
import com.diplom.rande_vuz.databinding.FragmentProfileBinding
import com.diplom.rande_vuz.ui.profile.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.diplom.rande_vuz.R

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
                binding.textViewName.text = it.name
                binding.textViewBirthDate.text = it.birthDate
                binding.textViewSpecialization.text = it.specialization
                binding.textViewVuzName.text = it.vuzName
                binding.textViewEmail.text = it.email
                binding.textViewWork.text = it.work
                binding.textViewExtracurricular.text = it.extracurricular
                binding.textViewDescription.text = it.description

                // Skills
                binding.skillsContainer.removeAllViews()
                it.skills.split(",").map(String::trim)
                    .filter { s -> s.isNotBlank() }
                    .forEach { skill ->
                        val tv = TextView(requireContext()).apply {
                            text = skill
                            setTextAppearance(R.style.TagText)
                            setPadding(12, 6, 12, 6)
                        }
                        binding.skillsContainer.addView(tv)
                    }

                // Goals
                binding.goalsContainer.removeAllViews()
                val goals = when (val g = it.goal) {
                    is String -> g.split(",").map(String::trim).filter(String::isNotBlank)
                    is List<*> -> g.filterIsInstance<String>()
                    else -> emptyList()
                }
                goals.forEach { goalText ->
                    val tv = TextView(requireContext()).apply {
                        text = goalText
                        setTextAppearance(R.style.TagText)
                        setPadding(8, 4, 8, 4)
                    }
                    binding.goalsContainer.addView(tv)
                }
            }
        }
    }

    private fun setupEditProfile() {
        binding.buttonEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
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