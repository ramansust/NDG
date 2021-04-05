package com.nissan.alldriverguide.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.datasoft.downloadManager.epubUtils.ExtractedEpubLoader;
import com.nissan.alldriverguide.database.PreferenceUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Rohan on 9/18/2017.
 */
public abstract class SearchDBAsync extends AsyncTask<Void, Void, Boolean> {
    private final ArrayList<String> listOfFiles = new ArrayList<>();
    private final Activity activity;
    private final String langType;
    private final int carType;

//    protected SearchDBAsync(Activity act, String lang_type) {
//        this.activity = act;
//        this.langType = lang_type;
//        this.carType = Values.carType;
//        listFiles();
//    }

    /**
     * SearchDBAsync constructor
     *
     * @param act      getting activity
     * @param langType downloaded language sort name
     * @param carType  downloaded car ID
     */
    protected SearchDBAsync(Activity act, String langType, int carType) {
        this.activity = act;
        this.carType = carType;
        this.langType = langType;

        listFiles();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        // shorting the ArrayList
        Collections.sort(listOfFiles, new java.util.Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.charAt(0) - s2.charAt(0);
            }
        });

        // checking the ArrayList that contain epub form sdCard
        if (listOfFiles != null && listOfFiles.size() > 0) {
            // looping the ArrayList
            for (int i = 0; i < listOfFiles.size(); i++) {

                // get the full epub path from sdCard by looping
                String ePubPath = NissanApp.getInstance().getCarPath(carType) +
                        NissanApp.getInstance().getePubFolderPath(carType) +
                        Values.UNDERSCORE +
                        langType +
                        Values.SLASH + listOfFiles.get(i);

//                Logger.error("file_name", "_______" + listOfFiles.get(i));
//                Logger.error("Path_Epub", "" + ePubPath);

                // here MAePubParser takes epub source path eg. /storage/emulated/0/.AllDriverGuide/qashqai2017/qashqai2017_en/homepage_en.epub
                // this MAePubParser extract the epub to sdCard and destination folder define in library
                // parseePub() method in MAePubParser class actually parse the toc.ncx file and load in arrayList
                ArrayList<EpubInfo> listOfEpub = new ExtractedEpubLoader(ePubPath).parseNcxFile();

                if (listOfEpub.size() < 1)
                    return false; // if listOfEpub ArrayList doesn't have data and contain null value it's return false.

                // This key is used for storing ArrayList in shared preference
//                String sharedPreferenceKey = carType + Values.UNDERSCORE + langType + Values.UNDERSCORE + epubType[i];
                String sharedPreferenceKey = carType + Values.UNDERSCORE + langType + Values.UNDERSCORE + NissanApp.getInstance().getePubType(listOfFiles.get(i));
                // Storing ArrayList in shared preference
                new PreferenceUtil(activity.getApplicationContext()).storeSearchEpubList(listOfEpub, sharedPreferenceKey);
//                try {
//                    // After extracting and cashing epub data, downloaded epub has been force deleted.
////                    FileUtils.forceDelete(new File(ePubPath));
////                    Logger.error("Path_Epub", "_____" + "deleted!");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

        return true; // if listOfEpub ArrayList contain data it's return true.
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        // pass the return type of doInBackground in onComplete() abstract method
        onComplete(aBoolean);
    }

    /**
     * This method is override in AsyncTask execution class
     *
     * @param status parameter true for successfully epub data download and extract
     */
    public abstract void onComplete(boolean status);

    /**
     * This method for adding epub in ArrayList
     */
    private void listFiles() {
        // clear the ArrayList
        listOfFiles.clear();
        // crating language epub folder path (eg. /storage/emulated/0/.AllDriverGuide/micrak14/micrak14_pt).
        String path = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType;

        File directory = new File(path); // here select the language path
        File[] files = directory.listFiles(); // here get the 7 language epub list from sd card (that was downloaded). eg. button_pt.epub
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                // here add the epub name in arrayList
                listOfFiles.add(files[i].getName());
//                Logger.error("file_name", "_______" + files[i].getName());
            }
        }
    }
}
