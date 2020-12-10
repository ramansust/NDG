package com.datasoft.downloadManager

import android.os.Environment
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.datasoft.downloadManager.epubUtils.ExtractedEpubLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DownloadTest {
    @Test
    fun downloadTest() {

        var path = Environment.getExternalStorageDirectory().absolutePath + "/.AllDriverGuide"

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val downloader = FileDownloader
                .Builder(appContext, "https://file-examples-com.github.io/uploads/2017/02/zip_5MB.zip")
//            .Builder(appContext, "http://192.168.1.253:5000/fbsharing/p3TrVjgY")
//            .saveToCacheDir()
                .setDownloadPath(path)
                .build()
        GlobalScope.launch {
            val result = downloader.download().asLiveData(Dispatchers.IO)
            withContext(Dispatchers.Main)
            {
                result.observeForever {
                    Log.d("DownloadProgress", it.toString())
                }
            }
        }

        while (true);
    }


    @Test
    fun downloadEpubZipAndExtract() {
//        var path = Environment.getExternalStorageDirectory().absolutePath + "/.AllDriverGuide"
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        var path = appContext.dataDir.absolutePath

        val downloadResult = ZipDownloader.downloadZipAndExtract(
                appContext,
                "http://cdn.maruboshi.nl/assets/media/uploads/epub/jukef16_en.zip",
                zipExtractPath = "$path/Car456"
        )
        var shudContinue = true
        GlobalScope.launch {
            val result = downloadResult.asLiveData(Dispatchers.IO)
            withContext(Dispatchers.Main)
            {
                result.observeForever {
                    Log.d("DownloadProgress", it.toString())
                    if (it is DownloadResult.ZipDeleteComplete)
                        shudContinue = false
                }
            }
        }

        while (shudContinue);
    }

    @Test
    fun testEpubExtract() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        var path = appContext.dataDir.absolutePath
        val epub =
                File("$path/Car456/jukef16_en/button_en.epub")
        val extractLocation =
                File("$path/Car456/jukef16_en/extractedEpub")
        extractLocation.mkdirs()

        UnZipper.extractEpub(epub, extractLocation)
    }

    @Test
    fun testEpubReading() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        var path = appContext.dataDir.absolutePath
        val extractedEpubLocation = "$path/Car456/jukef16_en/extractedEpub"
        val extractedEpubLoader = ExtractedEpubLoader(extractedEpubLocation)
        extractedEpubLoader.parseNcxFile()
    }
}