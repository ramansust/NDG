package com.datasoft.downloadManager

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.apache.commons.io.FilenameUtils
import java.io.File

class FileDownloader private constructor(private val url: String, private val savePath: String) {

    suspend fun download(): Flow<DownloadResult> {
        val downloader = HttpClient(Android)
        return downloader.downloadFile(savePath, url)
    }

    class Builder(private val context: Context, private val url: String) {
        private var defaultPath = context.cacheDir.absolutePath
        private var defaultName = FilenameUtils.getName(url)

        @RequiresApi(Build.VERSION_CODES.N)
        fun saveToDataDir(): Builder {
            defaultPath = context.dataDir.absolutePath
            return this
        }

        fun saveToCacheDir(): Builder {
            defaultPath = context.cacheDir.absolutePath
            return this
        }

        fun setDownloadPath(path: String): Builder {
            defaultPath = path.run {
                val currentLength = this.length
                if (this.endsWith("/"))
                    this.removeRange(currentLength - 1, currentLength)
                else this
            }
            return this
        }

        fun setFileName(name: String): Builder {
            defaultName = name
            return this
        }

        fun build() = FileDownloader(url, "$defaultPath/$defaultName")
    }
}

suspend fun HttpClient.downloadFile(savePath: String, url: String): Flow<DownloadResult> {
    return flow {
        try {
            val response = call {
                url(url)
                method = HttpMethod.Get
            }.response

            val data = ByteArray(response.contentLength()!!.toInt())
            var offset = 0

            do {
                val currentRead = response.content.readAvailable(data, offset, data.size)
                offset += currentRead
                val progress = (offset * 100f / data.size)
                emit(DownloadResult.Progress(progress))
            } while (currentRead > 0)

            response.close()

            if (response.status.isSuccess()) {
                withContext(Dispatchers.IO) {
                    val file = File(savePath)
                    file.writeBytes(data)
                    emit(DownloadResult.ZipDownloadSuccess(file = file))
                }

            } else {
                emit(DownloadResult.Error(DownloadError.FAILED_RESPONSE, "File not downloaded"))
            }
        } catch (e: TimeoutCancellationException) {
            emit(DownloadResult.Error(DownloadError.DOWNLOAD_TIMEOUT, "Connection timed out", e))
        } catch (t: Throwable) {
            emit(DownloadResult.Error(DownloadError.NO_INTERNET, "Failed to connect"))
            t.printStackTrace()
        }
    }
}