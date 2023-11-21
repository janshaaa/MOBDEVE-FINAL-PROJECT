package com.mobdeve.s12.aiwear.fragments

import com.mobdeve.s12.aiwear.activities.OnCanvasUpdateListener
import com.mobdeve.s12.aiwear.models.ClothesItem
import com.mobdeve.s12.aiwear.utils.DataHelper

class AllClothesFragment(isInHomeActivity: Boolean, canvasUpdateListener: OnCanvasUpdateListener?) : BaseClothesFragment(isInHomeActivity, canvasUpdateListener) {

    override fun provideInitialData(): List<ClothesItem> {
        return DataHelper.initializeData()
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return true
    }

}
