package com.example.p2pchat.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pchat.R
// import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Get peer information from intent
        val peerId = intent.getStringExtra("peer_id")

        // Setup toolbar
        supportActionBar?.apply {
            title = "Chat with $peerId"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
