package com.example.android.codelabs.navigation.Utils

import com.example.android.codelabs.navigation.database.ContactChat
import com.example.android.codelabs.navigation.database.Message
import com.example.android.codelabs.navigation.home.HomeViewModel
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson

data class BodyResponse (
        val error: Boolean?,
        val data: HomeViewModel.NUser?,
        val message: String?
)

class contactSocket(channel: String) {

    companion object {
        val sockets = mutableMapOf<String, Socket>()
    }

    var socket: Socket = IO.socket("http://10.0.2.2:3000/${channel}")

    init {
        println("DEBUG entrou contactSocket e criou mais um contato ")

        socket.connect()

        contactSocket.sockets[channel] = socket
    }
}

class mySocket(channel: String) {

    companion object {
        lateinit var socket : Socket

        fun isInit(): Boolean {
            return this::socket.isInitialized
        }
    }

    private val socket: Socket = IO.socket("http://10.0.2.2:3000/${channel}")

    init {
        mySocket.socket = socket
    }


}