package com.datasoft.downloadManager.epubUtils

data class EpubInfo(
        val title: String?,
        val htmlLink: String?,
        val index: Int = 0,
        val searchTag: String? = null
)
