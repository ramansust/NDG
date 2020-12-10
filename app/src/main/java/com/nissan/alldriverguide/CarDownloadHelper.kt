package com.nissan.alldriverguide

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.datasoft.downloadManager.DownloadResult
import com.datasoft.downloadManager.UnZipper
import com.datasoft.downloadManager.ZipDownloader
import kotlinx.coroutines.flow.collect
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

class CarDownloadHelper(
        private val context: Context,
        private val carUniqueName: String,
        private val languageZipLink: String,
        private val assetZipLink: String,
        private val baseSavePath: String
) {
    val downloadProgress = MutableLiveData<CarDownloadProgress>()
    private val carPath = "$baseSavePath/$carUniqueName"
    suspend fun download(
            langDownloaded: Boolean = false,
            currentPath: String = "$carPath/lang",
            downloadLink: String = languageZipLink,
            previousProgress: Float = 0f,
            overridePreviousDownload: Boolean = false
    ) {

        if (overridePreviousDownload)
            File(currentPath).delete()


        File(currentPath).mkdirs()

        val progressData = CarDownloadProgress.DOWNLOAD_PROGRESS(previousProgress)

        ZipDownloader.downloadZipAndExtract(
                context,
                downloadLink,
                zipExtractPath = currentPath
        ).collect {
            when (it) {
                is DownloadResult.Progress -> {
                    progressData.progress =
                            if (!langDownloaded)
                                it.progress / 2f
                            else
                                progressData.progress + it.progress / 2f

                    downloadProgress.postValue(progressData)
                }
                is DownloadResult.ZipDeleteComplete -> {
                    if (progressData.progress >= 100.0f || langDownloaded) {
                        downloadProgress.postValue(CarDownloadProgress.ASSET_DOWNLOAD_COMPLETE)
                    } else {
                        downloadProgress.postValue(CarDownloadProgress.LANG_DOWNLOAD_COMPLETE)
                        download(
                                true,
                                "$carPath/assets",
                                assetZipLink,
                                progressData.progress
                        )
                    }
                }

            }
        }

    }

    suspend fun extractEpubs() {

        val langPath = "$carPath/lang"
        val langFolder = File(langPath)
        val exists = langFolder.exists()
        if (!exists) return
        val langFolderChild = langFolder.list() ?: return
        if (langFolderChild.isNotEmpty()) {
            //Extracted car Name
            val extractedZipLocation = langPath + "/" + langFolderChild[0]

            val epubs = File(extractedZipLocation).listFiles { file ->
                return@listFiles !file.isDirectory && !file.endsWith(".epub")
            }
            if (epubs == null || epubs.isEmpty()) return

            for (epub in epubs) {
                val extractPath = "$langPath/${epub.nameWithoutExtension}"
                UnZipper.extractEpub(epub, File(extractPath))
            }
        }


    }

}