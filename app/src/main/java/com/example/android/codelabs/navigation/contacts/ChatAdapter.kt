package com.example.android.codelabs.navigation.contacts

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.android.codelabs.navigation.R
import com.example.android.codelabs.navigation.database.ContactChat
import kotlinx.android.synthetic.main.message_self_card.view.*

private const val SELF: Int = 0
private const val CONTACT: Int = 1

class ChatAdapter (
        private val chats: List<ContactChat>,
        private val viewModel: ChatViewModel,
        private val emailContact: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var contactChat: ContactChat

    override fun getItemViewType(position: Int): Int {
        contactChat = chats[position]
        if (contactChat.self) return SELF
        return CONTACT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_self_card, parent, false)
        if (viewType == SELF) {
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.RIGHT
            params.topMargin = 5
            params.rightMargin = 5
            view.card_view.layoutParams = params
            view.card_view.setCardBackgroundColor(Color.parseColor("#FAF7D7"))

            view.email_contact.text = "Você"
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        contactChat = chats[position]
        (holder as ViewHolder).bind(contactChat, emailContact)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(contactChat: ContactChat, emailContact: String){
            if (itemView.email_contact.text != "Você") itemView.email_contact.text = emailContact
            itemView.message_text.text = contactChat.message
        }
    }

    override fun getItemCount() = chats.size

}