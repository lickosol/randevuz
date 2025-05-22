package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.databinding.FragmentLentaBinding
import com.google.firebase.auth.FirebaseAuth

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LentaViewModel
    private lateinit var adapter: LentaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем RecyclerView
        adapter = LentaAdapter(emptyList())
        binding.rvProfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfiles.adapter = adapter

        // ViewModel
        viewModel = ViewModelProvider(this)[LentaViewModel::class.java]

        viewModel.users.observe(viewLifecycleOwner) { users ->
            Log.d("LentaFragment", "Обновлено пользователей: ${users.size}")
            adapter.updateUsers(users)
        }

        // Лог текущего пользователя
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("LentaFragment", "Текущий UID: ${currentUser?.uid}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
