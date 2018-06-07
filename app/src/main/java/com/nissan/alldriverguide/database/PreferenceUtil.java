package com.nissan.alldriverguide.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.multiLang.model.Tutorial;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceUtil {

    public SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    private final String IS_FIRST_TIME = "is_first_time";
    private final String SELECTED_LANG = "selected_lang";
    private final String IS_DATABASE_EMPTY = "is_database_empty";
    private final String PUSH_REGISTRATION = "PUSH_REGISTRATION";
    private final String PUSH_REGISTRATION_STATUS = "push_registration_status";
    private final String RATE_APP = "rate_app";
    private final String IS_FIRST_TIME_GREAT_NOT_GREAT = "is_first_time_great_not_great";

    private final String SESSION_ONE = "session_one";
    private final String SESSION_THREE = "session_three";
    private final String IS_GREAT = "is_great";


    public PreferenceUtil(Context mContext) {
        super();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean getIsFirstTime() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }

    public void setIsFirstTime(boolean isFirst) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(IS_FIRST_TIME, isFirst);
        spEditor.apply();
    }
    public boolean getIsDatabaseEmpty() {
        return sharedPreferences.getBoolean(IS_DATABASE_EMPTY, true);
    }

    public void setIsDatabaseEmpty(boolean isFirst) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(IS_DATABASE_EMPTY, isFirst);
        spEditor.apply();
    }
    public String getSelectedLang() {
        return sharedPreferences.getString(SELECTED_LANG, "");
    }

    public void setSelectedLang(String lang) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SELECTED_LANG, lang);
        spEditor.apply();
    }

    //this is for storing the epub list for search
    public void storeMultiLangData(List<?> data, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(key, json);
        editor.apply();
    }


    public String retrieveMultiLangData(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        return json;
    }

    //this is for storing the epub list for search
    public void storeSearchEpubList(ArrayList<EpubInfo> data, String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(id, json);
        editor.apply();
    }


    public ArrayList<EpubInfo> retrieveSearchEpubList(String id) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(id, null);
        Type type = new TypeToken<ArrayList<EpubInfo>>() {        }.getType();
        ArrayList<EpubInfo> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public String getPushRegistrationID() {
        return sharedPreferences.getString(PUSH_REGISTRATION, "");
    }

    public void setPushRegistrationID(String push_registration_id) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(PUSH_REGISTRATION, push_registration_id);
        spEditor.apply();
    }

    public boolean getPushRegistrationStatus() {
        return sharedPreferences.getBoolean(PUSH_REGISTRATION_STATUS, false);
    }

    public void setPushRegistrationStatus(boolean bool) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(PUSH_REGISTRATION_STATUS, bool);
        spEditor.apply();
    }

    public int getOpenCountForRateApp() {
        return sharedPreferences.getInt(RATE_APP, 0);
    }

    public void setOpenCountForRateApp() {
        int count = getOpenCountForRateApp() + 1;

        spEditor = sharedPreferences.edit();
        spEditor.putInt(RATE_APP, count);
        spEditor.apply();
    }

    public void resetUserNavigationCount() {
        spEditor = sharedPreferences.edit();
        spEditor.putInt(RATE_APP, 0);
        spEditor.apply();
    }

    public boolean getIsFirstTimeGreatNotGreat() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_GREAT_NOT_GREAT, true);
    }

    public void setIsFirstTimeGreatNotGreat(boolean isFirst) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(IS_FIRST_TIME_GREAT_NOT_GREAT, isFirst);
        spEditor.apply();
    }

    public boolean getSessionOne() {
        return sharedPreferences.getBoolean(SESSION_ONE, false);
    }

    public void setSessionOne(boolean isFirst) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(SESSION_ONE, isFirst);
        spEditor.apply();
    }

    public boolean getSessionThree() {
        return sharedPreferences.getBoolean(SESSION_THREE, false);
    }

    public void setSessionThree(boolean isFirst) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(SESSION_THREE, isFirst);
        spEditor.commit();
    }


    public boolean isGreat() {
        return sharedPreferences.getBoolean(IS_GREAT, false);
    }

    public void setIsGreat(boolean isGreat) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(IS_GREAT, isGreat);
        spEditor.apply();
    }

    /*public int getOpenCountForRateApp() {
        return sharedPreferences.getInt(RATE_APP, 0);
    }

    public void setOpenCountForRateApp() {
        int count = getOpenCountForRateApp() + 1;

        if(count > Values.RATE_APP_DIVISOR) {
            count = 1;
        }

        spEditor = sharedPreferences.edit();
        spEditor.putInt(RATE_APP, count);
        spEditor.apply();
    }*/

}