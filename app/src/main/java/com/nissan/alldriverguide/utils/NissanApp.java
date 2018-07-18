package com.nissan.alldriverguide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.epub.XMLParser;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.BuildConfig;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.search.tab.WarningLightFragment;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.VideoInfo;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.ExploreTabVideoModel;
import com.nissan.alldriverguide.multiLang.model.GlobalMessage;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.multiLang.model.TabMenu;
import com.nissan.alldriverguide.multiLang.model.Tutorial;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by raman on 1/19/17.
 */

public class NissanApp {

    public static NissanApp instance;

    //    public String Vuforia_key = "AZeJ28P/////AAAAAcTlmrGvj0ngjCcwQjIavJcqQvIaBitAo5eBTkG2cG4PyF+t82bIUefZWp/9apWQuelfAA08QuTLCad92KWOLHBY+d1EzDYRAVelFk0LDUkFeW7TrxowR1fPpEu1Axlxm9NA6j9YMSqfxxH/378ei+xhkeUqLJeSTThdQVIivZ2WjlE6hIhhwZS0nqh/0mHH92gbfzMcgdrLQ1uM5u0jIRn9yjbVw4/CzCtQzjYpRyqWRz+vhrGMFL9xKBqyTwRy2PKrR2/6T6xYER4ZyCddIGJD5zgrLxPkPp3dbFGslnmTU3ZZYijn2kMIROhcIZYmx8F59odWmfNh4lRcV91pK554tGieRVsWzFqB/APdo3Av"; // free
    // this key is used for vuforia sdk for AR
    private String Vuforia_key = "ATZPXEj/////AAAAAK8J10rSI0dLr0sGOY7rQdUf20QxKpYGh6/JG14CkV1pjGLTF81OoLa/hx0u5+ZWxIO2Iufir6M2cEX2zz7GY3UGxAsJ6mmtoricZVrENjMjGhzYpIE6Rbuk62hcaRcrdy9dFM1CITHzbDPEE7cEcY99HHSSLHzsfLR9Zlo/WNzc5aTcOoWl+hewMmLfyZdwK/KN36FlvLy0j4hzM68fitZSgXvo+Ed1gCDDpSTT+yfF4r8h11emgrs68kLzUl4WWH/xwZbupVQj4cyGag2sC3vu2b2qSuyABHXfTViQjsIqUL8r3I7wF6Xkio8XN+a6b1Y3IArX4FJ9XlB3VR6QxCyFjUYbh96CSBEDA3Q8Q5wd"; // Paid key for NDG

    private ArrayList<VideoInfo> list;
    private Configuration conf;
    private ArrayList<TabMenu> tabMenuArrayList;
    private ArrayList<Tutorial> tutorialArrayList;
    private List<AlertMessage> alertMessageGlobalArrayList, alertMessageCarWiseLangDownloadList;
    private List<GlobalMessage> globalMessageArrayList;
    private List<LanguageList> carWiseLanguageList;

    public List<LanguageList> getCarWiseLanguageList() {
        return carWiseLanguageList;
    }

    public void setCarWiseLanguageList(List<LanguageList> carWiseLanguageList) {
        this.carWiseLanguageList = carWiseLanguageList;
    }

    public List<AlertMessage> getAlertMessageCarWiseLangDownloadList() {
        return alertMessageCarWiseLangDownloadList;
    }

    public void setAlertMessageCarWiseLangDownloadList(List<AlertMessage> alertMessageGlobalArrayList) {
        this.alertMessageGlobalArrayList = alertMessageCarWiseLangDownloadList;
    }

    public List<AlertMessage> getAlertMessageGlobalArrayList() {
        return alertMessageGlobalArrayList;
    }

    public void setAlertMessageGlobalArrayList(List<AlertMessage> alertMessageGlobalArrayList) {
        this.alertMessageGlobalArrayList = alertMessageGlobalArrayList;
    }

