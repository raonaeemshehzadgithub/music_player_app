package com.app.musicplayer.ui.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.app.musicplayer.databinding.ActivityMainBinding
import com.app.musicplayer.ui.adapters.ViewPagerAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.fragments.*
import com.app.musicplayer.ui.viewstates.SongsViewState
import com.app.musicplayer.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<SongsViewState>() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewState: SongsViewState by viewModels()
    override val contentView: View by lazy { binding.root }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onSetup() {
        setUpPermission()
        viewState.apply {

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setUpPermission() {
        handleMediaPermissions { success ->
            if (success) {
                setUpViewPager()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ), GENERIC_PERMISSION_HANDLER
                )
            }
        }
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter.addFragment(AllMusicFragment())
        viewPagerAdapter.addFragment(AlbumsFragment())
        viewPagerAdapter.addFragment(ArtistsFragment())
        viewPagerAdapter.addFragment(RecentlyPlayedFragment())
        viewPagerAdapter.addFragment(MyFavouritesFragment())
        binding.fragmentCallsViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(
            binding.musicTabs, binding.fragmentCallsViewPager
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleMediaPermissions(callback: (granted: Boolean) -> Unit) {
        handlePermission(getPermissionToRequest()) { granted ->
            callback(granted)
            if (granted && isRPlus()) {
                if (isTiramisuPlus()) {
                    handlePermission(PERMISSION_READ_MEDIA_AUDIOS) {}
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        launchGrantAllFilesDialog()
//                    }
//                }
            }
        }
    }
}