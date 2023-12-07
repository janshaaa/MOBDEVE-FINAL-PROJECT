package com.mobdeve.s12.aiwear.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.models.NotificationModel
import com.mobdeve.s12.aiwear.adapters.NotificationsAdapter
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.ForumPostModel
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class NotificationsActivity : AppCompatActivity() {

    private val shouldAllowBack = false
    private lateinit var mAuth : FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var userData: UserModel
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
    private lateinit var data: ArrayList<NotificationModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        userData = runBlocking {
            FirestoreDatabaseHandler.getUserByUuid(currentUser!!.uid)!!
        }

        // Initialize your buttons
        navButtons = listOf(
            findViewById(R.id.homeBtn),
            findViewById(R.id.calendarBtn),
            findViewById(R.id.forumBtn),
            findViewById(R.id.notifsBtn)
        )

        val activeBtn = findViewById<Button>(R.id.notifsBtn)
        activeBtn.setCompoundDrawablesWithIntrinsicBounds(0,
            R.drawable.baseline_notifications_36, 0, 0)

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

                val createPostBtn = addDialog.findViewById<Button>(R.id.CreatePostBtn)
                createPostBtn.setOnClickListener {
                    val createPostIntent = Intent(this, CreatePostActivity::class.java)
                    createPostIntent.putExtra(ForumPostModel.POST_CREATED_BY_KEY, currentUser.uid)
                    createPostIntent.putExtra(ForumPostModel.USER_NAME_KEY, userData.userName)
                    createPostIntent.putExtra(ForumPostModel.USER_PHOTOURL_KEY, userData.photoUrl)
                    startActivity(createPostIntent)
                    addDialog.dismiss()
//                    finish()
                }

                val addClothesBtn = addDialog.findViewById<Button>(R.id.AddClothesBtn)
                addClothesBtn.setOnClickListener {
                    val addClothesIntent = Intent(this, AddClothesActivity::class.java)
                    startActivity(addClothesIntent)
                    addDialog.dismiss()
                }

                val schedOOTDBtn = addDialog.findViewById<Button>(R.id.SchedOOTDBtn)
                schedOOTDBtn.setOnClickListener {
                    showDatePicker()
                    addDialog.dismiss()
                }
            }
        }

        val settingsBtn = findViewById<ImageButton>(R.id.settingsBtn)
        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        // Initialize recyclerView
        this.data = DataHelper.generateNotifications()
        this.recyclerView = findViewById(R.id.notifRecyclerView)
        this.notificationsAdapter = NotificationsAdapter(this.data)

        this.recyclerView.adapter = this.notificationsAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed()
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            overridePendingTransition(0, 0)
            finish()
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
                val homeIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.calendarBtn) -> {
                val calendarIntent = Intent(this, CalendarActivity::class.java)
                startActivity(calendarIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.forumBtn) -> {
                val forumIntent = Intent(this, ForumActivity::class.java)
                startActivity(forumIntent)
                overridePendingTransition(0, 0)
                finish()
            }
            this.findViewById<Button>(R.id.notifsBtn) -> {
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DatePickerDialogCustom,
            { _, year, month, day ->
                val selectedDate = "$year-${month + 1}-$day"
                // You can do something with the selected date, e.g., pass it to the new activity
                val intent = Intent(this, CreateOutfitActivity::class.java)
                intent.putExtra(CreateOutfitActivity.SELECTED_DATE_KEY, selectedDate)
                intent.putExtra(CreateOutfitActivity.USER_UUID_KEY, userData.uuid)
                startActivity(intent)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}