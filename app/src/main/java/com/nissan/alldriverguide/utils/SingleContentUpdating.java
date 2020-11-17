package com.nissan.alldriverguide.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.mobioapp.infinitipacket.epub.MAePubParser;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.database.PreferenceUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shubha on 11/27/17.
 */

public abstract class SingleContentUpdating extends AsyncTask<Void, Void, Boolean> {

    private Context activity;
    private ArrayList<String> folderNameUpdatedFolderList = new ArrayList<>();
    private ArrayList<String> folderNameUpdatedFolderListAfterExtraction = new ArrayList<>();
    private ArrayList<String> listOfFiles = new ArrayList<>();
    private String langType = "", combimeterButton = "combimeter_button";
    private int carType = 0;

    public SingleContentUpdating(Activity activity, String lang, int carType) {
        this.activity = activity;
        this.langType = lang;
        this.carType = carType;
    }

    private boolean deleteUpdatedFolder() {
        String folderToDelete = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME;
        File folderConvertedToFile = new File(folderToDelete);

        if (folderConvertedToFile.exists()) {
            try {
                FileUtils.deleteDirectory(folderConvertedToFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        } else {
            return false;
        }

    }

    private void parseAndUpdateSharedPreference() {
        listFiles();

        if (folderNameUpdatedFolderList != null && folderNameUpdatedFolderList.size() > 0) {
            for (int i = 0; i < folderNameUpdatedFolderList.size(); i++) {
                ArrayList<EpubInfo> listOfePub = new MAePubParser(NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME + Values.SLASH + folderNameUpdatedFolderList.get(i)).parseePub();

                if (listOfePub == null || listOfePub.size() < 1)
                    return;

                if (folderNameUpdatedFolderList.get(i).endsWith(".epub")) {
                    String sharedPreferenceKey = carType + Values.UNDERSCORE + langType + Values.UNDERSCORE + NissanApp.getInstance().getEpubId(folderNameUpdatedFolderList.get(i));
                    new PreferenceUtil(activity.getApplicationContext()).storeSearchEpubList(listOfePub, sharedPreferenceKey);
                }
            }
        }

    }


    private void listFiles() {
        listOfFiles.clear();
        String path = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType;

        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                listOfFiles.add(files[i].getName());
            }
        }
    }

    private void getListOfUpdatedContent() {
        String extractedUpdatedContentDir = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME;
        File dirUpdatedContent = new File(extractedUpdatedContentDir);

        if (dirUpdatedContent.isDirectory()) {
            for (File file : dirUpdatedContent.listFiles()) {
                if (file.getName().endsWith(".epub")) {
                    folderNameUpdatedFolderList.add(file.getName());
                }
            }
        }


    }

    private boolean copyDirectory() {
        String srcPath = "", destPath = "";

        if (folderNameUpdatedFolderListAfterExtraction != null && folderNameUpdatedFolderListAfterExtraction.size() > 0) {
            for (int i = 0; i < folderNameUpdatedFolderListAfterExtraction.size(); i++) {
                try {
                    String ePubTypeFolder = folderNameUpdatedFolderListAfterExtraction.get(i);

                    if (!combimeterButton.equalsIgnoreCase(ePubTypeFolder)) {
                        srcPath = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME + Values.SLASH + ePubTypeFolder + "/OEBPS";
                        destPath = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.SLASH + ePubTypeFolder + "/OEBPS";
                    } else {
                        srcPath = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME + Values.SLASH + combimeterButton;
                        destPath = NissanApp.getInstance().getCarPath(carType) + Values.SLASH + ePubTypeFolder;
                    }

                    File srcFile = new File(srcPath);
                    File destFile = new File(destPath);

                    Logger.error("Src___" + srcFile.getAbsolutePath(), "Des_____" + destPath);

                    if (!srcFile.exists() || !destFile.exists())
                        return false;

                    FileUtils.copyDirectory(srcFile, destFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else {
            return false;
        }

    }

    private void getUpdatedFolderListToCopy() {

        String extractedUpdatedContentDir = NissanApp.getInstance().getCarPath(carType) + NissanApp.getInstance().getePubFolderPath(carType) + Values.UNDERSCORE + langType + Values.UNDERSCORE + Values.CONTENT_FOLDER_NAME;
        File dirUpdatedContent = new File(extractedUpdatedContentDir);

        if (dirUpdatedContent.isDirectory()) {
            for (File file : dirUpdatedContent.listFiles()) {
                if (file.getName().startsWith(".") || file.getName().equalsIgnoreCase(combimeterButton)) {
                    folderNameUpdatedFolderListAfterExtraction.add(file.getName());
                }
            }
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        getListOfUpdatedContent();
        parseAndUpdateSharedPreference();

        getUpdatedFolderListToCopy();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (folderNameUpdatedFolderListAfterExtraction != null && folderNameUpdatedFolderListAfterExtraction.size() > 0) {
            if (copyDirectory()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        deleteUpdatedFolder();
        onComplete(aBoolean);
    }

    public abstract void onComplete(boolean status);
}
