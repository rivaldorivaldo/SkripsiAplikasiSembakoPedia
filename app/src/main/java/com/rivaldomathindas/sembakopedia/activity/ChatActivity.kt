package com.rivaldomathindas.sembakopedia.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.rivaldomathindas.sembakopedia.adapter.MessagesAdapter
import com.rivaldomathindas.sembakopedia.model.Chat
import com.rivaldomathindas.sembakopedia.model.Message
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils.setDrawable
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper.get
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.utils.*
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : BaseActivity(), View.OnClickListener {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var chatName: String
    private lateinit var chatId: String
    private lateinit var uid1: String
    private lateinit var uid2: String
    private lateinit var chatQuery: Query
    private lateinit var prefs: SharedPreferences
    private var hasTyped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        prefs = PreferenceHelper.defaultPrefs(this)

        uid1 = intent.getStringExtra(K.MY_ID)
        uid2 = intent.getStringExtra(K.OTHER_ID)
        chatName = intent.getStringExtra(K.CHAT_NAME)
        chatId = AppUtils.chatID(uid1, uid2)

        initViews()

        chatQuery = getDatabaseReference().child(K.MESSAGES).child(chatId).orderByChild(K.TIMESTAMP)
        chatQuery.addValueEventListener(messagesValueListener)
        chatQuery.addChildEventListener(messagesChildListener)
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = chatName
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextListener()
        send.setImageDrawable(setDrawable(this, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimaryLight, 22))

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()

        messagesAdapter = MessagesAdapter()
        rv.adapter = messagesAdapter
    }

    //memeriksa perubahan pada edittext
    private fun editTextListener() {
        message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty() || s.isBlank()) {
                    hasTyped = false
                    send.setImageDrawable(setDrawable(this@ChatActivity, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimaryLight, 22))

                } else {
                    hasTyped = true
                    send.setImageDrawable(setDrawable(this@ChatActivity, FontAwesome.Icon.faw_paper_plane, R.color.colorPrimary, 22))

                }
            }
        })
    }

    //memeriksa pesan
    private val messagesValueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            noMessages()
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                hasMessages()
            } else {
                noMessages()
            }
        }
    }

    //implementasi childeventlistener
    private val messagesChildListener = object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message = p0.getValue(Message::class.java)
            messagesAdapter.addMessage(message!!)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }

    //mengirim pesan
    private fun sendMessage() {
        val ref = getDatabaseReference().child(K.MESSAGES).child(chatId)
        val key = ref.push().key

        val msg = Message()
        msg.id = key
        msg.senderId = getUid()
        msg.chatId = chatId
        msg.time = System.currentTimeMillis()
        msg.message = message.text.toString().trim()

        ref.child(key!!).setValue(msg).addOnSuccessListener {
            message.setText("")
            updateChats()
        }
    }

    //memperbaharui isi obrolan
    private fun updateChats() {
        val chat = Chat()
        chat.id = chatId
        chat.username = chatName
        chat.time = System.currentTimeMillis()
        chat.message = message.text.toString().trim()
        chat.senderId = getUid()

        getDatabaseReference().child(K.CHATS).child(getUid()).child(chatId).setValue(chat)

        chat.username = prefs[K.NAME]
        getDatabaseReference().child(K.CHATS).child(uid2).child(chatId).setValue(chat)
    }

    //jika ada pesan
    private fun hasMessages() {
        empty?.hideView()
        rv?.showView()
    }

    //jika tidak ada pesan
    private fun noMessages() {
        rv?.hideView()
        empty?.showView()
    }

    //override fungsi dari onclick
    override fun onClick(v: View?) {
        when(v?.id) {
            send.id -> if (hasTyped) {
                sendMessage()
                updateChats()
            }
        }
    }

    //override fungsi dari onoptionsitemselected
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    //override fungsi dari tombol back
    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }

    //override fungsi dari ondestroy
    override fun onDestroy() {
        super.onDestroy()
        chatQuery.removeEventListener(messagesChildListener)
        chatQuery.removeEventListener(messagesValueListener)
    }
}
