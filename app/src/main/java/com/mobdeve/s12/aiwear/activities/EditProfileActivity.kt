package com.mobdeve.s12.aiwear.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.database.UserDatabase
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class EditProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var backBtn: ImageButton
    private lateinit var headerTv: TextView

    private lateinit var userIv2: CircleImageView
    private lateinit var userNameEtv: EditText
    private lateinit var userDisplayNameEtv: EditText
    private lateinit var userBioEtv: EditText
    private lateinit var userBdayEtv: DatePicker
    private lateinit var userGenderSpinner: Spinner
    private lateinit var userEmailTv: TextView
    private lateinit var saveProfileBtn: Button

    private lateinit var editTextFields: List<EditText>
    private var newBirthday = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeComponents()

        headerTv.text = "Edit Profile"

        // Loading Firebase user data
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var userData = runBlocking {
            FirestoreDatabaseHandler.getUserByUuid(currentUser!!.uid)!!
        }

        newBirthday = UserModel.DATE_FORMAT.format(userData.birthday)

        initializeUserInfo(currentUser, userData)
        saveProfileBtn.isEnabled = false

        editTextFields = listOf(
            userNameEtv,
            userDisplayNameEtv,
            userBioEtv
        )

        for(field in editTextFields) {
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val newText = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                    saveProfileBtn.isEnabled = !isTextStillOriginal(userData)
                }
            })
        }

        saveProfileBtn.setOnClickListener {
            if(isValidProfileEdit(userData)) {
                userData.userName = userNameEtv.text.toString()
                userData.displayName = userDisplayNameEtv.text.toString()
                userData.bio = userBioEtv.text.toString()
                userData.birthday = UserModel.DATE_FORMAT.parse(newBirthday)
                userData.gender = UserModel.GENDER_OPTIONS[userGenderSpinner.selectedItemPosition]

                runBlocking{
                    FirestoreDatabaseHandler.setUser(userData)
                }
                Toast.makeText(
                    this,
                    "Successfully updated profile!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            else {
                Toast.makeText(
                    this,
                    "Username already exists.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initializeComponents() {
        backBtn = findViewById(R.id.backBtn)
        headerTv = findViewById(R.id.settingsHeaderTv)
        userIv2 = findViewById(R.id.userIv2)
        userNameEtv = findViewById(R.id.userNameEtv)
        userDisplayNameEtv = findViewById(R.id.userDisplayNameEtv)
        userBioEtv  = findViewById(R.id.userBioEtv)
        userBdayEtv = findViewById(R.id.userBdayEtv)
        userGenderSpinner = findViewById(R.id.userGenderSpinner)
        userEmailTv = findViewById(R.id.userEmailTv)
        saveProfileBtn = findViewById(R.id.saveBodyBtn)
    }


    private fun initializeUserInfo(currentUser : FirebaseUser?, userData : UserModel?) {
        Glide.with(this).load(currentUser?.photoUrl).into(userIv2)
        userNameEtv.setText(userData?.userName ?: "username")
        userDisplayNameEtv.setText(userData?.displayName ?: "name")
        userBioEtv.setText(userData?.bio ?: "bio")
        userEmailTv.setText(currentUser?.email ?: "email")

        userBdayEtv.init(
            userData!!.birthday.year + 1900,  // Year since 1900
            userData!!.birthday.month,        // Month (0-based)
            userData!!.birthday.date          // Day of the month
        ) { view, year, month, day ->
            newBirthday = UserModel.DATE_FORMAT.format(Date(year-1900, month, day))
            saveProfileBtn.isEnabled = this.newBirthday != UserModel.DATE_FORMAT.format(userData?.birthday)
        }

//        TODO("Changing of profile picture/uploading")

//        TODO("Fix formatting of spinner")


        if (userGenderSpinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, UserModel.GENDER_OPTIONS)
            userGenderSpinner.adapter = adapter

            userGenderSpinner.setSelection(UserModel.GENDER_OPTIONS.indexOf(userData.gender))

            userGenderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    saveProfileBtn.isEnabled = UserModel.GENDER_OPTIONS[position] != userData.gender
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    /*
    *   isTextStillOriginal() is a helper function that can be used when determining if there are
    *   any changes to either the title or body edit text. Please note that this uses the
    *   viewBinding approach. If you're not planning to use ViewBinding, please make the appropriate
    *   changes.
    * */
    private fun isTextStillOriginal(userData: UserModel?) : Boolean {
        return (this.userNameEtv.text.toString() == userData?.userName) and
                (this.userDisplayNameEtv.text.toString() == userData?.displayName) and
                (this.userBioEtv.text.toString() == userData?.bio)
    }

    private fun isValidProfileEdit(userData: UserModel?): Boolean {
        val newUsername = userNameEtv.text.toString()

        return if (newUsername == userData?.userName) { // Username did not change
            true
        } else {  // Username changed
            runBlocking {
                FirestoreDatabaseHandler.isUserNameAvailable(userData!!.userName)
            }

        }
    }
}
