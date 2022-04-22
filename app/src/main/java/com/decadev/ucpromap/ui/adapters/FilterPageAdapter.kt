package com.decadev.ucpromap.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class FilterPageAdapter(val pageList: List<Fragment> = listOf(), activity: FragmentActivity) :
    FragmentStateAdapter(activity) {


    override fun getItemCount(): Int = pageList.size

    override fun createFragment(position: Int): Fragment {
        return pageList[position]
    }


}