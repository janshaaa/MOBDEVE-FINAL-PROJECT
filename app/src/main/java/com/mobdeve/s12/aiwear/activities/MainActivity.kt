package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.database.UserDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Loading Firebase user data
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userDb = UserDatabase(applicationContext)

        Handler().postDelayed({
            if(currentUser != null) {
                var userData = userDb.queryUserByUUID(currentUser!!.uid)
                if (userData != null) {
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                }
                else {
                    val registerIntent = Intent(this, RegisterActivity::class.java)
                    startActivity(registerIntent)
                    finish()
                }
            }
            else {
                val signInIntent = Intent (this, SignInActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        }, 1000)

    }

}