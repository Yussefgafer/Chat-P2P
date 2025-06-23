package com.example.p2pchat.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pchat.R
// import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Setup toolbar
        supportActionBar?.apply {
            title = "Settings"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
