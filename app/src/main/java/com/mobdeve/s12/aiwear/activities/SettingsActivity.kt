package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Settings"

        mAuth = FirebaseAuth.getInstance()

        val signOutBtn = findViewById<Button>(R.id.signOutBtn)
        signOutBtn.setOnClickListener {
            signOut()
        }

        val editProfileBtn = findViewById<Button>(R.id.CreateIdeaBtn)
        editProfileBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val bodyInfoBtn = findViewById<Button>(R.id.SchedOOTDBtn)
        bodyInfoBtn.setOnClickListener {
            val intent = Intent(this, BodyInfoActivity::class.java)
            startActivity(intent)
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
    private fun signOut() {
        mAuth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}