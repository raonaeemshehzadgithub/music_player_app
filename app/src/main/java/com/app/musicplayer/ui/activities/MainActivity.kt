package com.app.musicplayer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.musicplayer.databinding.ActivityMainBinding
import com.app.musicplayer.ui.adapters.ViewPagerAdapter
import com.app.musicplayer.ui.fragments.*
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewPager()
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter.addFragment(AllMusicFragment())
        viewPagerAdapter.addFragment(AlbumsFragment())
        viewPagerAdapter.addFragment(ArtistsFragment())
        viewPagerAdapter.addFragment(RecentlyPlayedFragment())
        viewPagerAdapter.addFragment(MyFavouritesFragment())
        binding.fragmentCallsViewPager?.adapter = viewPagerAdapter
        TabLayoutMediator(
            binding.musicTabs!!, binding.fragmentCallsViewPager!!
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "All Music"
                }
                1 -> {
                    tab.text = "Albums"
                }
                2 -> {
                    tab.text = "Artists"
                }
                3 -> {
                    tab.text = "Recently Played"
                }
                4 -> {
                    tab.text = "My Favourites"
                }
            }
        }.attach()
    }
}