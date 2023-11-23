package com.mobdeve.s12.aiwear.fragments

import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s12.aiwear.activities.OnCanvasUpdateListener
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.utils.DataHelper
import com.mobdeve.s12.aiwear.utils.FirestoreDatabaseHandler
import kotlinx.coroutines.runBlocking

class TopsFragment(isInHomeActivity: Boolean, canvasUpdateListener: OnCanvasUpdateListener?) : BaseClothesFragment(isInHomeActivity, canvasUpdateListener) {

    override fun provideInitialData(): List<ClothesItem> {
//        return DataHelper.getItemsByCategory("tops")
        return runBlocking {
            FirestoreDatabaseHandler.getClothesByCategory(
                FirebaseAuth.getInstance().currentUser!!.uid,
                "tops"
            )
        }
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return item.category == "tops"
    }

}