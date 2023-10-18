package com.mobdeve.s12.aiwear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var navButtons: List<Button>
    private val buttonIconMap = mapOf(
        R.id.homeBtn to Pair(R.drawable.baseline_home_36, R.drawable.outline_home_36),
        R.id.calendarBtn to Pair(R.drawable.baseline_calendar_month_36, R.drawable.outline_calendar_month_36),
        R.id.forumBtn to Pair(R.drawable.baseline_feed_36, R.drawable.outline_feed_36),
        R.id.notifsBtn to Pair(R.drawable.baseline_notifications_36, R.drawable.outline_notifications_36),
        R.id.addBtn to Pair(R.drawable.clicked_add_circle_36, R.drawable.baseline_add_circle_24)
    )

    private lateinit var users: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeUser()

        // Initialize your buttons
        navButtons = listOf(
            findViewById(R.id.homeBtn),
            findViewById(R.id.calendarBtn),
            findViewById(R.id.forumBtn),
            findViewById(R.id.notifsBtn),
            findViewById(R.id.addBtn)
        )

        for (button in navButtons) {
            button.setOnClickListener { onBottomNavigationItemClick(button) }
        }

        val settingsBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
    private fun initializeUser() {
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        users = UserGenerator.generateUsers()
        var userData = users.find { it.uid == currentUser!!.uid }

        val userNameTv = findViewById<TextView>(R.id.userNameTv)
        val userBioTv = findViewById<TextView>(R.id.userBioTv)
        val userIv = findViewById<ImageView>(R.id.userIv)
        userNameTv.text = userData?.displayName ?: "name"
        userBioTv.text = userData?.bio ?: ""
        Glide.with(this).load(currentUser?.photoUrl).into(userIv);
    }

    private fun onBottomNavigationItemClick(clickedButton: Button) {
        for (button in navButtons) {
            val iconPair = buttonIconMap[button.id]
            if (iconPair != null) {
                val iconResId = if (button == clickedButton) iconPair.first else iconPair.second
                button.setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0)
            }
        }

        val textView = this.findViewById<TextView>(R.id.trialTv)

        // Handle navigation or other logic here based on the clickedButton
        when (clickedButton) {
            // Handle navigation or logic for each button
            this.findViewById<Button>(R.id.homeBtn) -> {
                textView.text = "home"
                TODO("Switch to Home View")
            }
            this.findViewById<Button>(R.id.calendarBtn) -> {
                textView.text = "calendar"
                TODO("Switch to Calendar View")
            }
            this.findViewById<Button>(R.id.forumBtn) -> {
                textView.text = "forum"
                TODO("Switch to Forum View")
            }
            this.findViewById<Button>(R.id.notifsBtn) -> {
                textView.text = "notifs"
                TODO("Switch to Notifs View")
            }
            this.findViewById<Button>(R.id.addBtn) -> {
                textView.text = "add"
                TODO("Show Add outfit options")
            }
        }
    }


}