package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.databinding.FragmentLentaBinding

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LentaViewModel
    private lateinit var adapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(LentaViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvProfiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LentaFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.profiles.observe(viewLifecycleOwner) { profiles ->
            adapter.submitList(profiles)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}