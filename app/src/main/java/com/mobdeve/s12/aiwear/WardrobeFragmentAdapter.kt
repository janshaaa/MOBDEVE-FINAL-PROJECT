package com.mobdeve.s12.aiwear

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class WardrobeFragmentAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){


    private val fragmentList: List<Fragment> by lazy {
        listOf(
            AllClothesFragment(),
            TopsFragment(),
            BottomsFragment()
        )
    }

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getCurrentFragment(position: Int): Fragment? {
        return fragmentList.getOrNull(position)
    }


}