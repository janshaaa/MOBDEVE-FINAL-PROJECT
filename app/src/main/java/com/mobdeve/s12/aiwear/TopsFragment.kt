package com.mobdeve.s12.aiwear
class TopsFragment : BaseClothesFragment() {

    override fun provideInitialData(): List<ClothesItem> {
        return DataHelper.getItemsByCategory("tops")
    }

    override fun isCategoryItem(item: ClothesItem): Boolean {
        return item.category == "tops"
    }

}