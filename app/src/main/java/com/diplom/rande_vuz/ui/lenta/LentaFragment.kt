package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.activities.AfterRegistrationActivity
import com.diplom.rande_vuz.databinding.FragmentLentaBinding
import com.diplom.rande_vuz.models.UserData
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LentaViewModel
    private lateinit var adapter: LentaAdapter
    private var fullUserList: List<UserData> = emptyList()

    private var selectedGoals: List<String> = emptyList()
    private val allGoals = listOf("Дружба", "Репетиторство", "Работа", "Не знаю")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBottomSystemInsets()

        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("LentaFragment", "Текущий UID: ${currentUser?.uid}")

        adapter = LentaAdapter(emptyList()) { user ->
            if (user.uid.isBlank()) {
                Toast.makeText(context, "Ошибка: ID пользователя пустой", Toast.LENGTH_SHORT).show()
                return@LentaAdapter
            }
            (activity as? AfterRegistrationActivity)
                ?.navigateToChatFromOtherTab(chatId = null, otherUserId = user.uid)
        }

        binding.rvProfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfiles.adapter = adapter

        binding.btnToggleFilter.setOnClickListener {
            toggleFilterVisibility()
        }

        binding.btnApplyFilters.setOnClickListener {
            val selected = binding.chipGroupFilterGoals.checkedChipIds
                .mapNotNull { id ->
                    binding.chipGroupFilterGoals.findViewById<Chip>(id)?.text?.toString()
                }

            if (selected.size == allGoals.size) {
                binding.chipGroupFilterGoals.clearCheck()
                selectedGoals = emptyList()
            } else {
                selectedGoals = selected
            }

            applyGoalFilter()
            binding.filterCard.visibility = View.GONE
        }

        viewModel = ViewModelProvider(this)[LentaViewModel::class.java]
        viewModel.users.observe(viewLifecycleOwner) { users ->
            Log.d("LentaFragment", "Получено пользователей: ${users.size}")
            fullUserList = users
            applyGoalFilter()
        }
    }

    private fun handleBottomSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.rvProfiles.setPadding(
                binding.rvProfiles.paddingLeft,
                binding.rvProfiles.paddingTop,
                binding.rvProfiles.paddingRight,
                systemBars.bottom + 150
            )
            insets
        }
    }

    private fun toggleFilterVisibility() {
        if (binding.filterCard.visibility == View.VISIBLE) {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
            binding.filterCard.startAnimation(anim)
            binding.filterCard.visibility = View.GONE
        } else {
            binding.filterCard.visibility = View.VISIBLE
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            binding.filterCard.startAnimation(anim)
        }
    }

    private fun applyGoalFilter() {
        val filtered = fullUserList.filter { user ->
            val goals = when (val g = user.goal) {
                is String -> listOf(g)
                is List<*> -> g.filterIsInstance<String>()
                else -> emptyList()
            }
            selectedGoals.isEmpty() || goals.any { it in selectedGoals }
        }
        adapter.updateUsers(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}