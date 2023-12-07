package com.mobdeve.s12.aiwear.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mobdeve.s12.aiwear.R
import com.mobdeve.s12.aiwear.models.UserModel

class ViewOutfitActivity : AppCompatActivity() {

    companion object {
        const val SELECTED_DATE_KEY = "selectedDate"
        const val CURRENT_USER_KEY = "currentUser"
        const val OUTFIT_PATH_KEY = "outfitPath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_outfit)

        val selectedDate = intent.getStringExtra(SELECTED_DATE_KEY)
        val currentUser = intent.getStringExtra(CURRENT_USER_KEY).toString()
        val outfitPath = intent.getStringExtra(OUTFIT_PATH_KEY)

        val outfitOOTDIv = findViewById<ImageView>(R.id.outfitOOTDIv)
        Glide.with(this).load(outfitPath).into(outfitOOTDIv)

        // Set header
        initializeHeader(selectedDate!!)
    }

    private fun initializeHeader(selectedDate: String) {
        val headerTv = findViewById<TextView>(R.id.settingsHeaderTv)
        headerTv.text = "Outfit of the Day"

        val date = UserModel.DATE_FORMAT.parse(selectedDate)
        val formattedDate = CreateOutfitActivity.DATE_FORMAT.format(date)
        val dateTv = findViewById<TextView>(R.id.OOTDdateTv)
        dateTv.text = "Outfit for ${formattedDate}"

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

}

