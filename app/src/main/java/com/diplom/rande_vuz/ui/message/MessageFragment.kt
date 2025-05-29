// MessageFragment.kt
package com.diplom.rande_vuz.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.SendStatus
import MessageViewModel

class MessageFragment : Fragment() {

    private val args by navArgs<MessageFragmentArgs>()
    private var actualChatId: String? = null

    private lateinit var btnBack: ImageButton
    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_message, container, false)

        btnBack     = root.findViewById(R.id.buttonBack)
        ivAvatar    = root.findViewById(R.id.imageAvatar)
        tvName      = root.findViewById(R.id.textViewChatName)
        tvSubtitle  = root.findViewById(R.id.textViewChatSubtitle)
        rvMessages  = root.findViewById(R.id.recyclerViewMessages)
        etMessage   = root.findViewById(R.id.editTextMessage)
        btnSend     = root.findViewById(R.id.buttonSend)

        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        adapter = MessagesAdapter()

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSendButton()

        actualChatId = args.chatId
        if (actualChatId != null) {
            viewModel.loadMessages(actualChatId!!)
        } else {
            viewModel.checkExistingChat(args.otherUserId) { existing ->
                existing?.let {
                    actualChatId = it
                    viewModel.loadMessages(it)
                }
            }
        }

        viewModel.otherUser.observe(viewLifecycleOwner) { user ->
            tvName.text = user.name
            tvSubtitle.text = "${user.vuzName}, ${user.specialization}"
            Glide.with(requireContext())
                .load(user.profilePhotoPath)
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(ivAvatar)
        }
        viewModel.loadOtherUser(args.otherUserId)

        return root
    }

    private fun setupToolbar() {
        btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupRecyclerView() {
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isNotEmpty())
                rvMessages.scrollToPosition(list.size - 1)
        }
        viewModel.sendStatus.observe(viewLifecycleOwner) { status ->
            if (status is SendStatus.Error) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка отправки: ${status.exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSendButton() {
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isBlank()) return@setOnClickListener

            actualChatId?.let {
                viewModel.sendMessage(it, text)
            } ?: run {
                viewModel.createChatWithMessage(args.otherUserId, text) { newId ->
                    actualChatId = newId
                    viewModel.loadMessages(newId)
                }
            }
            etMessage.text.clear()
        }
    }
}