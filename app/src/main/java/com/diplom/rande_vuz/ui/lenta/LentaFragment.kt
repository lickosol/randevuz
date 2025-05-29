package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.btnApplyFilters.setOnClickListener {
            val selected = binding.chipGroupFilterGoals.checkedChipIds
                .mapNotNull { id -> binding.chipGroupFilterGoals.findViewById<Chip>(id)?.text?.toString() }

            if (selected.size == allGoals.size) {
                binding.chipGroupFilterGoals.clearCheck()
                binding.chipGroupFilterGoals.checkedChipIds.toList().forEach { id ->
                    binding.chipGroupFilterGoals.findViewById<Chip>(id)?.isChecked = false
                }
                selectedGoals = emptyList()
            } else {
                selectedGoals = selected
            }

            applyGoalFilter()
        }

        viewModel = ViewModelProvider(this)[LentaViewModel::class.java]
        viewModel.users.observe(viewLifecycleOwner) { users ->
            Log.d("LentaFragment", "Получено пользователей: ${users.size}")
            fullUserList = users
            applyGoalFilter()
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