    public GlobalMessage getGlobalMessage(Context context) {

        if (globalMessageArrayList == null || globalMessageArrayList.size() == 0) {
            Type type = new TypeToken<ArrayList<GlobalMessage>>() {
            }.getType();
            globalMessageArrayList = new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(context).getSelectedLang()) + "_" + Values.GLOBAL_MSG_KEY), type);
            setGlobalMessageArrayList(globalMessageArrayList);
        }

        if (globalMessageArrayList == null || globalMessageArrayList.size() == 0)
            return new GlobalMessage();

        return globalMessageArrayList.get(0);
    }

    public void setGlobalMessageArrayList(List<GlobalMessage> globalMessageArrayList) {
        this.globalMessageArrayList = globalMessageArrayList;
    }

    public AssistanceInfo assistanceInfo;

    public AssistanceInfo getAssistanceInfo() {
        return assistanceInfo;
    }

    public void setAssistanceInfo(AssistanceInfo assistanceInfo) {
        this.assistanceInfo = assistanceInfo;
    }

    public ArrayList<Tutorial> getTutorialArrayList() {
        return tutorialArrayList;
    }

    public void setTutorialArrayList(ArrayList<Tutorial> tutorialArrayList) {
        this.tutorialArrayList = tutorialArrayList;
    }

    public ArrayList<TabMenu> getTabMenuArrayList() {
        return tabMenuArrayList;
    }

    public void setTabMenuArrayList(ArrayList<TabMenu> tabMenuArrayList) {
        this.tabMenuArrayList = tabMenuArrayList;
    }

    public ArrayList<Object> getCarList() {
        return carList;
    }

    public void setCarList(ArrayList<Object> carList) {
        this.carList = carList;
    }

    private ArrayList<Object> carList = new ArrayList<>();

    public ArrayList<CarInfo> getCarAllList() {
        return carAllList;
    }

    public void setCarAllList(ArrayList<CarInfo> carAllList) {
        this.carAllList = carAllList;
    }

    private ArrayList<ExploreTabVideoModel> exploreVideoList;

    public ArrayList<ExploreTabVideoModel> getExploreVideoList() {
        return exploreVideoList;
    }

    public void setExploreVideoList(ArrayList<ExploreTabVideoModel> exploreVideoList) {
        this.exploreVideoList = exploreVideoList;
    }

    private ArrayList<CarInfo> carAllList;

    public static NissanApp getInstance() {
        if (instance == null) {
            instance = new NissanApp();
        }
        return instance;
    }

    public void destroyInstance() {
        instance = null;
    }


    public int getWidth(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public int getHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /**
     * @param car car full name by this car id
     * @return Full name of car
     */
    public String getCarName(int car) {

        String cn = "";
        if (car == 1) {
            cn = "Qashqai EUR";
        } else if (car == 2) {
            cn = "Qashqai RUS";
        } else if (car == 3) {
            cn = "Juke";
        } else if (car == 4) {
            cn = "X-Trail EUR";
        } else if (car == 5) {
            cn = "X-Trail RUS";
        } else if (car == 6) {
            cn = "Pulsar";
        } else if (car == 7) {
            cn = "Micra";
        } else if (car == 8) {
            cn = "Note";
        } else if (car == 9) {
            cn = "Leaf";
        } else if (car == 10) {
            cn = "Navara";
        } else if (car == 11) {
            cn = "Micrak14";
        } else if (car == 12) {
            cn = "New Nissan Qashqai";
        } else if (car == 13) {
            cn = "New Nissan X-Trail";
        } else if (car == 14) {
            cn = "New Nissan LEAF";
        } else {
            cn = "";
        }

        return cn;
    }

    /**
     * @param language_name compare with language sort name
     * @return Language full name as a string
     */
    public String getLanguageName(String language_name) {
        String cn = "";


        if (carWiseLanguageList == null || carWiseLanguageList.size() == 0) {

            if (language_name.equalsIgnoreCase("en")) {
                cn = "English";
            } else if (language_name.equalsIgnoreCase("de")) {
                cn = "Deutsch";
            } else if (language_name.equalsIgnoreCase("fr")) {
                cn = "Français";
            } else if (language_name.equalsIgnoreCase("it")) {
                cn = "Italiano";
            } else if (language_name.equalsIgnoreCase("es")) {
                cn = "Español";
            } else if (language_name.equalsIgnoreCase("nl")) {
                cn = "Nederlands";
            } else if (language_name.equalsIgnoreCase("ru")) {
                cn = "Русский";
            } else if (language_name.equalsIgnoreCase("sv")) {
                cn = "Svenska";
            } else if (language_name.equalsIgnoreCase("no")) {
                cn = "Norsk";
            } else if (language_name.equalsIgnoreCase("pl")) {
                cn = "Polski";
            } else if (language_name.equalsIgnoreCase("fi")) {
                cn = "Suomi";
            } else if (language_name.equalsIgnoreCase("pt")) {
                cn = "Português";
            } else if (language_name.equalsIgnoreCase("DA")) {
                cn = "Dansk";
            } else {
                cn = "English";
            }
        } else {
            for (LanguageList langModel : carWiseLanguageList) {
                if (langModel.getLanguageShortcode().equalsIgnoreCase(language_name))
                    cn = langModel.getLanguageName();
            }
        }

        return cn;
    }

    /**
     * @return Vuforia key
     */
    public String getVuforia_key() {
        return Vuforia_key;
    }

    private void setLanguage(Activity activity, final String lang) {
        Configuration conf = activity.getResources().getConfiguration();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        Resources resources = new Resources(activity.getAssets(), metrics, conf);
    }

    public boolean isFileExists(String path) {
        if (new File(path).exists()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEpubExists(String ePubPath, String lang) {
        if (new File(ePubPath + "/" + Values.INFO).exists()
                && new File(ePubPath + "/" + Values.HOME_PAGE).exists()
                && new File(ePubPath + "/" + Values.BUTTON).exists()
                && new File(ePubPath + "/" + Values.COMBIMETER).exists()
                && new File(ePubPath + "/" + Values.TYRE).exists()
                && new File(ePubPath + "/" + Values.WARRANTY).exists()
                && new File(ePubPath + "/" + Values.ENGINE).exists()) {

            return true;
        } else {
            return false;
        }
    }

    /**
     * @param car  compare with car
     * @param lang compare with language sort name
     * @return Language URL for specific car as a string
     */
    public String getLanguageURL(int car, String lang) {
        String url = "";
        switch (car) {
            case 1:
                if (lang.contains("en")) {
                    url = UrlLinks.QASHQAI_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.QASHQAI_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.QASHQAI_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.QASHQAI_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.QASHQAI_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.QASHQAI_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.QASHQAI_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.QASHQAI_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.QASHQAI_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.QASHQAI_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.QASHQAI_PT;
                } else {

                }
                break;

            case 2:
                if (lang.contains("en")) {
                    url = UrlLinks.QASHQAI_RUS_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.QASHQAI_RUS_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.QASHQAI_RUS_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.QASHQAI_RUS_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.QASHQAI_RUS_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.QASHQAI_RUS_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.QASHQAI_RUS_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.QASHQAI_RUS_SV;
                } else {

                }
                break;

            case 3:
                if (lang.contains("en")) {
                    url = UrlLinks.JUKE_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.JUKE_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.JUKE_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.JUKE_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.JUKE_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.JUKE_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.JUKE_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.JUKE_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.JUKE_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.JUKE_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.JUKE_PT;
                } else {

                }
                break;

            case 4:
                if (lang.contains("en")) {
                    url = UrlLinks.XTRAIL_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.XTRAIL_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.XTRAIL_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.XTRAIL_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.XTRAIL_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.XTRAIL_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.XTRAIL_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.XTRAIL_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.XTRAIL_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.XTRAIL_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.XTRAIL_PT;
                } else {

                }
                break;

            case 5:
                if (lang.contains("en")) {
                    url = UrlLinks.XTRAIL_RUS_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.XTRAIL_RUS_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.XTRAIL_RUS_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.XTRAIL_RUS_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.XTRAIL_RUS_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.XTRAIL_RUS_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.XTRAIL_RUS_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.XTRAIL_RUS_SV;
                } else {

                }
                break;

            case 6:
                if (lang.contains("en")) {
                    url = UrlLinks.PULSAR_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.PULSAR_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.PULSAR_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.PULSAR_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.PULSAR_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.PULSAR_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.PULSAR_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.PULSAR_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.PULSAR_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.PULSAR_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.PULSAR_PT;
                } else {

                }
                break;

            case 7:
                if (lang.contains("en")) {
                    url = UrlLinks.MICRA_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.MICRA_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.MICRA_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.MICRA_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.MICRA_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.MICRA_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.MICRA_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.MICRA_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.MICRA_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.MICRA_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.MICRA_PT;
                } else {

                }
                break;

            case 8:
                if (lang.contains("en")) {
                    url = UrlLinks.NOTE_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.NOTE_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.NOTE_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.NOTE_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.NOTE_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.NOTE_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.NOTE_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.NOTE_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.NOTE_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.NOTE_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.NOTE_PT;
                } else {

                }
                break;

            case 9:
                if (lang.contains("en")) {
                    url = UrlLinks.LEAF_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.LEAF_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.LEAF_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.LEAF_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.LEAF_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.LEAF_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.LEAF_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.LEAF_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.LEAF_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.LEAF_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.LEAF_PT;
                } else {

                }
                break;

            case 10:
                if (lang.contains("en")) {
                    url = UrlLinks.NAVARA_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.NAVARA_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.NAVARA_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.NAVARA_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.NAVARA_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.NAVARA_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.NAVARA_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.NAVARA_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.NAVARA_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.NAVARA_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.NAVARA_PT;
                } else {

                }
                break;

            case 11:
                if (lang.contains("en")) {
                    url = UrlLinks.MICRA_NEW_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.MICRA_NEW_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.MICRA_NEW_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.MICRA_NEW_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.MICRA_NEW_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.MICRA_NEW_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.MICRA_NEW_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.MICRA_NEW_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.MICRA_NEW_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.MICRA_NEW_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.MICRA_NEW_PT;
                } else {

                }
                break;

            case 12:
                if (lang.contains("en")) {
                    url = UrlLinks.QASHQAI_2017_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.QASHQAI_2017_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.QASHQAI_2017_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.QASHQAI_2017_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.QASHQAI_2017_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.QASHQAI_2017_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.QASHQAI_2017_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.QASHQAI_2017_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.QASHQAI_2017_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.QASHQAI_2017_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.QASHQAI_2017_PT;
                } else {

                }
                break;

            case 13:
                if (lang.contains("en")) {
                    url = UrlLinks.XTRAIL_2017_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.XTRAIL_2017_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.XTRAIL_2017_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.XTRAIL_2017_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.XTRAIL_2017_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.XTRAIL_2017_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.XTRAIL_2017_RS;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.XTRAIL_2017_SV;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.XTRAIL_2017_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.XTRAIL_2017_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.XTRAIL_2017_PT;
                } else {

                }
                break;

            case 14:
                if (lang.contains("en")) {
                    url = UrlLinks.LEAF_2017_EN;
                } else if (lang.contains("de")) {
                    url = UrlLinks.LEAF_2017_DE;
                } else if (lang.contains("es")) {
                    url = UrlLinks.LEAF_2017_ES;
                } else if (lang.contains("fr")) {
                    url = UrlLinks.LEAF_2017_FR;
                } else if (lang.contains("it")) {
                    url = UrlLinks.LEAF_2017_IT;
                } else if (lang.contains("nl")) {
                    url = UrlLinks.LEAF_2017_NL;
                } else if (lang.contains("ru")) {
                    url = UrlLinks.LEAF_2017_RU;
                } else if (lang.contains("sv")) {
                    url = UrlLinks.LEAF_2017_SV;
                } else if (lang.contains("no")) {
                    url = UrlLinks.LEAF_2017_NO;
                } else if (lang.contains("pl")) {
                    url = UrlLinks.LEAF_2017_PL;
                } else if (lang.contains("fi")) {
                    url = UrlLinks.LEAF_2017_FI;
                } else if (lang.contains("pt")) {
                    url = UrlLinks.LEAF_2017_PT;
                } else {

                }
                break;

            default:
                break;
        }
        return url;
    }

    /**
     * @param path select car path location
     * @return true if car path is exist
     */
    public boolean createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * display internet alert when internet is not available
     *
     * @param activity getting activity
     * @param msg      display internet alert text
     */
    public void showInternetAlert(Activity activity, String msg) {
        final Dialog dialog = new DialogController(activity).internetDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * @param carType compare for specific car
     * @return car full path from sdCard as a string
     */
    public String getCarPath(int carType) {
        String carPath = "";
        switch (carType) {
            case 1:
                carPath = Values.PATH + Values.qashqai_folder;
                break;

            case 2:
                carPath = Values.PATH + Values.qashqai_folder_rus;
                break;

            case 3:
                carPath = Values.PATH + Values.juke_folder;
                break;

            case 4:
                carPath = Values.PATH + Values.xtrail_folder;
                break;

            case 5:
                carPath = Values.PATH + Values.xtrail_folder_rus;
                break;

            case 6:
                carPath = Values.PATH + Values.pulsar_folder;
                break;

            case 7:
                carPath = Values.PATH + Values.micra_folder;
                break;

            case 8:
                carPath = Values.PATH + Values.note_folder;
                break;

            case 9:
                carPath = Values.PATH + Values.leaf_folder;
                break;

            case 10:
                carPath = Values.PATH + Values.navara_folder;
                break;

            case 11:
                carPath = Values.PATH + Values.micra_new_folder;
                break;

            case 12:
                carPath = Values.PATH + Values.qashqai_2017;
                break;

            case 13:
                carPath = Values.PATH + Values.xtrail_2017;
                break;

            case 14:
                carPath = Values.PATH + Values.leaf_2017;
                break;

            default:
                break;
        }

        return carPath;
    }

    /**
     * @param carType for specific car epub
     * @return ePub folder ptah for specific car as a string
     */
    public String getePubFolderPath(int carType) {
        String path = "";
        switch (carType) {
            case 1:
                path = Values.qashqai_folder;
                break;

            case 2:
                path = Values.qashqai_folder_rus;
                break;

            case 3:
                path = Values.juke_folder;
                break;

            case 4:
                path = Values.xtrail_folder;
                break;

            case 5:
                path = Values.xtrail_folder_rus;
                break;

            case 6:
                path = Values.pulsar_folder;
                break;

            case 7:
                path = Values.micra_folder;
                break;

            case 8:
                path = Values.note_folder;
                break;

            case 9:
                path = Values.leaf_folder;
                break;

            case 10:
                path = Values.navara_folder;
                break;

            case 11:
                path = Values.micra_new_folder;
                break;

            case 12:
                path = Values.qashqai_2017;
                break;

            case 13:
                path = Values.xtrail_2017;
                break;

            case 14:
                path = Values.leaf_2017;
                break;

            default:
                break;
        }

        return path;
    }

    /**
     * for getting local car asset URL
     *
     * @param carType compare with specific car
     * @return specific car URL as a string
     */
    public String getAssetsURL(int carType) {
        String assetsPath = "";
        switch (carType) {
            case 1:
                assetsPath = UrlLinks.QASHQAI_LINK;
                break;

            case 2:
                assetsPath = UrlLinks.QASHQAI_LINK_RUS;
                break;

            case 3:
                assetsPath = UrlLinks.JUKE_LINK;
                break;

            case 4:
                assetsPath = UrlLinks.XTRAIL_LINK;
                break;

            case 5:
                assetsPath = UrlLinks.XTRAIL_LINK_RUS;
                break;

            case 6:
                assetsPath = UrlLinks.PULSAR_LINK;
                break;

            case 7:
                assetsPath = UrlLinks.MICRA_LINK;
                break;

            case 8:
                assetsPath = UrlLinks.NOTE_LINK;
                break;

            case 9:
                assetsPath = UrlLinks.LEAF_LINK;
                break;

            case 10:
                assetsPath = UrlLinks.NAVARA_LINK;
                break;

            case 11:
                assetsPath = UrlLinks.MICRA_NEW_LINK;
                break;

            case 12:
                assetsPath = UrlLinks.QASHQAI_2017_LINK;
                break;

            case 13:
                assetsPath = UrlLinks.XTRAIL_2017_LINK;
                break;

            case 14:
                assetsPath = UrlLinks.LEAF_2017_LINK;
                break;

            default:
                break;
        }

        return assetsPath;
    }

    public ArrayList<VideoInfo> getList() {
        return list;
    }

    public void setList(ArrayList<VideoInfo> list) {
        this.list = list;
    }

    /**
     * @param activity
     * @param lang     for localized language in Android configuration
     * @return Configuration by getting selected language
     */
    public Configuration changeLocalLanguage(Activity activity, String lang) {

        conf = activity.getResources().getConfiguration();
        if (!lang.equals("")) {

            if (lang.contentEquals("en")) {
                conf.locale = new Locale("en");
            } else if (lang.contentEquals("de")) {
                conf.locale = new Locale("de");
            } else if (lang.contentEquals("es")) {
                conf.locale = new Locale("es");
            } else if (lang.contentEquals("fr")) {
                conf.locale = new Locale("fr");
            } else if (lang.contentEquals("it")) {
                conf.locale = new Locale("it");
            } else if (lang.contentEquals("nl")) {
                conf.locale = new Locale("nl");
            } else if (lang.contentEquals("ru")) {
                conf.locale = new Locale("ru");
            } else if (lang.contentEquals("sv")) {
                conf.locale = new Locale("sv");
            } else if (lang.contentEquals("no")) {
                conf.locale = new Locale("no");
            } else if (lang.contentEquals("pl")) {
                conf.locale = new Locale("pl");
            } else if (lang.contentEquals("fi")) {
                conf.locale = new Locale("fi");
            } else if (lang.contentEquals("pt")) {
                conf.locale = new Locale("pt");
            } else {
                conf.locale = new Locale(lang);
            }
        }
//        conf.locale = new Locale(lang);
        return conf;
    }

    /**
     * @return formatted date time as a string
     */
    public String getDateTime() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

/*    */

    /**
     * @param position  indicate the car id
     * @param imageView set image by car id which display in list
     *//*
    public void setCarImage(int position, ImageView imageView) {
        switch (position) {
            case 1:
            case 2:
                imageView.setBackgroundResource(R.drawable.qashqai_bnw);
                break;

            case 3:
                imageView.setBackgroundResource(R.drawable.juke);
                break;

            case 4:
            case 5:
                imageView.setBackgroundResource(R.drawable.xtrail_bnw);
                break;

            case 6:
                imageView.setBackgroundResource(R.drawable.pulsar);
                break;

            case 7:
                imageView.setBackgroundResource(R.drawable.micra_bnw);
                break;

            case 8:
                imageView.setBackgroundResource(R.drawable.note);
                break;

            case 9:
                imageView.setBackgroundResource(R.drawable.leaf_bnw);
                break;

            case 10:
                imageView.setBackgroundResource(R.drawable.navara);
                break;

            case 11:
                imageView.setBackgroundResource(R.drawable.micra_k14);
                break;

            case 12:
                imageView.setBackgroundResource(R.drawable.qashqai_2017_download);
                break;

            case 13:
                imageView.setBackgroundResource(R.drawable.xtrail_2017_download);
                break;

            case 14:
                imageView.setBackgroundResource(R.drawable.leaf_2017_download);
                break;

            default:
                break;
        }
    }*/

/*    public void setPreviousCarImage(int position, ImageView imageView) {
        switch (position) {
            case 1:
            case 2:
                imageView.setBackgroundResource(R.drawable.qashqai_bnw);
                break;

            case 4:
            case 5:
                imageView.setBackgroundResource(R.drawable.xtrail_bnw);
                break;

            case 7:
                imageView.setBackgroundResource(R.drawable.micra_bnw);
                break;

            case 9:
                imageView.setBackgroundResource(R.drawable.leaf_bnw);
                break;

            default:
                break;
        }
    }*/

/*    public void setCarImageAssistance(int position, ImageView imageView) {
        switch (position) {
            case 1:
            case 2:
                imageView.setBackgroundResource(R.drawable.qashqai);
                break;

            case 3:
                imageView.setBackgroundResource(R.drawable.juke);
                break;

            case 4:
            case 5:
                imageView.setBackgroundResource(R.drawable.xtrail);
                break;

            case 6:
                imageView.setBackgroundResource(R.drawable.pulsar);
                break;

            case 7:
                imageView.setBackgroundResource(R.drawable.micra);
                break;

            case 8:
                imageView.setBackgroundResource(R.drawable.note);
                break;

            case 9:
                imageView.setBackgroundResource(R.drawable.leaf);
                break;

            case 10:
                imageView.setBackgroundResource(R.drawable.navara);
                break;

            case 11:
                imageView.setBackgroundResource(R.drawable.micra_k14);
                break;

            case 12:
                imageView.setBackgroundResource(R.drawable.qashqai_2017_assistance);
                break;

            case 13:
                imageView.setBackgroundResource(R.drawable.xtrail_2017_assistance);
                break;

            case 14 :
                imageView.setBackgroundResource(R.drawable.leaf_2017_assistance);
                break;

            default:
                break;
        }
    }*/
    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * @param context
     * @return device id as a string
     */
    @SuppressLint("HardwareIds")
    public String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * @return device model as a string
     */
    public String getDeviceModel() {
        String deviceName = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;
        int deviceAPI = Build.VERSION.SDK_INT;
        return deviceName + " (" + deviceModel + ") - " + deviceAPI;
    }

    public void replaceFragment(FragmentTransaction transaction, int containerId, Fragment fragment, String addToBackStack) {
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(addToBackStack);
        transaction.commit();
    }

    /*
     * This method actually check the epub type null
     * or not null that retrieve form database
     */
    public boolean insertSearchDataIntoDatabase(Context mContext, String keyWord) {

        ArrayList<EpubInfo> searchResult;
        searchResult = new PreferenceUtil(mContext).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(mContext).getSelectedLang() + Values.UNDERSCORE + Values.COMBIMETER_TYPE);
        searchResult = WarningLightFragment.searchForTag(searchResult, keyWord);
        if (searchResult != null && searchResult.size() > 0)
            return true;

        searchResult = WarningLightFragment.searchForTag(new PreferenceUtil(mContext).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(mContext).getSelectedLang() + Values.UNDERSCORE + Values.HOMEPAGE_TYPE), keyWord);
        if (searchResult != null && searchResult.size() > 0)
            return true;

        searchResult = WarningLightFragment.searchForTag(new PreferenceUtil(mContext).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(mContext).getSelectedLang() + Values.UNDERSCORE + Values.TYRE_TYPE), keyWord);
        if (searchResult != null && searchResult.size() > 0)
            return true;

        searchResult = WarningLightFragment.searchForTag(new PreferenceUtil(mContext).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(mContext).getSelectedLang() + Values.UNDERSCORE + Values.ENGINE_TYPE), keyWord);
        if (searchResult != null && searchResult.size() > 0)
            return true;

        searchResult = WarningLightFragment.searchForTag(new PreferenceUtil(mContext).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(mContext).getSelectedLang() + Values.UNDERSCORE + Values.WARRANTY_TYPE), keyWord);
        if (searchResult != null && searchResult.size() > 0)
            return true;

        return false;

    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (in != null)
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public ArrayList<EpubInfo> parseePub(String dest) {

        Logger.error("destination", "___________" + dest);

        ArrayList list = new ArrayList();
        XMLParser parser = new XMLParser();
        String xml = this.getFileContent(dest + Values.TOC_DIRECTORY);
        Document doc = parser.getDomElement(xml);
        NodeList nl = doc.getElementsByTagName("navPoint");

        for (int i = 0; i < nl.getLength(); ++i) {
            EpubInfo info = new EpubInfo();
            Element element = (Element) nl.item(i);
            info.setIndex(i);
            info.setHtmlLink(parser.getAttributeValue(element, "content", "src"));
//            info.setTitle(parser.getValue(element, "text"));
//            info.setSearchTag(parser.getValue(element, "search"));

            info.setTitle(StringHelper.convertFromUTF8(parser.getValue(element, "text")));
            info.setSearchTag(StringHelper.convertFromUTF8(parser.getValue(element, "search")));

            list.add(info);
        }

        return list;
    }

    private String getFileContent(String targetFilePath) {
        FileInputStream fileInputStream = null;
        File file = new File(targetFilePath);

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
        }

        StringBuilder sb = null;

        try {
            for (; fileInputStream.available() > 0; sb.append((char) fileInputStream.read())) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
            }
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        String fileContent = "";
        if (sb != null) {
            fileContent = sb.toString();
        }

        try {
            fileInputStream.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return fileContent;
    }

    public int getLanguageID(String language_name) {
        int langID = 0;

        if (carWiseLanguageList == null || carWiseLanguageList.size() == 0) {

            if (language_name.equalsIgnoreCase("en")) {
                langID = 1;
            } else if (language_name.equalsIgnoreCase("de")) {
                langID = 2;
            } else if (language_name.equalsIgnoreCase("fr")) {
                langID = 3;
            } else if (language_name.equalsIgnoreCase("it")) {
                langID = 4;
            } else if (language_name.equalsIgnoreCase("es")) {
                langID = 5;
            } else if (language_name.equalsIgnoreCase("nl")) {
                langID = 6;
            } else if (language_name.equalsIgnoreCase("ru")) {
                langID = 7;
            } else if (language_name.equalsIgnoreCase("sv")) {
                langID = 8;
            } else if (language_name.equalsIgnoreCase("no")) {
                langID = 9;
            } else if (language_name.equalsIgnoreCase("pl")) {
                langID = 10;
            } else if (language_name.equalsIgnoreCase("fi")) {
                langID = 11;
            } else if (language_name.equalsIgnoreCase("pt")) {
                langID = 12;
            } else if (language_name.equalsIgnoreCase("DA")) {
                langID = 21;
            } else {
                langID = 1;
            }

        } else {

            for (LanguageList languageModel : carWiseLanguageList) {
                if (languageModel.getLanguageShortcode().equalsIgnoreCase(language_name)) {
                    langID = languageModel.getLanguageId();
                }
            }
        }
        return langID;
    }

    public int getEpubId(String name) {
        if (name.contains("combimeter"))
            return Values.COMBIMETER_TYPE;
        else if (name.contains("homepage"))
            return Values.HOMEPAGE_TYPE;
        else if (name.contains("tyre"))
            return Values.TYRE_TYPE;
        else if (name.contains("engine"))
            return Values.ENGINE_TYPE;
        else if (name.contains("warranty"))
            return Values.WARRANTY_TYPE;
        else if (name.contains("button"))
            return Values.BUTTON_TYPE;
        else if (name.contains("info"))
            return Values.INFO_TYPE;
        else if (name.contains("assistance"))
            return Values.WARRANTY_TYPE;

        return 0;
    }

    public String getDensityName(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "xxxhdpi";
        }
        if (density >= 3.0) {
            return "xxhdpi";
        }
        if (density >= 2.0) {
            return "xhdpi";
        }
        if (density >= 1.5) {
            return "hdpi";
        }
        if (density >= 1.0) {
            return "mdpi";
        }
        return "ldpi";
    }

    public String getAlertMessage(Context context, String lang_short_name, String msg_type) {

        String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang_short_name) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

        List<AlertMessage> alertMessageArrayList = NissanApp.getInstance().getAlertMessageGlobalArrayList();
        if (alertMessageArrayList == null || alertMessageArrayList.size() == 0) {
            Type type = new TypeToken<ArrayList<AlertMessage>>() {
            }.getType();
            alertMessageArrayList = new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(key_global_alert_message), type);
            if (alertMessageArrayList == null || alertMessageArrayList.size() == 0)
                return "";
            NissanApp.getInstance().setAlertMessageGlobalArrayList(alertMessageArrayList);
        }

        int searchIndex = alertMessageArrayList.indexOf(new AlertMessage(msg_type));

        if (searchIndex >= 0)
            return alertMessageArrayList.get(searchIndex).getMsg();

        return "";

    }

    public String getTabTitle(Context context, String index) {

        ArrayList<TabMenu> tabMenuList = getTabMenuArrayList();

        if (tabMenuList == null || tabMenuList.size() == 0) {
            tabMenuList = getDataFromStorage(context);
        }

        if (tabMenuList == null || tabMenuList.size() == 0)
            return "";

        for (TabMenu tabMenu : tabMenuList) {
            if (tabMenu.getIndex().equals(index))
                return tabMenu.getTitle();
        }

        return "";
    }

    private ArrayList<TabMenu> getDataFromStorage(Context context) {

        String key = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(context).getSelectedLang()) + "_" + Values.TAB_MENU_KEY;

        Type type = new TypeToken<ArrayList<TabMenu>>() {
        }.getType();
        return new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(key), type);

    }

    public String getURLAccordingToDensity(Context context, String device_density, CarList model) {

        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);

        if (large || xlarge) {
            if (large) {
                return model.getImgLargeMdpi();
            } else {
                return model.getImgXLargeMdpi();
            }
        } else {
            if (device_density.equalsIgnoreCase("xxxhdpi")) {
                return model.getImgXXXhdpi();
            } else if (device_density.equalsIgnoreCase("xxhdpi")) {
                return model.getImgXXhdpi();
            } else if (device_density.equalsIgnoreCase("xhdpi")) {
                return model.getImgXhdpi();
            } else if (device_density.equalsIgnoreCase("hdpi")) {
                return model.getImgHdpi();
            } else if (device_density.equalsIgnoreCase("ldpi")) {
                return model.getImgLdpi();
            } else {
                return model.getImgHdpi();
            }
        }


    }

    public int getePubType(String ePubName) {
        if (ePubName.contains("combimeter")) {
            return 1;
        } else if (ePubName.contains("homepage")) {
            return 2;
        } else if (ePubName.contains("tyre")) {
            return 3;
        } else if (ePubName.contains("engine")) {
            return 4;
        } else if (ePubName.contains("warranty")) {
            return 5;
        } else if (ePubName.contains("button")) {
            return 6;
        } else if (ePubName.contains("info")) {
            return 7;
        }

        return 0;
    }

}
