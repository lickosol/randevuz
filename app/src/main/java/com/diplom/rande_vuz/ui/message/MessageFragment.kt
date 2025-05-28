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

    private var actualChatId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        val args by navArgs<MessageFragmentArgs>()
        val chatId = args.chatId
        val otherUserId = args.otherUserId

        actualChatId = chatId

        setupRecyclerView()
        setupObservers()

        if (chatId != null) {
            // Загружаем существующий чат
            viewModel.loadMessages(chatId)
        } else {
            // Проверяем существующий чат
            viewModel.checkExistingChat(otherUserId) { existingChatId ->
                if (existingChatId != null) {
                    // Чат существует - загружаем
                    actualChatId = existingChatId
                    viewModel.loadMessages(existingChatId)
                }
                // Если чата нет - он создастся при отправке первого сообщения
            }
        }

        setupSendMessage(otherUserId)
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
            if (messages.isNotEmpty()) {
                binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
            }
        }

        viewModel.sendStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is SendStatus.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка отправки: ${status.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {} // Успех обрабатывается молча
            }
        }
    }

    private fun setupSendMessage(otherUserId: String) {
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString().trim()

            if (messageText.isBlank()) return@setOnClickListener

            if (actualChatId != null) {
                // Отправка в существующий чат
                viewModel.sendMessage(actualChatId!!, messageText)
            } else {
                // Создание нового чата с проверкой
                if (otherUserId.isBlank()) {
                    Toast.makeText(context, "Ошибка: не указан собеседник", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.createChatWithMessage(otherUserId, messageText) { newChatId ->
                    actualChatId = newChatId
                    viewModel.loadMessages(newChatId)
                }
            }
            binding.editTextMessage.text.clear()
        }
    }
}