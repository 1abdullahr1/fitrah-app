package com.fitrah.clearpath

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fitrah.clearpath.databinding.ActivityMainBinding
import com.fitrah.clearpath.ui.MainPagerAdapter
import com.fitrah.clearpath.ui.onboarding.DeductiveOnboardingActivity
import com.fitrah.clearpath.util.OnboardingManager
import com.fitrah.clearpath.util.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var onboardingManager: OnboardingManager
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeManager = ThemeManager(this)
        themeManager.applySavedTheme()

        onboardingManager = OnboardingManager(this)
        if (!onboardingManager.isOnboardingCompleted) {
            startActivity(Intent(this, DeductiveOnboardingActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerAndNavigation()
    }

    private fun setupViewPagerAndNavigation() {
        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.isUserInputEnabled = true

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val targetItemId = when (position) {
                    0 -> R.id.nav_journey
                    1 -> R.id.nav_salah
                    2 -> R.id.nav_guide
                    3 -> R.id.nav_reflect
                    else -> R.id.nav_journey
                }
                if (binding.bottomNavigation.selectedItemId != targetItemId) {
                    binding.bottomNavigation.selectedItemId = targetItemId
                }
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val targetPage = when (item.itemId) {
                R.id.nav_journey -> 0
                R.id.nav_salah -> 1
                R.id.nav_guide -> 2
                R.id.nav_reflect -> 3
                else -> 0
            }
            if (binding.viewPager.currentItem != targetPage) {
                binding.viewPager.setCurrentItem(targetPage, true)
            }
            true
        }
    }
}
