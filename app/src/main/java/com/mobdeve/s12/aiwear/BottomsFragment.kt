package com.mobdeve.s12.aiwear

class BottomsFragment : BaseClothesFragment() {

    override fun provideInitialData(): List<ClothesItem> {
        return DataHelper.getItemsByCategory("bottoms")
    }


    override fun isCategoryItem(item: ClothesItem): Boolean {
        return item.category == "bottoms"
    }

    override fun filterData(searchQuery: String) {
        val filteredList = clothesItemList.filter {
            it.name?.contains(searchQuery, ignoreCase = true) ?: false
        }
        adapter.updateData(filteredList)
    }

}