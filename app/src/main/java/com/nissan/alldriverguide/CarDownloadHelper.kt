package com.nissan.alldriverguide

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.datasoft.downloadManager.DownloadResult
import com.datasoft.downloadManager.UnZipper
import com.datasoft.downloadManager.ZipDownloader
import com.nissan.alldriverguide.database.PreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

sealed class CarDownloadProgress {
    object STARTED : CarDownloadProgress()
    object LANG_DOWNLOAD_COMPLETE : CarDownloadProgress()
    object LANG_DOWNLOAD_FAILED : CarDownloadProgress()
    object LANG_EXTRACT_COMPLETE : CarDownloadProgress()
    object LANG_EXTRACT_FAILED : CarDownloadProgress()
    object LANG_ALL_ERROR : CarDownloadProgress()
    object ASSET_DOWNLOAD_COMPLETE : CarDownloadProgress()
    object ASSET_DOWNLOAD_FAILED : CarDownloadProgress()
    object ASSET_EXTRACT_COMPLETE : CarDownloadProgress()
    object ASSET_EXTRACT_FAILED : CarDownloadProgress()
    object ASSET_ALL_ERROR : CarDownloadProgress()
    data class DOWNLOAD_PROGRESS(var progress: Float) : CarDownloadProgress()
    object COMPLETE : CarDownloadProgress()
    object FAILED : CarDownloadProgress()
}

class CarDownloadHelper @JvmOverloads constructor(
        private val context: Context,
        private val carUniqueName: String,
        private val languageZipLink: String? = null,
        private val assetZipLink: String? = null,
        private val baseSavePath: String,
        private var langSavePath: String? = null,
        private var assetSavePath: String? = null
) {
    val downloadProgress = MutableLiveData<CarDownloadProgress>()
//    private val carPath = "$baseSavePath/$carUniqueName"


    private val preferenceUtil = PreferenceUtil(context)

    @JvmOverloads
    fun downloadAssetAndLang(
            langDownloaded: Boolean = false,
            currentPath: String = langSavePath ?: baseSavePath,
            downloadLink: String = languageZipLink ?: "",
            previousProgress: Float = 0f,
            overridePreviousDownload: Boolean = false
    ) {

        if (overridePreviousDownload)
            File(currentPath).delete()


        File(currentPath).mkdirs()

        val progressData = CarDownloadProgress.DOWNLOAD_PROGRESS(previousProgress)

        GlobalScope.launch(Dispatchers.IO) {
            val isAssetAvailable = !assetZipLink.isNullOrEmpty()

            if (langDownloaded)
                extractEpubs()

            ZipDownloader.downloadZipAndExtract(
                    context,
                    downloadLink,
                    zipExtractPath = currentPath
            ).collect {
                when (it) {
                    is DownloadResult.Progress -> {
                        progressData.progress =
                                if (!isAssetAvailable)
                                    it.progress
                                else if (!langDownloaded)
                                    it.progress / 2f
                                else
                                    50 + it.progress / 2f

                        downloadProgress.postValue(progressData)
                    }
                    is DownloadResult.ZipDeleteComplete -> {
                        if (!isAssetAvailable)
                            downloadProgress.postValue(CarDownloadProgress.ASSET_DOWNLOAD_COMPLETE)
                        else if (progressData.progress >= 100.0f || langDownloaded) {
                            downloadProgress.postValue(CarDownloadProgress.COMPLETE)
                        } else if (!assetZipLink.isNullOrEmpty()) {
                            downloadProgress.postValue(CarDownloadProgress.LANG_DOWNLOAD_COMPLETE)
                            downloadAssetAndLang(
                                    true,
                                    assetSavePath ?: baseSavePath,
                                    assetZipLink,
                                    progressData.progress
                            )
                        }


                    }

                }
            }
        }

    }

    fun extractEpubs() {

        val langPath = (langSavePath
                ?: return) + "/" + File(langSavePath).name + "_" + preferenceUtil.selectedLang
        val langFolder = File(langPath)
        val exists = langFolder.exists()
        if (!exists) return

        val epubs = File(langPath).listFiles { file ->
            return@listFiles !file.isDirectory && !file.endsWith(".epub")
        }
        if (epubs == null || epubs.isEmpty()) return

        for (epub in epubs) {
            val extractPath = getExtractPath(langPath, epub)
            UnZipper.extractEpub(epub, File(extractPath))
            epub.delete()
        }

    }

    private fun getExtractPath(langPath: String, ePub: File): String {
        val fileName = ePub.name

        return when {
            fileName.contains("homepage") -> "$langPath/.ar_homepage"

            fileName.contains("info") -> "$langPath/.ar_info"

            fileName.contains("combimeter") -> "$langPath/.ar_combimeter"

            fileName.contains("button") -> "$langPath/.ar_button"

            fileName.contains("tyre") -> "$langPath/.ar_tyre";

            fileName.contains("engine") -> "$langPath/.ar_engine"

            fileName.contains("warranty") -> "$langPath/.ar_warranty"

            else -> ""
        }
    }


}