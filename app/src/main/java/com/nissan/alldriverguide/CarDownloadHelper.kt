package com.nissan.alldriverguide

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.datasoft.downloadManager.*
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
    object INVALID_ASSET_LINK : CarDownloadProgress()
    object INVALID_LANG_LINK : CarDownloadProgress()
    object UNREACHABLE_ASSET_LINK : CarDownloadProgress()
    object UNREACHABLE_LANG_LINK : CarDownloadProgress()
    object NO_INTERNET : CarDownloadProgress()
}

class CarDownloadHelper @JvmOverloads constructor(
        private val context: Context,
        private val carUniqueName: String,
        private val languageZipLink: String? = null,
        private val assetZipLink: String? = null,
        private val baseSavePath: String,
        private var langSavePath: String? = null,
        private var assetSavePath: String? = null,
        private val preferenceUtil:PreferenceUtil = PreferenceUtil(context),
        private val selectedLang:String = preferenceUtil.selectedLang

) {


    val downloadProgress = MutableLiveData<CarDownloadProgress>()
//    private val carPath = "$baseSavePath/$carUniqueName"


    private fun updateProgress(carDownloadProgress: CarDownloadProgress) {
        downloadProgress.postValue(carDownloadProgress)
    }


    private fun performPreCheck(): Boolean {

        if (!context.isOnline()) {
            downloadProgress.postValue(CarDownloadProgress.NO_INTERNET)
            return false
        }

        fun String?.checkUrlValid(): Boolean {
            if (this == null) return true
            return UrlUtils.isUrlValid(this)
        }

        fun String?.isReachable(): Boolean {
            if (this == null) return true
            return UrlUtils.isUrlReachable(this)
        }


        //Lang Check
        if (languageZipLink.checkUrlValid()) {
            if (!languageZipLink.isReachable()) {
                updateProgress(CarDownloadProgress.UNREACHABLE_LANG_LINK)
                return false
            }

        } else {
            updateProgress(CarDownloadProgress.INVALID_LANG_LINK)
            return false
        }

        //Asset Check
        if (assetZipLink.checkUrlValid()) {
            if (!assetZipLink.isReachable()) {
                updateProgress(CarDownloadProgress.UNREACHABLE_ASSET_LINK)
                return false
            }
        } else {
            updateProgress(CarDownloadProgress.INVALID_ASSET_LINK)
            return false
        }

        return true
    }

    @JvmOverloads
    fun downloadAssetAndLang(
            langDownloaded: Boolean = false,
            currentPath: String = langSavePath ?: baseSavePath,
            downloadLink: String = languageZipLink ?: "",
            previousProgress: Float = 0f,
            overridePreviousDownload: Boolean = false
    ) {

        GlobalScope.launch(Dispatchers.IO) {

            if (!performPreCheck()) return@launch

            if (overridePreviousDownload)
                File(currentPath).delete()


            File(currentPath).mkdirs()

            val progressData = CarDownloadProgress.DOWNLOAD_PROGRESS(previousProgress)

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
                        if (!isAssetAvailable) {
                            if (progressData.progress >= 100.0f || langDownloaded)
                            {
                                extractEpubs()
                                downloadProgress.postValue(CarDownloadProgress.COMPLETE)
                            }
                            else
                                downloadProgress.postValue(CarDownloadProgress.ASSET_DOWNLOAD_COMPLETE)
                        } else if (progressData.progress >= 100.0f || langDownloaded) {
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
                ?: return) + "/" + File(langSavePath).name + "_" + selectedLang
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