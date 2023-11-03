package com.mobdeve.s12.aiwear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class NotificationsActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var navButtons: List<Button>
    private val buttonIconMap = mapOf(
        R.id.homeBtn to Pair(R.drawable.baseline_home_36, R.drawable.outline_home_36),
        R.id.calendarBtn to Pair(R.drawable.baseline_calendar_month_36, R.drawable.outline_calendar_month_36),
        R.id.forumBtn to Pair(R.drawable.baseline_forum_36, R.drawable.outline_forum_36),
        R.id.notifsBtn to Pair(R.drawable.baseline_notifications_36, R.drawable.outline_notifications_36),
        R.id.addBtn to Pair(R.drawable.clicked_add_circle_36, R.drawable.baseline_add_circle_24)
    )
    private lateinit var data: ArrayList<NotificationModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Initialize your buttons
        navButtons = listOf(
            findViewById(R.id.homeBtn),
            findViewById(R.id.calendarBtn),
            findViewById(R.id.forumBtn),
            findViewById(R.id.notifsBtn),
            findViewById(R.id.addBtn)
        )

        val activeBtn = findViewById<Button>(R.id.notifsBtn)
        activeBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.baseline_notifications_36, 0, 0)

        for (button in navButtons) {
            button.setOnClickListener { onBottomNavigationItemClick(button) }
        }

        val settingsBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        // Initialize recyclerView
        this.data = DataHelper.generateNotifications()
        this.recyclerView = findViewById(R.id.notifRecyclerView)
        this.myAdapter = MyAdapter(this.data)

        this.recyclerView.adapter = this.myAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun onBottomNavigationItemClick(clickedButton: Button) {
        for (button in navButtons) {
            val iconPair = buttonIconMap[button.id]
            if (iconPair != null) {
                val iconResId = if (button == clickedButton) iconPair.first else iconPair.second
                button.setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0)
            }
        }

        // Handle navigation or other logic here based on the clickedButton
        when (clickedButton) {
            // Handle navigation or logic for each button
            this.findViewById<Button>(R.id.homeBtn) -> {
                finish()
            }
            this.findViewById<Button>(R.id.calendarBtn) -> {
                val calendarIntent = Intent(this, CalendarActivity::class.java)
                startActivity(calendarIntent)
                finish()
            }
            this.findViewById<Button>(R.id.forumBtn) -> {
                val forumIntent = Intent(this, ForumActivity::class.java)
                startActivity(forumIntent)
                finish()
            }
            this.findViewById<Button>(R.id.notifsBtn) -> {

            }
            this.findViewById<Button>(R.id.addBtn) -> {
                Toast.makeText(
                    this,
                    "Added an item",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}