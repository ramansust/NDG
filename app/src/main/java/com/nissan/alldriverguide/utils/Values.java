package com.nissan.alldriverguide.utils;

import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by raman on 1/19/17.
 */

public class Values {
    public static final String PATH = Environment.getExternalStorageDirectory() + "/.AllDriverGuide";
    public static String car_path = "";
    public static int carType = 0;
    public static int ePubType = 0;

    //Folder Name of cars in sdcard
    public static final String qashqai_folder = "/qashqai";
    public static final String qashqai_folder_rus = "/qashqai_rus";
    public static final String juke_folder = "/juke";
    public static final String xtrail_folder = "/xtrail";
    public static final String xtrail_folder_rus = "/xtrail_rus";
    public static final String pulsar_folder = "/pulsar";
    public static final String micra_folder = "/micra";
    public static final String note_folder = "/note";
    public static final String leaf_folder = "/leaf";
    public static final String navara_folder = "/navara";
    public static final String micra_new_folder = "/micrak14";
    public static final String qashqai_2017 = "/qashqai2017";
    public static final String xtrail_2017 = "/xtrail2017";
    public static final String leaf_2017 = "/leaf2017";

    public static String info = "/info_";
    public static String homepage = "/homepage_";
    public static String homepage2 = "homepage";
    public static String button = "/button_";
    public static String combimeter = "/combimeter_";
    public static String tyre = "/tyre_";
    public static String warranty = "/warranty_";
    public static String engine = "/engine_";

    public static String epub = ".epub";

    public static String UNDERSCORE = "_";
    public static String SLASH = "/";

    public static String ASSETS = "/assets/";

    public static final String HOME_PAGE = "/.ar_homepage";
    public static final String BUTTON = "/.ar_button";
    public static final String INFO = "/.ar_info";
    public static final String COMBIMETER = "/.ar_combimeter";
    public static final String TYRE = "/.ar_tyre";
    public static final String ENGINE = "/.ar_engine";
    public static final String WARRANTY = "/.ar_warranty";


    public static final int COMBIMETER_TYPE = 1; // warning lights
    public static final int HOMEPAGE_TYPE = 2;   // quick reference guide (QRG)
    public static final int TYRE_TYPE = 3;       // tyre information
    public static final int ENGINE_TYPE = 4;     // engine compartment
    public static final int WARRANTY_TYPE = 5;   // warranty
    public static final int BUTTON_TYPE = 6;     // when start the AR and detect the car
    public static final int INFO_TYPE = 7;       // display in middle button for AR camera
    public static final int ASSISTANCE_TYPE = 8; // nissan assistance (this epub getting from last index of warranty epub)


    public static final int RED_TYPE = 1;
    public static final int YELLOW_TYPE = 2;
    public static final int GREEN_TYPE = 3;
    public static final int BLUE_TYPE = 4;
    public static final int GRAY_TYPE = 5;
    public static final int CYAN_TYPE = 6;
    public static final int ORANGE_TYPE = 7;

    public static String ar_value = "";
    // Tyre Transition Info
    public static int gif_index = 0;
    public static ArrayList<String> gif_names = new ArrayList<String> ();
    public static int gif_count = 0;
    public static String tyre_gif_folder = "";
    public static int i = 0;

    public static int videoIndex = 0;

    public static String tabExplore = "Explore";
    public static String tabAssistance = "Assistance";
    public static String tabSearch = "Search";
    public static String tabSettings = "Settings";
    public static String tabSearchChildFragment = "tabSearchFragment";

    public static String keyWord = "";

    public static final String TOC_DIRECTORY = "/OEBPS/toc.ncx";

    public static final String SUCCESS_STATUS = "200";
    public static final String FAILED_STATUS = "400";

    public static final String DEVICE_TYPE = "1";

    public static final String CONTENT_FOLDER_NAME = "updated";

    public static final String AVAILABLE_FOR_DOWNLOAD = "0";
    public static final String ALREADY_DOWNLOADED = "1";
    public static final String PREVIOUS_CAR = "2";
    public static final int CAR_SELECTED = 1;
    public static final int CAR_NOT_SELECTED = 0;

    public static final int RATE_APP_DIVISOR = 30;

    public static final int RATE_APP_SESSION = 0;
    public static final int RATE_APP_FIRST_SESSION = 30;
    public static final int RATE_APP_SECOND_SESSION = 15;

    public static final String TUTORIAL = "multi_lang_tutorial";
    public static final String TAB_MENU = "multi_lang_tab_menu";
    public static final String CAR_LANGUAGE_LIST= "multi_lang_list";
    public static final String TUTORIAL_KEY = "multi_lang_tutorial";
    public static final String TAB_MENU_KEY = "multi_lang_tab_menu";

    public static final String GLOBALMSGKEY = "globalmsg";
    public static final String EXPLOREDATA = "multi_lang_explore";
    public static final String SETTINGDATA = "multi_lang_setting";
    public static final String EPUBID = "0";
    public static final String GLOBAL_MSG_KEY = "multi_lang_globalmsg";
    public static final String GLOBAL_ALERT_MSG_KEY = "multi_lang_global_alert_msg";
    public static final String CAR_WISE_LANG_DOWNLOAD_ALERT_MSG = "multi_lang_car_wise_dl_list_alert_msg";

    public static final String ALERT_MSG_TYPE_INTERNET = "internet_check";
    public static final String ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_1 = "download_car_guide1";
    public static final String ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2 = "download_car_guide2";

    public static final String ASSISTANCE_OBJ_STORE_KEY = "assistance";
    public static final String  DELETE_MESSAGE = "delete_message";
    public static final String  REGISTER_PUSH_MESSAGE = "register_push";
    public static final String  ALREADY_READ_IT = "already_read";
    public static final String  READ = "read";
    public static final String  CHANGING_FLAT_TYRE = "please_read";
    public static final String  USING_TYRE_PUNCTURE = "please_read_tyre_puncture";
    public static final String  CONFIRM_EXIT_MESSAGE = "confirm_exit";
    public static final String  LEARN_MORE_TITLE = "learn_more_title";
    public static final String  WATCH_AGAIN_MSG = "watch_again";
    public static final String  LEARN_MORE_MSG = "learn_more";
    public static final String SEARCH_HINT = "search";
    public static final String RECENT_SEARCH = "recent";
    public static final String CLEAR = "clear";
    public static final String  INTERNET_CHECK = "internet_check";
    public static final String  DOWNLOADING = "downloading";
    public static final String  STARTING_DOWNLOAD = "starting_download";
    public static final String  DATA_SYNCING = "data_syncing";
    public static final String  DOWNLOAD_CONFIRMATION = "dowload_confirmation";

    public static final String  RATE_OUR_APP_YES = "rate_our_app_yes";
    public static final String  RATE_ASK_ME_LATER = "rate_ask_me_later";
    public static final String  RATE_NO_THANKS = "rate_no_thanks";
    public static final String  RATE_OUR_APP_SUBTITLE = "rate_our_app_sub_title";

    public static final String  FEEDBACK_TITLE = "feedback";
    public static final String  TITLE_FIELD = "send_feedback_title";
    public static final String  DESCRIPTION_FIELD = "send_feedback_description";
    public static final String  SEND_FEEDBACK_BUTTON_TEXT = "send_feedback_button";
    public static final String  SEND_FEEDBACK_COMPLETE_TOAST = "feedback_toast";

    public static final String  NEXT = "next";

    public static final String  NATIONAL_MSG = "national";
    public static final String  INTERNATIONAL_MSG = "international";
    public static final String  DOWNLOAD_SURE_MSG = "sure_download";
}
