/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.codelabs.navigation.Utils.mySocket
import com.example.android.codelabs.navigation.contacts.ChatViewModel
import com.example.android.codelabs.navigation.contacts.ChatViewModelFactory
import com.example.android.codelabs.navigation.contacts.ContactsAdapter
import com.example.android.codelabs.navigation.contacts.ContactsViewModel
import com.example.android.codelabs.navigation.createUser.CreateUserViewModel
import com.example.android.codelabs.navigation.database.Contact
import com.example.android.codelabs.navigation.database.ContactChat
import com.example.android.codelabs.navigation.database.Message
import com.example.android.codelabs.navigation.databinding.FlowStepOneFragmentBinding
import com.example.android.codelabs.navigation.databinding.FlowStepTwoFragmentBinding
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.flow_step_two_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Presents how multiple steps flow could be implemented.
 */
class FlowStepFragment : Fragment() {

    private var _binding: FlowStepOneFragmentBinding? = null
    private val binding get() = _binding!!

    private var _binding2: FlowStepTwoFragmentBinding? = null
    private val binding2 get() = _binding2!!

    lateinit var socket: Socket

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        setHasOptionsMenu(true)

        val safeArgs: FlowStepFragmentArgs by navArgs()

        return when (safeArgs.flowStepNumber) {
            2 -> {
                _binding2 = FlowStepTwoFragmentBinding.inflate(inflater, container, false)
//                setHasOptionsMenu(true)
                binding2.root
            }
            else -> {
                _binding = FlowStepOneFragmentBinding.inflate(inflater, container, false)
//                setHasOptionsMenu(true)
                binding.root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (view.id) {
            R.id.step_one -> {
                val viewModel = ViewModelProvider(this).get(CreateUserViewModel::class.java)
                binding.viewModel = viewModel

                viewModel.returnUserCreated.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        println("Debug: Observe : ${it.toString()}")
                        val options = navOptions {
                            anim {
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_right
                            }
                        }
                        findNavController().navigate(R.id.home_dest, null, options)
                    }
                })

//                view.findViewById<View>(R.id.next_button).setOnClickListener(
//                        Navigation.createNavigateOnClickListener(R.id.next_action)
//                )

            } else -> {
                val viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
                binding2.viewModel = viewModel

                viewModel.allContacts.observe(viewLifecycleOwner, Observer {
                    contactsRecyclerView.apply {
                        layoutManager = LinearLayoutManager(this.context)
                        adapter = ContactsAdapter(it, viewModel)
                        if (it.size > 1) smoothScrollToPosition(it.size - 1)
                    }
                })

                view.findViewById<View>(R.id.fab).setOnClickListener(
                    Navigation.createNavigateOnClickListener(R.id.shopping_dest)
                )

                CoroutineScope(Dispatchers.IO).launch {
                    if (!mySocket.isInit()) {
                        socketRequest(viewModel)
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _binding2 = null
    }


    private suspend fun socketRequest(viewModel: ContactsViewModel) {
        println("DEBUG:  criou novo canal usuário")
        // Connecting to the users channel

        mySocket(MySingleton.myUser.userEmail!!)

        mySocket.socket.on(Socket.EVENT_CONNECT) {
            requireActivity().runOnUiThread{
                Toast.makeText(this.context, "Connected to user channel ${MySingleton.myUser.userEmail}", Toast.LENGTH_SHORT).show()
            }
        }

        mySocket.socket.on(Socket.EVENT_ERROR) { args ->
            args.forEach {
                println(it.toString())
            }
        }

        mySocket.socket.on("*") { newArray ->
            var content : String = ""
            newArray.forEach {
                content = it.toString()
            }

            val json = Gson().fromJson(content, Message::class.java)

            println(json)

            lateinit var contactChat: ContactChat
            if (json.type == "message") {
                contactChat = Gson().fromJson(json.data, ContactChat::class.java)
            }
            contactChat.self = false
            println("DEBUG:  mensagem recebida ")

            viewModel.insertMessage(contactChat)
        }

        mySocket.socket.connect()

    }

}
