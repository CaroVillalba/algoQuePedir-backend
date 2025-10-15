package ar.edu.unsam.algo3.domain

import java.time.LocalDateTime

class Message(
    val sentAt: LocalDateTime,
    val subject: String,
    val content: String,
    private var read: Boolean = false
) {
    fun markAsRead() {
        read = true
    }

    fun markAsUnread() {
        read = false
    }

    fun isRead() = read
}

class Inbox {
    private val internalMessages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        internalMessages.add(message)
    }

    fun getMessages(): List<Message> = internalMessages.toList()

    fun getUnreadMessages(): List<Message> =
        internalMessages.filter { !it.isRead() }

    fun getReadMessages(): List<Message> =
        internalMessages.filter { it.isRead() }
}
