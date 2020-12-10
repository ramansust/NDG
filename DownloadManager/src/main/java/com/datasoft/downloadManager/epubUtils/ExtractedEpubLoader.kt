package com.datasoft.downloadManager.epubUtils

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.FileReader
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory


class ExtractedEpubLoader(private val extractedEpubFolder: String) {

    private fun readFromNcxString(xml: String): Document {

        val dbf = DocumentBuilderFactory.newInstance()

        val db = dbf.newDocumentBuilder()
        val inputSource = InputSource()
        inputSource.characterStream = StringReader(xml)
        inputSource.encoding = "ISO-8859-1"
        return db.parse(inputSource)
    }

    private fun getEpubBaseUrl() = "file:///$extractedEpubFolder"

    fun parseNcxFile(): ArrayList<EpubInfo> {
        val ncxPath = "$extractedEpubFolder/OEBPS/toc.ncx"
        val xmlStr = readXmlAsString(ncxPath)
        val doc = readFromNcxString(xmlStr)
        val nl: NodeList = doc.getElementsByTagName("navPoint")

        val baseUrl = getEpubBaseUrl()

        val epubInfos = ArrayList<EpubInfo>()
        for (i in 0 until nl.length) {

            val element = nl.item(i) as Element
            val info = EpubInfo(
                    index = i,
                    htmlLink = getAttributeValue(element, "content", "src")?.run {
                        "$baseUrl/OEBPS/" + this.removeRange(this.indexOf(".xhtml") + 6, this.length)
                    },
                    title = getValue(element, "text"),
                    searchTag = getValue(element, "search")
            )

            epubInfos.add(info)
        }
        return epubInfos
    }

    private fun readXmlAsString(xmlPath: String): String {
        val reader = BufferedReader(FileReader(xmlPath))
        val stringBuilder = StringBuilder()
        var buffer = CharArray(10)
        while (reader.read(buffer) != -1) {
            stringBuilder.append(String(buffer))
            buffer = CharArray(10)
        }
        reader.close()

        return stringBuilder.toString()
    }

    private fun getValue(item: Element, str: String?): String? {
        val n = item.getElementsByTagName(str)
        return getElementValue(n.item(0))
    }

    private fun getAttributeValue(item: Element, tag: String?, attribute: String?): String? {
        val n = item.getElementsByTagName(tag)
        val e = n.item(0) as Element
        return e.getAttribute(attribute)
    }

    private fun getElementValue(elem: Node?): String {
        if (elem != null && elem.hasChildNodes()) {
            var child = elem.firstChild
            while (child != null) {
                if (child.nodeType.toInt() == 3) {
                    return child.nodeValue
                }
                child = child.nextSibling
            }
        }
        return ""
    }

}