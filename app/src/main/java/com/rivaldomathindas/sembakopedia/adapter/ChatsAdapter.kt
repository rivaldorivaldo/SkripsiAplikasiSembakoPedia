package com.rivaldomathindas.sembakopedia.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.callbacks.ChatListCallback
import com.rivaldomathindas.sembakopedia.databinding.ItemChatBinding
import com.rivaldomathindas.sembakopedia.model.Chat
import com.rivaldomathindas.sembakopedia.utils.TimeFormatter
import com.rivaldomathindas.sembakopedia.utils.inflate

class ChatsAdapter(private val callback: ChatListCallback) : RecyclerView.Adapter<ChatsAdapter.ChatListHolder>() {
    private var chats = mutableListOf<Chat>()

    fun addChat(chat: Chat) {
        chats.add(chat)
        notifyItemInserted(chats.size - 1)
    }

    fun updateChat(updatedChat: Chat) {
        for ((index, chat) in chats.withIndex()) {
            if (updatedChat.id == chat.id) {
                chats[index] = updatedChat
                notifyItemChanged(index, updatedChat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListHolder {
        return ChatListHolder(parent.inflate(R.layout.item_chat), callback)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: ChatListHolder, position: Int) {
        holder.bindViews(chats[position])
    }

    interface OnItemClickListener {
        fun onItemClickListener(chat: Chat)
    }

    class ChatListHolder(private val binding: ItemChatBinding, callback: ChatListCallback) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.callback = callback
        }

        fun bindViews(chat: Chat) {
            binding.data = chat
            binding.time = TimeFormatter().getChatTimeStamp(chat.time!!)
        }

    }

}