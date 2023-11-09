package com.mobdeve.s12.aiwear.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mobdeve.s12.aiwear.models.UserModel
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.R
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
    private lateinit var saveProfileBtn : Button

    private lateinit var editTextFields: List<EditText>

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    private val GENDER_OPTIONS = arrayOf("Female", "Male", "Non-Binary", "Rather not say")

    private lateinit var users: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Edit Profile"

        users = DataHelper.generateUsers()

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var userData = users.find { it.uid == currentUser!!.uid }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        if (userData == null) {
            userData = UserModel(
                currentUser!!.uid,
                "Kim Chaewon",
                "MOBDEVE wow!",
                "Female",
                dateFormat.parse("2000-08-01")
            )
        }

        initializeUserInfo(currentUser, userData)
        saveProfileBtn = findViewById(R.id.saveBodyBtn)
        saveProfileBtn.isEnabled = false

        editTextFields = listOf(
            userNameEtv,
            userBioEtv,
            userBdayEtv
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
            userData?.displayName = userNameEtv.text.toString()
            userData?.bio = userBioEtv.text.toString()
            userData?.birthday = DATE_FORMAT.parse(userBdayEtv.text.toString())
            userData?.gender = GENDER_OPTIONS[userGenderSpinner.selectedItemPosition]

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

//        TODO("Changing of profile picture/uploading")

//        TODO("Fix formatting of spinner")
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
                    saveProfileBtn.isEnabled = GENDER_OPTIONS[position] != userData?.gender

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
        return (this.userNameEtv.text.toString() == userData?.displayName) and
                (this.userBioEtv.text.toString() == userData?.bio) and
                (this.userBdayEtv.text.toString() == DATE_FORMAT.format(userData?.birthday))
    }
}
