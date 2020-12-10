package com.datasoft.downloadManager

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

object ZipDownloader {

    fun downloadZipAndExtract(
            context: Context,
            zipUrl: String,
            zipExtractPath: String,
            downloadZipOnCache: Boolean = true,
            downloadZipOnDataDir: Boolean = false,
            downloadZipOnSpecificPath: String? = null
    ) = flow {
        withContext(Dispatchers.IO) {
            val zipExtractFolder = File(zipExtractPath)

            zipExtractFolder.mkdirs() // No need to add this line

            val downloader = FileDownloader.Builder(context, zipUrl)
                    .apply {
                        when {
                            downloadZipOnSpecificPath != null ->
                                this.setDownloadPath(downloadZipOnSpecificPath)
                            downloadZipOnDataDir -> this.saveToDataDir()
                            else -> this.saveToCacheDir()
                        }
                    }.build()

            downloader.download().collect(action = {
                emit(it)
                if (it is DownloadResult.ZipDownloadSuccess) {
                    try {
                        UnZipper.unzip(it.file, zipExtractFolder)
                        emit(DownloadResult.ZipExtractSuccess(it.file, zipExtractFolder))
                        val success = it.file.delete()
                        emit(DownloadResult.ZipDeleteComplete(success))
                    } catch (e: IOException) {
                        emit(
                                DownloadResult.Error(
                                        DownloadError.ZIP_EXTRACT_ERROR, "Zip extract error", e
                                )
                        )
                    }
                }
            })
        }
    }
}