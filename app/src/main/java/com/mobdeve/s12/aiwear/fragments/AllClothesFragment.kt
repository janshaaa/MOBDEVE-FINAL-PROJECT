package com.mobdeve.s12.aiwear.fragments

import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.activities.OnCanvasUpdateListener
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking

class AllClothesFragment(isInHomeActivity: Boolean, canvasUpdateListener: OnCanvasUpdateListener?) : BaseClothesFragment(isInHomeActivity, canvasUpdateListener) {

   override fun provideInitialData(): List<ClothesItem> {
//        return DataHelper.initializeData()
        return runBlocking {
            FirestoreDatabaseHandler.getAllClothes(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
        }
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return true
    }

}
