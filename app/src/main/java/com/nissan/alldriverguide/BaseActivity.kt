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
        fun checkCarDownloadProgress(context: Context, carDownloadProgress: CarDownloadProgress,
                                     progressDialog: ProgressDialog?) {

            when (carDownloadProgress) {
                is DOWNLOAD_PROGRESS ->
                    onDownloadProgress(context, carDownloadProgress, progressDialog)
                CarDownloadProgress.FAILED -> {
                    showErrorDialog(context, "Error ! Unable to update content, Please try again.");
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.INVALID_LANG_LINK, CarDownloadProgress.INVALID_ASSET_LINK -> {
                    showErrorDialog(context, "Invalid Download data")
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.UNREACHABLE_LANG_LINK, CarDownloadProgress.UNREACHABLE_ASSET_LINK -> {
                    showErrorDialog(context, "Requested Data does not exist on our servers")
                    progressDialog?.dismiss()
                }

                CarDownloadProgress.NO_INTERNET -> {
                    showErrorDialog(context, "No internet connection, Please try again")
                    progressDialog?.dismiss()
                }

                else -> {
                    showErrorDialog(context, "Error ! Unable to update content, Please try again.");
                    progressDialog?.dismiss()
                }
            }
        }

        internal fun onDownloadProgress(context: Context, progress: DOWNLOAD_PROGRESS,
                                        progressDialog: ProgressDialog?) {


            val resources = context.resources

            if (progressDialog != null) {
                val formattedString = String.format("%.02f", progress)
                progressDialog.setMessage("""
                ${resources.getStringArray(R.array.car_names)[Values.carType - 1]}
                ${resources.getString(R.string.alert_download_complete)}$formattedString%
                """.trimIndent())
            }
        }

        internal fun showErrorDialog(context: Context, msg: String) {
            val dialogFragment = DialogErrorFragment.getInstance(context, msg)
            if (context is AppCompatActivity)
                dialogFragment.show(context.supportFragmentManager, "error_fragment")
        }
    }
}