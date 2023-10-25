package com.mobdeve.s12.aiwear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class EditProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var userIv2 : CircleImageView
    private lateinit var userNameEtv : EditText
    private lateinit var userBioEtv : EditText
    private lateinit var userBdayEtv : EditText
    private lateinit var userGenderSpinner : Spinner
    private lateinit var userEmailTv : TextView

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

    private lateinit var users: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        users = DataHelper.generateUsers()

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var userData = users.find { it.uid == currentUser!!.uid }

        initializeUserInfo(currentUser, userData)

//        TODO("do enabling disabling of save button")

        val saveProfileBtn = findViewById<Button>(R.id.saveProfileBtn)
        saveProfileBtn.setOnClickListener {
            userData?.displayName = userNameEtv.text.toString()
            userData?.bio = userBioEtv.text.toString()
            userData?.birthday = DATE_FORMAT.parse(userBdayEtv.text.toString())
            userData?.gender = userBioEtv.text.toString()

            Toast.makeText(
                this,
                "Successfully updated profile!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

    }

    private fun initializeUserInfo(currentUser : FirebaseUser?, userData : UserModel?) {
        userIv2 = findViewById(R.id.userIv2)
        userNameEtv = findViewById(R.id.userNameEtv)
        userBioEtv = findViewById(R.id.userBioEtv)
        userBdayEtv = findViewById(R.id.userBdayEtv)
        userEmailTv = findViewById(R.id.userEmailTv)
        userGenderSpinner = findViewById(R.id.userGenderSpinner)

        Glide.with(this).load(currentUser?.photoUrl).into(userIv2)
        userNameEtv.setText(userData?.displayName ?: "name")
        userBioEtv.setText(userData?.bio ?: "bio")
        userBdayEtv.setText(DATE_FORMAT.format(userData?.birthday))
        userEmailTv.setText(currentUser?.email ?: "email")

//        TODO("Fix formatting of spinner")
        val GENDER_OPTIONS = arrayOf("Female", "Male", "Non-Binary", "Rather not say")
        userGenderSpinner.setSelection(GENDER_OPTIONS.indexOf(userData!!.gender))

        if (userGenderSpinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, GENDER_OPTIONS)
            userGenderSpinner.adapter = adapter

            userGenderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
//                    Toast.makeText(this@EditProfileActivity,
//                        getString(R.string.selected_item) + " " +
//                                "" + GENDER_OPTIONS[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }
}
