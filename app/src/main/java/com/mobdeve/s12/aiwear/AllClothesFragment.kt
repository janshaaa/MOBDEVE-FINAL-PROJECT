package com.mobdeve.s12.aiwear

class AllClothesFragment : BaseClothesFragment() {

    override fun provideInitialData(): List<ClothesItem> {
        return DataHelper.initializeData()
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return true
    }


}
