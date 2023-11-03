package com.mobdeve.s12.aiwear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class WardrobeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: WardrobeFragmentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wardrobe_view)

        tabLayout = findViewById(R.id.wardrobe_tablayout)
        viewPager2 = findViewById(R.id.wardrobe_viewpager)

        adapter = WardrobeFragmentAdapter(supportFragmentManager, lifecycle)


        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchQuery = newText ?: ""
                val fragment = adapter.getCurrentFragment(viewPager2.currentItem)
                (fragment as? BaseClothesFragment)?.filterData(searchQuery)
                return true
            }
        }
        )}

}
