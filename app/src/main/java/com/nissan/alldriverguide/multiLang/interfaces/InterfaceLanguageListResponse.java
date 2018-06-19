package com.nissan.alldriverguide.multiLang.interfaces;

import com.nissan.alldriverguide.multiLang.model.LanguageList;

import java.util.List;

public interface InterfaceLanguageListResponse {
    void languageListDownloaded(List<LanguageList> languageLists);
    void languageListFailed(String failedResponse);
}
