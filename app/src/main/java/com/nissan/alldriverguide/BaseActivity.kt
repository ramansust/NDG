package com.nissan.alldriverguide

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.nissan.alldriverguide.CarDownloadProgress.DOWNLOAD_PROGRESS
import com.nissan.alldriverguide.utils.DialogErrorFragment
import com.nissan.alldriverguide.utils.Values

open class BaseActivity : AppCompatActivity() {


    companion object {
        @JvmStatic
        @JvmOverloads
        fun checkCarDownloadProgress(context: Context, carDownloadProgress: CarDownloadProgress,
                                     progressDialog: ProgressDialog?,
                                     showCarName: Boolean = false
        ) {

            when (carDownloadProgress) {
                is DOWNLOAD_PROGRESS ->
                    onDownloadProgress(context, carDownloadProgress, progressDialog, showCarName)
                CarDownloadProgress.FAILED -> {
                    showErrorDialog(context, "Error ! Unable to update content, Please try again.")
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.INVALID_LANG_LINK,
                CarDownloadProgress.INVALID_ASSET_LINK -> {
                    showErrorDialog(context, "Invalid Download data")
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.UNREACHABLE_LANG_LINK,
                CarDownloadProgress.UNREACHABLE_ASSET_LINK -> {
                    showErrorDialog(context, "Requested Data does not exist on our servers")
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.NO_INTERNET -> {
                    showErrorDialog(context, "No internet connection, Please try again")
                    progressDialog?.dismiss()
                }
                CarDownloadProgress.LANG_DOWNLOAD_COMPLETE,
                CarDownloadProgress.ASSET_DOWNLOAD_COMPLETE -> {

                }

                else -> {
                    showErrorDialog(context, "Error ! Unable to update content, Please try again.")
                    progressDialog?.dismiss()
                }
            }
        }

        internal fun onDownloadProgress(context: Context, progress: DOWNLOAD_PROGRESS,
                                        progressDialog: ProgressDialog?,
                                        showCarName: Boolean) {


            val resources = context.resources

            if (progressDialog != null) {
                val formattedString = String.format("%.02f", progress.progress)
                val msg = StringBuilder().run {
                    if (showCarName)
                        append("${resources.getStringArray(R.array.car_names)[Values.carType - 1]}\n")
                    append("${resources.getString(R.string.alert_download_complete)}$formattedString%")
                    toString()
                }
                progressDialog.setMessage(msg)
            }
        }

        internal fun showErrorDialog(context: Context, msg: String) {
            val dialogFragment = DialogErrorFragment.getInstance(context, msg)
            if (context is AppCompatActivity)
                dialogFragment.show(context.supportFragmentManager, "error_fragment")
        }
    }
}