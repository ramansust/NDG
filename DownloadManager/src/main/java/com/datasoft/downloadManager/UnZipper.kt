package com.datasoft.downloadManager

import ir.mahdi.mzip.zip.ZipArchive
import java.io.File

object UnZipper {

    @Throws(java.io.IOException::class)
    fun unzip(zipFile: File?, targetDirectory: File?) {
        ZipArchive.unzip(zipFile?.absolutePath, targetDirectory?.absolutePath, "")
    }

    fun extractEpub(epubFile: File?, targetDirectory: File?) {
        ZipArchive.unzip(epubFile?.absolutePath, targetDirectory?.absolutePath, "")
    }
}