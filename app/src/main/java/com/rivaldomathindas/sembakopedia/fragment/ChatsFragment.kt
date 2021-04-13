package com.rivaldomathindas.sembakopedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rivaldomathindas.sembakopedia.activity.ChatActivity
import com.rivaldomathindas.sembakopedia.adapter.ChatsAdapter
import com.rivaldomathindas.sembakopedia.callbacks.ChatListCallback
import com.rivaldomathindas.sembakopedia.model.Chat
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.K
import com.rivaldomathindas.sembakopedia.utils.hideView
import com.rivaldomathindas.sembakopedia.utils.showView
import com.rivaldomathindas.sembakopedia.R
import kotlinx.android.synthetic.main.fragment_chat.*
import timber.log.Timber


class ChatsFragment : BaseFragment(), ChatListCallback {
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var chatsQuery: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        chatsQuery = getDatabaseReference().child(K.CHATS).child(getUid())
        chatsQuery.addValueEventListener(chatsValueListener)
        chatsQuery.addChildEventListener(chatsChildListener)

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(v: View) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(requireActivity())
        rv.itemAnimator = DefaultItemAnimator()

        chatsAdapter = ChatsAdapter(this)
        rv.adapter = chatsAdapter
        rv.showShimmerAdapter()
    }

    private val chatsValueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Error fetching chats: $p0")
            noChats()
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                hasChats()
            } else {
                noChats()
            }
        }
    }

    private val chatsChildListener = object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
            Timber.e("Child listener cancelled: $p0")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Timber.e("Chat moved: ${p0.key}")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            val chat = p0.getValue(Chat::class.java)
            chatsAdapter.updateChat(chat!!)
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val chat = p0.getValue(Chat::class.java)
            chatsAdapter.addChat(chat!!)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            Timber.e("Chat removed: ${p0.key}")
        }
    }

    private fun hasChats() {
        rv?.hideShimmerAdapter()
        empty?.hideView()
        rv?.showView()
    }

    private fun noChats() {
        rv?.hideShimmerAdapter()
        rv?.hideView()
        empty?.showView()
    }

    override fun onClick(chat: Chat) {
        val i = Intent(activity, ChatActivity::class.java)
        i.putExtra(K.MY_ID, getUid())
        i.putExtra(K.OTHER_ID, AppUtils.getID2(chat.id!!, getUid()))
        i.putExtra(K.CHAT_NAME, chat.username)
        requireActivity().startActivity(i)
        AppUtils.animateFadein(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        chatsQuery.removeEventListener(chatsValueListener)
        chatsQuery.removeEventListener(chatsChildListener)
    }
}
