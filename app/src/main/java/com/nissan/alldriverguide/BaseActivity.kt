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
                is CarDownloadProgress.FAILED -> {
                    showErrorDialog(context, "Error ! Unable to update content, Please try again.");
                    progressDialog?.dismiss()
                }

            }
        }

        private fun onDownloadProgress(context: Context, progress: DOWNLOAD_PROGRESS,
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

        private fun showErrorDialog(context: Context, msg: String) {
            val dialogFragment = DialogErrorFragment.getInstance(context, msg)
            if (context is AppCompatActivity)
                dialogFragment.show(context.supportFragmentManager, "error_fragment")
        }
    }
}