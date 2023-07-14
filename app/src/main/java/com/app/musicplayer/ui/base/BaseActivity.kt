package com.app.musicplayer.ui.base

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.BsSetRingtoneBinding
import com.app.musicplayer.extentions.getPermissionString
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewState> : AppCompatActivity(), BaseView<VM> {

    abstract override val viewState: VM
    abstract val contentView: View?
    lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var playerMenuCallBack: (String) -> Unit = {}

    @Inject
    lateinit var disposables: CompositeDisposable
    @Inject
    lateinit var strings: StringsInteractor
    @Inject
    lateinit var prefs: PreferenceHelper

    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    private var setRingtoneCallBack: (String) -> Unit = {}
    var isAskingPermissions = false
    var showSettingAlert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        onSetup()
        backPressed()
        viewState.apply {
            attach()
            errorEvent.observe(this@BaseActivity) {
                it.ifNew?.let(this@BaseActivity::showError)
            }

            finishEvent.observe(this@BaseActivity) {
                it.ifNew?.let { finish() }
            }

            messageEvent.observe(this@BaseActivity) {
                it.ifNew?.let(this@BaseActivity::showMessage)
            }
        }
    }

    private fun backPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
//                    finish()
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        viewState.detach()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun finish() {
        finishAndRemoveTask()
    }

    override fun showError(stringResId: Int) {
        applicationContext.toast(strings.getString(stringResId))
    }

    override fun showMessage(stringResId: Int) {
        applicationContext.toast(strings.getString(stringResId))
    }

    fun moveBack() {
        finish()
    }
    fun bsSetRingtone(setRingtoneCallBack: (String) -> Unit) {
        this.setRingtoneCallBack = setRingtoneCallBack
        var setRingtoneValue: String? = null
        var setRingtoneBottomSheet:BottomSheetDialog?=null
        Handler(Looper.getMainLooper()).post {
            setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        }
        val binding = BsSetRingtoneBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet?.setContentView(binding.root)
        defaultCheckedRingtone(binding.setRingtoneGroup, prefs)
        binding.setRingtoneGroup.setOnCheckedChangeListener { group, checked ->
            val radioButton = group.findViewById<RadioButton>(checked)
            setRingtoneValue = radioButton.text.toString()
        }
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet?.isShowing == true) {
                setRingtoneBottomSheet?.dismiss()
            }
        }
        binding.doneButton.setOnClickListener {
            if (setRingtoneValue.equals(PHONE_RINGTONE) or setRingtoneValue.equals(ALARM_RINGTONE)) {
                prefs.setRingtone = setRingtoneValue
            }
            setRingtoneCallBack(DONE)
            if (setRingtoneBottomSheet?.isShowing == true) {
                setRingtoneBottomSheet?.dismiss()
            }
        }
        setRingtoneBottomSheet?.show()
    }

    private fun defaultCheckedRingtone(ringtoneGroup: RadioGroup, prefs: PreferenceHelper) {
        for (count in 0 until ringtoneGroup.childCount) {
            val radioButton: RadioButton = ringtoneGroup.getChildAt(count) as RadioButton
            radioButton.let {
                when (it.text) {
                    prefs.setRingtone -> {
                        it.isChecked = true
                    }
                }
            }
        }
    }

    fun playerMenu(
        menu_btn: ImageView, menuCallBack: (String) -> Unit
    ) {
        this.playerMenuCallBack = menuCallBack
        val wrapper: Context = ContextThemeWrapper(this, R.style.popUpMenuMain)
        val popupMenuSelected = PopupMenu(wrapper, menu_btn)
        popupMenuSelected.inflate(R.menu.player_menu)
        popupMenuSelected.gravity = Gravity.END
        popupMenuSelected.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share_track -> {
                    playerMenuCallBack(SHARE_TRACK)
                }

                R.id.set_track_as -> {
                    playerMenuCallBack(SET_TRACK_AS)
                }

                R.id.delete_track -> {
                    playerMenuCallBack(DELETE_TRACK)
                }

                R.id.settings -> {
                    playerMenuCallBack(SETTINGS)
                }
            }
            true
        }
        popupMenuSelected.show()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(
                this, arrayOf(getPermissionString(permissionId)), GENERIC_PERMISSION_HANDLER
            )
        }
    }

    fun handleNotificationPermission(callback: (granted: Boolean) -> Unit) {
        if (!isTiramisuPlus()) {
            callback(true)
        } else {
            handlePermission(PERMISSION_POST_NOTIFICATIONS) { granted ->
                callback(granted)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERMISSION_HANDLER) {
            for (i in permissions.indices) {
                val per: String = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val showRationale = shouldShowRequestPermissionRationale(per)
                    if (!showRationale) {
                        val builder = AlertDialog.Builder(this@BaseActivity)
                        builder.setTitle("App Permission")
                            .setMessage(R.string.access_storage_from_settings)
                            .setPositiveButton(
                                "Open Settings"
                            ) { _, _ ->
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package",
                                    packageName, null
                                )
                                intent.data = uri
                                startActivityForResult(
                                    intent,
                                    OPEN_SETTINGS
                                )
                                finish()
                            }
                        showSettingAlert = builder.setCancelable(false).create()
                        showSettingAlert?.show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ), 0
                        )
                    }
                } else {
                    actionOnPermission?.invoke(grantResults[0] == 0)
                }
            }
        }
//        else if (requestCode == 0) {
//            for (i in permissions.indices) {
//                val per: String = permissions[i]
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    val showRationale = shouldShowRequestPermissionRationale(per)
//                    if (!showRationale) {
//                        //user clicked on never ask again
//                        val builder = AlertDialog.Builder(this@BaseActivity)
//                        builder.setTitle("App Permission")
//                            .setMessage(R.string.access_storage_from_settings)
//                            .setPositiveButton(
//                                "Open Settings"
//                            ) { _, _ ->
//                                val intent =
//                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                val uri = Uri.fromParts(
//                                    "package",
//                                    packageName, null
//                                )
//                                intent.data = uri
//                                startActivityForResult(
//                                    intent,
//                                    OPEN_SETTINGS
//                                )
//                                finish()
//                            }
//                        showSettingAlert = builder.setCancelable(false).create()
//                        showSettingAlert?.show()
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this, arrayOf(
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_MEDIA_IMAGES
//                            ), 0
//                        )
//                    }
//                } else {
//                    toast("granted")
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_PLAYING_TRACK && resultCode == RESULT_OK) {
            sendIntent(NEXT)
        }
    }
    open fun <VS : BaseViewState> onFragmentSetup(fragment: BaseFragment<VS>) {}

}