package com.app.musicplayer.ui.activities

import android.Manifest
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMainBinding
import com.app.musicplayer.extentions.beGone
import com.app.musicplayer.extentions.beVisible
import com.app.musicplayer.extentions.hideKeyboard
import com.app.musicplayer.extentions.onTextChangeListener
import com.app.musicplayer.extentions.showKeyboard
import com.app.musicplayer.ui.adapters.ViewPagerAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.fragments.*
import com.app.musicplayer.ui.viewstates.AlbumsViewState
import com.app.musicplayer.ui.viewstates.ArtistsViewState
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.ui.viewstates.MainViewState
import com.app.musicplayer.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewState>() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }
    override val viewState: MainViewState by viewModels()
    private val tracksViewState: TracksViewState by viewModels()
    private val albumsViewState: AlbumsViewState by viewModels()
    private val artistsViewState: ArtistsViewState by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onSetup() {
        setUpPermission()
        viewState.apply {
            binding.toolbar.searchInput.onTextChangeListener {
                tracksViewState.onFilterChanged(it)
                albumsViewState.onFilterChanged(it)
                artistsViewState.onFilterChanged(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setUpPermission() {
        handleMediaPermissions { success ->
            if (success) {
                setUpViewPager()
                setUpClicks()
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

    private fun setUpClicks() {
        binding.toolbar.search.setOnClickListener {
            showSearch()
        }
        binding.toolbar.moveBack.setOnClickListener {
            hideSearch()
        }
    }

    private fun showSearch() {
        showKeyboard(binding.toolbar.searchInput)
        binding.toolbar.title.beGone()
        binding.toolbar.search.beGone()
        binding.toolbar.moveBack.beVisible()
        binding.toolbar.searchInput.beVisible()
        binding.toolbar.searchInput.requestFocus()
    }

    private fun hideSearch() {
        hideKeyboard()
        binding.toolbar.title.beVisible()
        binding.toolbar.search.beVisible()
        binding.toolbar.moveBack.beGone()
        binding.toolbar.searchInput.beGone()
        binding.toolbar.searchInput.text = null
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
                    tab.text = getString(R.string.all_songs)
                }

                1 -> {
                    tab.text = getString(R.string.albums)
                }

                2 -> {
                    tab.text = getString(R.string.artists)
                }

                3 -> {
                    tab.text = getString(R.string.recently_played)
                }

                4 -> {
                    tab.text = getString(R.string.my_favorites)
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
    override fun onDestroy() {
        super.onDestroy()
        prefs.currentTrackId = 0L
    }
}