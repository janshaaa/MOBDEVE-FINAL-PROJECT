package com.mobdeve.s12.aiwear.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.adapters.ForumPostAdapter
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking

class ForumActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
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
    private lateinit var postsAdapter: ForumPostAdapter
    private lateinit var recyclerView: RecyclerView
    private var posts: ArrayList<ForumPostModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        // Initialize your buttons
        navButtons = listOf(
            findViewById(R.id.homeBtn),
            findViewById(R.id.calendarBtn),
            findViewById(R.id.forumBtn),
            findViewById(R.id.notifsBtn)
        )

        val activeBtn = findViewById<Button>(R.id.forumBtn)
        activeBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.baseline_forum_36, 0, 0)

        for (button in navButtons) {
            button.setOnClickListener { onBottomNavigationItemClick(button) }
        }

        // add clothes dialog
        val addButton = findViewById<ToggleButton>(R.id.addBtn)
        addButton.setText(null)

        addButton.setOnCheckedChangeListener{_, isChecked ->
            val addDialogView = layoutInflater.inflate(R.layout.dialog_add, null)
            val addDialog = Dialog(this, R.style.TransparentDialog)
            if (isChecked){
                addDialog.setContentView(addDialogView)
                val window = addDialog.window
                val displayMetrics = resources.displayMetrics
                val width = (displayMetrics.widthPixels * 0.8).toInt()
                window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

                window?.setGravity(Gravity.BOTTOM)
                addDialog.show()
                addButton.toggle()

                val addClothesBtn = addDialog.findViewById<Button>(R.id.AddClothesBtn)
                addClothesBtn.setOnClickListener {
                    val addClothesIntent = Intent(this, AddClothesActivity::class.java)
                    startActivity(addClothesIntent)
                    addDialog.dismiss()
                }
            }
        }

        val settingsBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        initializePosts()
    }

    override fun onResume() {
        super.onResume()
        postsAdapter.notifyDataSetChanged()
    }

    private fun initializePosts() {
        // Load data from firestore
        posts = runBlocking{ FirestoreDatabaseHandler.getAllPosts() }
        recyclerView = findViewById(R.id.postsRecyclerView)
        val forumTv = findViewById<TextView>(R.id.forumTv)

        if(posts != null) {
            // Initialize RecyclerView and adapter
            forumTv.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            postsAdapter = ForumPostAdapter(posts!!)

            // Set the adapter to the RecyclerView
            recyclerView.adapter = postsAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        else {
            forumTv.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
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
                val calendarIntent = Intent(this, CalendarActivity::class.java)
                startActivity(calendarIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.forumBtn) -> {

            }
            this.findViewById<Button>(R.id.notifsBtn) -> {
                val notifsIntent = Intent(this, NotificationsActivity::class.java)
                startActivity(notifsIntent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }
}