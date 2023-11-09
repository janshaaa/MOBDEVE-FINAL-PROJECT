package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.mobdeve.s12.aiwear.R

class CalendarActivity : AppCompatActivity() {
    private lateinit var navButtons: List<Button>
    private val buttonIconMap = mapOf(
        R.id.homeBtn to Pair(R.drawable.baseline_home_36, R.drawable.outline_home_36),
        R.id.calendarBtn to Pair(
            R.drawable.baseline_calendar_month_36,
            R.drawable.outline_calendar_month_36
        ),
        R.id.forumBtn to Pair(R.drawable.baseline_forum_36, R.drawable.outline_forum_36),
        R.id.notifsBtn to Pair(
            R.drawable.baseline_notifications_36,
            R.drawable.outline_notifications_36
        ),
        R.id.addBtn to Pair(R.drawable.clicked_add_circle_36, R.drawable.baseline_add_circle_24)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize your buttons
        navButtons = listOf(
            findViewById(R.id.homeBtn),
            findViewById(R.id.calendarBtn),
            findViewById(R.id.forumBtn),
            findViewById(R.id.notifsBtn),
            findViewById(R.id.addBtn)
        )

        val activeBtn = findViewById<Button>(R.id.calendarBtn)
        activeBtn.setCompoundDrawablesWithIntrinsicBounds(0,
            R.drawable.baseline_calendar_month_36, 0, 0)

        for (button in navButtons) {
            button.setOnClickListener { onBottomNavigationItemClick(button) }
        }

        val settingsBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

//        TODO("calendar date on click, search Outfits DB via data, update recycler view")
//
//        TODO("add item/outfit button")
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
                overridePendingTransition(0, 0)
            }
            this.findViewById<Button>(R.id.calendarBtn) -> {

            }
            this.findViewById<Button>(R.id.forumBtn) -> {
                val forumIntent = Intent(this, ForumActivity::class.java)
                startActivity(forumIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.notifsBtn) -> {
                val notifsIntent = Intent(this, NotificationsActivity::class.java)
                startActivity(notifsIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.addBtn) -> {
                val addClothesIntent = Intent(this, AddClothesActivity::class.java)
                startActivity(addClothesIntent)
            }
        }
    }
}