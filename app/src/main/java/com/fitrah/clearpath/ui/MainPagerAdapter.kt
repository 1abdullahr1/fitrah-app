package com.fitrah.clearpath.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fitrah.clearpath.ui.guide.GuideFragment
import com.fitrah.clearpath.ui.journey.JourneyFragment
import com.fitrah.clearpath.ui.reflect.ReflectFragment
import com.fitrah.clearpath.ui.salah.SalahFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> JourneyFragment.newInstance()
            1 -> SalahFragment.newInstance()
            2 -> GuideFragment.newInstance()
            3 -> ReflectFragment.newInstance()
            else -> JourneyFragment.newInstance()
        }
    }
}
