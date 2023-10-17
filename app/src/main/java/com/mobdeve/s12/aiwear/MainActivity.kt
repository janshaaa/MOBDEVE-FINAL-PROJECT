package com.mobdeve.s12.aiwear

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser

        Handler().postDelayed({
            if(user != null) {
                val homeIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
            }
            else {
                val signInIntent = Intent (this, SignInActivity::class.java)
                startActivity(signInIntent)
            }
        }, 1000)

    }

}