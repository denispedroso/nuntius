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

package com.example.android.codelabs.navigation.home

import android.os.Bundle
import android.view.*
import android.widget.Button

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.android.codelabs.navigation.MySingleton
import com.example.android.codelabs.navigation.R

import com.example.android.codelabs.navigation.databinding.HomeFragmentBinding

/**
 * Fragment used to show how to navigate to another destination
 */
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
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

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.user?.observe(viewLifecycleOwner, Observer { user ->
            // Update the cached copy of the words in the adapter.
            user.let {
                viewModel.gottenUser.value = it?.userEmail
                println("DEBUG: last User found = ${it?.userEmail}")
            }

            user?.userLogged?.let {
                if (user.userLogged) {
                    viewModel.loggedUser.value = user
                    viewModel.returnUserLoggedIn.value = true
                }
            }
        })

        viewModel.allUsers.observe(viewLifecycleOwner, Observer { users ->
            // Update the cached copy of the words in the adapter.
            users?.forEach {
                println("DEBUG:  user = ${it.userEmail}")
            }
        })

        viewModel.returnUserLoggedIn.observe(viewLifecycleOwner, Observer {
            if (it) {

                MySingleton.myUser = viewModel.loggedUser.value!!

                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_right
                    }
                }
                findNavController().navigate(R.id.flow_step_two_dest, null, options)
            }
        })

        view.findViewById<Button>(R.id.navigate_destination_button)?.setOnClickListener {
            val flowStepNumberArg = 1
            val action = HomeFragmentDirections.nextAction(flowStepNumberArg)
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    fun logIn () {
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.logIn()
    }
}
