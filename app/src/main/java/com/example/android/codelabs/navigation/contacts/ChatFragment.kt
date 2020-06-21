package com.example.android.codelabs.navigation.contacts

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.codelabs.navigation.MySingleton
import com.example.android.codelabs.navigation.Utils.contactSocket
import com.example.android.codelabs.navigation.database.ContactChat
import com.example.android.codelabs.navigation.database.Message
import com.example.android.codelabs.navigation.databinding.ChatFragmentBinding
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.chat_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.Dispatchers.IO as dIO

open class ChatFragment : Fragment() {

    private var _binding: ChatFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var contactEmail : String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

//        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactEmail= arguments?.getString("contactEmail").toString()

        val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(requireActivity().application, contactEmail) }
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        view.button_enviar.setOnClickListener {
            CoroutineScope(dIO).launch {
                emitMessage(viewModel)
            }
        }

        viewModel.allChat.observe(viewLifecycleOwner, Observer {
            chatRecyclerView.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = ChatAdapter(it, viewModel, contactEmail)
                if (it.size > 1) scrollToPosition(it.size - 1)
            }
        })
    }

    private suspend fun emitMessage(viewModel: ChatViewModel){
        var chat : ContactChat =
                ContactChat(0, "","", true, Date().time.toInt(), "")
        chat.message = viewModel.messageToSend.value.toString()
        chat.id = 0
        chat.emailSender = MySingleton.myUser.userEmail!!
        chat.emailRecipient = contactEmail

        viewModel.insert(chat)

        val messageData = Gson().toJson(chat)

        val jsonToExport = Gson().toJson(Message("message", messageData, Date().time.toString()))

        contactSocket.sockets[contactEmail]!!.emit("*", jsonToExport)

        requireActivity().runOnUiThread {
            viewModel.messageToSend.value = ""
        }
    }

}