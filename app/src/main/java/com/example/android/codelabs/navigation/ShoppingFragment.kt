package com.example.android.codelabs.navigation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.android.codelabs.navigation.contacts.ContactsViewModel
import com.example.android.codelabs.navigation.databinding.ShoppingFragmentBinding

class ShoppingFragment : Fragment() {
    private var _binding: ShoppingFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = ShoppingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        println("DEBUG: Entrou onviewCreated")
        println("DEBUG: id da view ${view.id}")

        viewModel.returnContactInserted.observe(viewLifecycleOwner, Observer {
            if (it) {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }
}