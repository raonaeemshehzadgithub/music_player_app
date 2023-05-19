package com.app.musicplayer.ui.base

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.extentions.getPermissionString
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewState> : AppCompatActivity(), BaseView<VM> {

    abstract override val viewState: VM
    abstract val contentView: View?
    lateinit var linearLayoutManager: RecyclerView.LayoutManager

    @Inject
    lateinit var disposables: CompositeDisposable

    @Inject
    lateinit var strings: StringsInteractor

    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
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
                    finish()
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

    open fun <VS : BaseViewState> onFragmentSetup(fragment: BaseFragment<VS>) {}

}