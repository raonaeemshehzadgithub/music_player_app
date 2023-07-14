package com.app.musicplayer.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.PopupTextFieldBinding
import com.app.musicplayer.databinding.PopupTrackPropertiesBinding
import com.app.musicplayer.extentions.formatMillisToHMS
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.utils.DELETE_TRACK
import com.app.musicplayer.utils.PROPERTIES_TRACK
import com.app.musicplayer.utils.RENAME_TRACK
import com.app.musicplayer.utils.SHARE_TRACK
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

abstract class BaseFragment<out VM : BaseViewState> : Fragment(), BaseView<VM> {

    private var _onFinishListener: () -> Unit = {}

    @Inject
    lateinit var baseActivity: BaseActivity<*>
    private var trackMenuCallBack: (String) -> Unit = {}
    private var renameTrackCallBack: (String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = contentView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSetup()
        viewState.apply {
            attach()
            errorEvent.observe(this@BaseFragment) {
                it.ifNew?.let { this@BaseFragment::showError }
            }
            finishEvent.observe(this@BaseFragment) {
                it.ifNew?.let { finish() }
            }
            messageEvent.observe(this@BaseFragment) {
                it.ifNew?.let { this@BaseFragment::showMessage }
            }
        }
        baseActivity.onFragmentSetup(this)
    }

    override fun showError(@StringRes stringResId: Int) {
        baseActivity.viewState.errorEvent.call(stringResId)
    }

    override fun showMessage(@StringRes stringResId: Int) {
        baseActivity.viewState.messageEvent.call(stringResId)
    }

    override fun finish() {
        super.finish()
        _onFinishListener.invoke()
    }

    fun setOnFinishListener(onFinishListener: () -> Unit) {
        _onFinishListener = onFinishListener
    }

    fun showTrackMenu(view: View, menuCallBack: (String) -> Unit) {
        this.trackMenuCallBack = menuCallBack
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.popUpMenuMain)
        val popupMenuSelected = PopupMenu(wrapper, view)
        popupMenuSelected.inflate(R.menu.track_menu)
        popupMenuSelected.gravity = Gravity.END
        popupMenuSelected.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share -> {
                    trackMenuCallBack(SHARE_TRACK)
                }

                R.id.delete -> {
                    trackMenuCallBack(DELETE_TRACK)
                }

                R.id.rename -> {
                    trackMenuCallBack(RENAME_TRACK)
                }

                R.id.properties -> {
                    trackMenuCallBack(PROPERTIES_TRACK)
                }
            }
            true
        }
        popupMenuSelected.show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun launchGrantAllFilesDialog() {
        MaterialAlertDialogBuilder(requireContext()).setMessage(getString(R.string.access_storage_prompt))
            .setPositiveButton("OK") { _, _ ->
                if (!Environment.isExternalStorageManager()) {
                    launchGrantAllFilesIntent()
                }
            }.setCancelable(false).show()
    }

    fun launchGrantAllFilesIntent() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("package:${requireContext().packageName}")
            startActivityForResult(intent, 214)
            finish()
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            try {
                startActivity(intent)
            } catch (e: Exception) {
            }
        }
    }

    fun showTrackPropertiesDialog(track:TrackCombinedData) {
        val binding = PopupTrackPropertiesBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme)
        builder.setView(view)
        binding.path.text = track.track.path?:""
        binding.name.text = track.track.path?.substringAfterLast("/")?.substringBeforeLast(".")
        binding.duration.text = formatMillisToHMS(track.track.duration ?: 0L)
        binding.artist.text = track.track.artist?.isUnknownString()
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    fun showTrackRenameMenu(name: String, renameCallBack: (String) -> Unit) {
        this.renameTrackCallBack = renameCallBack
        val binding = PopupTextFieldBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme)
        builder.setTitle("Rename Track")
        builder.setView(view)
        binding.etName.setText(name.substringBeforeLast("."))
        builder.setPositiveButton("Rename") { dialog, _ ->
            renameTrackCallBack(binding.etName.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    abstract val contentView: View?
    abstract val manager: RecyclerView.LayoutManager
    abstract override val viewState: VM
}