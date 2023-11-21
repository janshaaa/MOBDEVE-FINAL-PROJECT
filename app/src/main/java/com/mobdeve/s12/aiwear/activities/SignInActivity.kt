package com.mobdeve.s12.aiwear.activities

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.database.UserDatabase
import com.mobdeve.s12.aiwear.database.UserDatabaseHandler
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking


class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2 // Request code for Google sign-in
    private lateinit var db: FirebaseFirestore

    private val EMAIL = "email"
    private val DEFAULT_WEB_CLIENT_ID = "66774895063-0hpt0qt5h42pjr9us2qmlroi1p539mto.apps.googleusercontent.com"

    private val TAG = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Configure Google Sign-In
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(DEFAULT_WEB_CLIENT_ID)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        // Firebase
        mAuth = FirebaseAuth.getInstance()

        val googleSignInBtn = findViewById<Button>(R.id.googleSignInBtn)
        googleSignInBtn.setOnClickListener {
            googleSignIn()
        }
    }

    private fun googleSignIn() {

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Log.d("ONETAPCLIENT", "$oneTapClient")
                Log.d("SIGNINREQUEST", "$signInRequest")
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
                Toast.makeText(
                    this,
                    "Couldn't start One Tap UI. Try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = googleCredential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = mAuth.currentUser
                                        val userData = runBlocking {
                                            FirestoreDatabaseHandler.getUserByUuid(user!!.uid)
                                        }

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

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.d(TAG, e.toString())
                }
            }
        }
    }

}