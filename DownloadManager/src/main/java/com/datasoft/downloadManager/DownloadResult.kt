package com.datasoft.downloadManager

import java.io.File

sealed class DownloadResult {

    data class ZipDownloadSuccess(val file: File) : DownloadResult()

    data class Error(
            val downloadError: DownloadError,
            val message: String,
            val cause: Exception? = null
    ) : DownloadResult()

    data class Progress(val progress: Float) : DownloadResult()

    data class ZipExtractSuccess(val zipFile: File, val extractedFolder: File) : DownloadResult()

    data class ZipDeleteComplete(val success: Boolean) : DownloadResult()
}

enum class DownloadError {
    NO_INTERNET, INVALID_DOWNLOAD_PATH, ZIP_EXTRACT_ERROR, FAILED_TO_COMPLETE_DOWNLOAD, FAILED_RESPONSE,
    DOWNLOAD_TIMEOUT
}