package com.mobdeve.s12.aiwear.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.UserModel

class BodyInfoActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var headerTv: TextView = findViewById<TextView>(R.id.settingsHeaderTv)

    private lateinit var saveBodyBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_info)

        headerTv.text = "Body Information"

        // Loading Firebase user data
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
//        val userDb = UserDatabase(applicationContext)
//        var userData = userDb.queryUserByUUID(currentUser!!.uid)

        // TODO("Initialize user body information from database")

        saveBodyBtn = findViewById(R.id.saveBodyBtn)
        saveBodyBtn.setOnClickListener {

            // TODO("Update user info")

            Toast.makeText(
                this,
                "Successfully updated body information!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}