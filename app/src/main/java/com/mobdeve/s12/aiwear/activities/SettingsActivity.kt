package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.database.UserDatabase

class SettingsActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Settings"

        mAuth = FirebaseAuth.getInstance()

        val editProfileBtn = findViewById<Button>(R.id.CreateIdeaBtn)
        editProfileBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val bodyInfoBtn = findViewById<Button>(R.id.SchedOOTDBtn)
        bodyInfoBtn.setOnClickListener {
            val intent = Intent(this, BodyInfoActivity::class.java)
            startActivity(intent)
        }

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // Sign Out
        val signOutBtn = findViewById<Button>(R.id.signOutBtn)
        signOutBtn.setOnClickListener {
            signOut()
        }

        // Delete Account
        val delAccBtn = findViewById<Button>(R.id.deleteAccBtn)
        delAccBtn.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }
    }

    private fun signOut() {
        mAuth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Confirm Account Deletion")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, which ->
                // Handle account deletion here
                deleteAccount()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle cancelation or do nothing
                Toast.makeText(
                    this,
                    "Account deletion canceled",
                    Toast.LENGTH_SHORT
                ).show()
                // You might want to finish the activity or perform other actions here
            }
            .show()
    }

    private fun deleteAccount() {
        val currentUser = mAuth.currentUser
        val uuid = currentUser!!.uid
        val userDb = UserDatabase(applicationContext)

        currentUser!!.delete()
            ?.addOnSuccessListener {
                // Account deletion successful
                userDb.deleteUserByUUID(uuid)
                Toast.makeText(
                    this,
                    "Account deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Finish the activity or navigate to the login screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            ?.addOnFailureListener { e ->
                // Handle account deletion failure
                Toast.makeText(
                    this,
                    "Error while deleting account. Try again later",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("DELETE", "Error deleting account: ${e.message}")
            }
    }

}