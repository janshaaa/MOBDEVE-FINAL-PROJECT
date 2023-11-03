package com.mobdeve.s12.aiwear

class BottomsFragment : BaseClothesFragment() {

    override fun provideInitialData(): List<ClothesItem> {
        return DataHelper.getItemsByCategory("bottoms")
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return item.category == "bottoms"
    }


}