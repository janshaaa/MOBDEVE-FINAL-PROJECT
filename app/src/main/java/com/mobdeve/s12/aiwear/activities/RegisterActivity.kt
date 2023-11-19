package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.database.UserDatabase
import com.mobdeve.s12.aiwear.models.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeComponents()

        headerTv.text = "Registration"
        backBtn.visibility = View.GONE

        // Loading Firebase user data
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userDb = UserDatabase(applicationContext)

        // Initializing Firebase user data
        Glide.with(this).load(currentUser?.photoUrl).into(userIv2)
        userDisplayNameEtv.setText(currentUser!!.displayName)
        userEmailTv.setText(currentUser!!.email)

        // Birthday Field
        var birthday = ""
        val today = Calendar.getInstance()
        userBdayEtv.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)) { view, year, month, day ->
            val month = month + 1
            birthday = "$year-$month-$day"
        }

        // Gender Spinner
        userGenderSpinner.setSelection(0)

        if (userGenderSpinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, UserModel.GENDER_OPTIONS
            )
            userGenderSpinner.adapter = adapter

            userGenderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        // Register Button
        saveProfileBtn.text = "Register"
        saveProfileBtn.isEnabled = true
        saveProfileBtn.setOnClickListener {
            if(allFieldsComplete()) {
                if(userDb.isUniqueUsername(userNameEtv.text.toString())) {
                    userDb.addUser(
                        UserModel(
                            currentUser.uid,
                            userNameEtv.text.toString(),
                            userDisplayNameEtv.text.toString(),
                            userBioEtv.text.toString(),
                            UserModel.GENDER_OPTIONS[userGenderSpinner.selectedItemPosition],
                            UserModel.DATE_FORMAT.parse(birthday)
                        )
                    )
                    Toast.makeText(
                        this,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)
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
            else {
                Toast.makeText(
                    this,
                    "Please fill out all fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun allFieldsComplete(): Boolean {
        return (userNameEtv.text.toString() != "") and
                (userDisplayNameEtv.text.toString() != "") and
                (userBioEtv.text.toString() != "") and
                (userDisplayNameEtv.text.toString() != "") and
                (userGenderSpinner.selectedItemPosition != -1)
    }
}