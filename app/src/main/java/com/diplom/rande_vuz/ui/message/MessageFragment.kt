package com.diplom.rande_vuz.ui.message

import MessageViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.databinding.FragmentMessageBinding
import com.diplom.rande_vuz.models.SendStatus

class MessageFragment : Fragment() {

    private lateinit var viewModel: MessageViewModel
    private lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessagesAdapter

    private val args by navArgs<MessageFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        val chatId = args.chatId

        setupRecyclerView()
        setupObservers()

        viewModel.loadMessages(chatId)

        setupSendMessage(chatId)

        return binding.root
    }

    private fun setupRecyclerView() {
        messageAdapter = MessagesAdapter()
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        }

        viewModel.sendStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is SendStatus.Error -> {
                    Toast.makeText(requireContext(), "Ошибка отправки: ${status.exception.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setupSendMessage(chatId: String) {
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            if (messageText.isNotBlank()) {
                viewModel.sendMessage(chatId, messageText)
                binding.editTextMessage.text.clear()
            }
        }
    }
}
