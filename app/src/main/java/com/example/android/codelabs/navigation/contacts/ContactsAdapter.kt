package com.example.android.codelabs.navigation.contacts

import android.view.*
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.android.codelabs.navigation.MySingleton
import com.example.android.codelabs.navigation.R
import com.example.android.codelabs.navigation.Utils.contactSocket
import com.example.android.codelabs.navigation.database.Contact
import com.example.android.codelabs.navigation.database.ContactChat
import com.example.android.codelabs.navigation.database.Message
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.contact_row.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SELF: Int = 0

class ContactsAdapter (
        private val contacts: List<Contact>,
        private val viewModel: ContactsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var contact: Contact

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(contact: Contact, viewModel: ContactsViewModel){
            itemView.popUpMenu.setOnClickListener { it ->
                val popup = PopupMenu(itemView.context, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.contact_menu, popup.menu)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {item ->
                    println("DEBUG: item clicado com id= ${item.itemId}")
                    when(item.itemId) {
                        R.id.delete_contact_dest -> {
                            viewModel.delete(contact)
                            true
                        }
                        else -> false
                    }
                })
                popup.show()
            }

            itemView.setOnClickListener {
                val bundle = bundleOf("contactEmail" to contact.contactEmail)
                Navigation.findNavController(itemView).navigate(R.id.chat_dest, bundle)
            }

            if(contactSocket.sockets[contact.contactEmail.toString()] == null) {
                contactSocket(contact.contactEmail.toString())
            }

            itemView.contactEmail.text = contact.contactEmail
        }

    }

    override fun getItemViewType(position: Int): Int {
        contact = contacts[position]
        return SELF
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        contact = contacts[position]
        (holder as ViewHolder).bind(contact, viewModel)
    }

    override fun getItemCount() = contacts.size

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        contactSocket.sockets[contact.contactEmail]?.close()
    }
}
