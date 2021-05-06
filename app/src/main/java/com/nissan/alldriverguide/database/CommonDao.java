package com.nissan.alldriverguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.EpubModel;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.SearchModel;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Raman
 * @version 1.0
 */

public class CommonDao {

    private static final CommonDao instance = new CommonDao();
    long newHeadId;
    int count;

    public CommonDao() {

    }

    public static CommonDao getInstance() {
        return instance;
    }


    //db.query(true, P_TABLE, coloane,KEY_P_CAT_LIST + " LIKE ?" + " AND " +CID + " LIKE ?", new String[] {"%"+CID+"%", "%"+input+"%"}, null, null, null, null);
    public ArrayList<EpubModel> getEpubListByTag(Context context, String searchTag, int cartype) {
        ArrayList<EpubModel> listAllEpub = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        Cursor mCursor = db
                .query(true,
                        EpubInfoTableDirectory.TABLE_NAME,
                        new String[]{EpubInfoTableDirectory._ID, EpubInfoTableDirectory.TITLE, EpubInfoTableDirectory.TAG, EpubInfoTableDirectory.LINK, EpubInfoTableDirectory.CARTYPE, EpubInfoTableDirectory.EPUBTYPE, EpubInfoTableDirectory.INDEX},
                        EpubInfoTableDirectory.TITLE + " LIKE ? " + " AND " + EpubInfoTableDirectory.CARTYPE + " LIKE ? ",
                        new String[]{"%" + searchTag + "%", "%" + cartype + "%"},
                        null,
                        null,
                        EpubInfoTableDirectory.EPUBTYPE + " ASC",
                        null);
        if (mCursor.moveToFirst()) {
            for (int i = 0; i < mCursor.getCount(); i++) {
                EpubModel epubModule = getListFromCursor(mCursor);
                listAllEpub.add(epubModule);
                mCursor.moveToNext();
            }
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        return listAllEpub;
    }

    /**
     * Getting section wise all data in a ArrayList
     * status = 0 (For Available for download)
     * status = 1 (For Downloaded Car)
     * status = 2 (For Previous model)
     *
     * @param context need activity context for get db read/write permission
     * @return All car information in an ArrayList that display in landing page
     * of section wise like: Available for download, Downloaded Car and Previous model
     */
    public ArrayList<Object> getAllOderCarList(Context context) {
        int downloadCar = 0;
        ArrayList<Object> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);

        Cursor cursor = db.rawQuery("SELECT * FROM car_info WHERE region = 'EUR' AND status = '1' ORDER BY list_index ASC;", null);
        Cursor cursor2 = db.rawQuery("SELECT * FROM car_info WHERE region = 'EUR' AND status = '0' ORDER BY list_index ASC;", null);
        Cursor cursor3 = db.rawQuery("SELECT * FROM car_info WHERE region = 'EUR' AND status = '2' ORDER BY list_index ASC;", null);


        try {
            if (cursor.moveToFirst()) {
                do {

                    CarInfo info = new CarInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 0, cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
                    if (info.getId() >= 11) { // this condition is display for latest 4 cars (11,12,13,14) top in CarDownloadActivity.java
                        list.add(0, info);
                    } else {
                        list.add(info);
                    }
                    downloadCar++;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        try {
            if (cursor2.moveToFirst()) {
                do {
                    CarInfo info = new CarInfo(cursor2.getInt(0), cursor2.getString(1), cursor2.getString(2), cursor2.getString(3), cursor2.getString(4), cursor2.getString(5), 0, cursor2.getString(6), cursor2.getInt(7), cursor2.getInt(8));
                    if (info.getId() >= 11) { // this condition is display for latest 4 cars (11,12,13,14) top in CarDownloadActivity.java
                        list.add(downloadCar, info);
                    } else {
                        list.add(info);
                    }
                } while (cursor2.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor2 != null) {
                cursor2.close();
            }
        }

        try {
            if (cursor3.moveToFirst()) {
                do {
                    CarInfo info = new CarInfo(cursor3.getInt(0), cursor3.getString(1), cursor3.getString(2), cursor3.getString(3), cursor3.getString(4), cursor3.getString(5), 0, cursor3.getString(6), cursor3.getInt(7), cursor3.getInt(8));
                    if (info.getId() >= 11) { // this condition is display for latest 4 cars (11,12,13,14) top in CarDownloadActivity.java
                        list.add(downloadCar, info);
                    } else {
                        list.add(info);
                    }
                } while (cursor3.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor3 != null) {
                cursor3.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        //for ordering old juke to the last position
        int indexOfOldJuke = -1;
        for (Object info : list) {
            if (info instanceof CarInfo) {
                if (((CarInfo) info).getId() == 3 && Integer.valueOf(((CarInfo) info).getStatus()) != 1) {
                    indexOfOldJuke = list.indexOf(info);
                }
            }
        }
        if (indexOfOldJuke != -1) {
            Object juke = list.remove(indexOfOldJuke);
            list.add(juke);
        }

        return list;
    }

    public ArrayList<EpubModel> getAllEpubList(Context context) {

        ArrayList<EpubModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        Cursor cursor = db.rawQuery("SELECT * FROM epub_info;", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    EpubModel info = new EpubModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
                    list.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return list;
    }


    public ArrayList<EpubModel> getAllEpubListAccordingtoType(Context context, int cartype) {

        ArrayList<EpubModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT  * FROM " + EpubInfoTableDirectory.TABLE_NAME + " WHERE " + EpubInfoTableDirectory.CARTYPE + "= " + cartype;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    EpubModel info = new EpubModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
                    list.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return list;
    }


    public void deleteSingleCarEpub(Context context, int carId) {
        SQLiteDatabase libraryListDb = getWritableDatabase(context);
        String whereClause = EpubInfoTableDirectory.CARTYPE + " = ?";
        String[] whereArgs = {String.valueOf(carId)};
        libraryListDb.delete(EpubInfoTableDirectory.TABLE_NAME, whereClause, whereArgs);
    }

    private EpubModel getListFromCursor(Cursor aCursor) {
        EpubModel epubModel = new EpubModel();
        epubModel.setId(aCursor.getInt(aCursor.getColumnIndex(EpubInfoTableDirectory._ID)));
        epubModel.setTitle(aCursor.getString(aCursor.getColumnIndex(EpubInfoTableDirectory.TITLE)));
        epubModel.setTag(aCursor.getString(aCursor.getColumnIndex(EpubInfoTableDirectory.TAG)));
        epubModel.setLink(aCursor.getString(aCursor.getColumnIndex(EpubInfoTableDirectory.LINK)));
        epubModel.setCarType(aCursor.getInt(aCursor.getColumnIndex(EpubInfoTableDirectory.CARTYPE)));
        epubModel.setEpubType(aCursor.getInt(aCursor.getColumnIndex(EpubInfoTableDirectory.EPUBTYPE)));
        epubModel.setIndex(aCursor.getInt(aCursor.getColumnIndex(EpubInfoTableDirectory.INDEX)));
        return epubModel;
    }

    public ArrayList<CarInfo> getAllCarList(Context context) {

        ArrayList<CarInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        Cursor cursor = db.rawQuery("SELECT * FROM car_info WHERE region = 'EUR';", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    CarInfo info = new CarInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 0, cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
                    if (info.getId() >= 11) {
                        list.add(0, info);
                    } else {
                        list.add(info);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return list;
    }

    public CarInfo getCarInfo(Context context, int carID) {
        CarInfo info = null;

        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM car_info WHERE _id = " + carID;
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    info = new CarInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), 0, cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return info;
    }

    public int updateDateAndStatus(Context context, int id, String status, String dateTime, String region, String versionName, int versionCode) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("dateTime", dateTime);
        values.put("region", region);
        values.put("version_name", versionName);
        values.put("version_code", versionCode);
        int result = db.update("car_info", values, "_id" + "=" + id, null);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    public int updateLanguageStatus(Context context, int id, String lang) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put("language", lang);
        int result = db.update("car_info", values, "_id" + "=" + id, null);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    public int updateCarStatus(Context context, int id, int status) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put("status", status);
        int result = db.update("car_info", values, "_id" + "=" + id, null);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    public String getLanguageStatus(Context context, int id) {
        String status = "";
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT " + "language" + " FROM " + "car_info" + " WHERE " + "_id" + "=" + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    status = cursor.getString(i);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return status;
    }


    public int getStatus(Context context, int id) {
        int status = 0;
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT " + "status" + " FROM " + "car_info" + " WHERE " + "_id" + "=" + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    status = cursor.getInt(i);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return status;
    }

    public int getLastID(Context context) {
        int status = 0;
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT " + "_id" + " FROM " + "car_info" + " order by _id DESC limit 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    status = cursor.getInt(i);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return status;
    }

    public String getCarName(Context context, int id) {
        String status = "";
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT " + "name" + " FROM " + "car_info" + " WHERE " + "_id" + "=" + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    status = cursor.getString(i);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return status;
    }

    private ContentValues prepareMyTableListContentValues(CarInfo info) {
        ContentValues headValues = new ContentValues(8);
        headValues.put(CarInfoTableEntity.NAME, info.getName());
        headValues.put(CarInfoTableEntity.STATUS, info.getStatus());
        headValues.put(CarInfoTableEntity.DATE_TIME, info.getDateTime());
        headValues.put(CarInfoTableEntity.REGION, info.getRegion());
        headValues.put(CarInfoTableEntity.LANGUAGE, info.getSelectedLanguage());
        headValues.put(CarInfoTableEntity.VERSION_NAME, info.getVersionName());
        headValues.put(CarInfoTableEntity.VERSION_CODE, info.getVersionCode());
        headValues.put(CarInfoTableEntity.LIST_INDEX, info.getIndex());

        return headValues;
    }

    public long insertInCarInfoTable(Context context, CarInfo carInfo) {
        ContentValues libraryListValues = prepareMyTableListContentValues(carInfo);
        SQLiteDatabase db = getWritableDatabase(context);
        if (db.isOpen()) {
            long newHeadId = db.insert(CarInfoTableEntity.TABLE_NAME, "null", libraryListValues);
            db.close();
            return newHeadId;
        } else {
            return -1;
        }

    }

    public long insertInEpubTable(Context context, ArrayList<EpubInfo> list, int cartype, int epub_type) {
        SQLiteDatabase db = getWritableDatabase(context);
        int size = list.size();
        if (db.isOpen()) {
            try {
                for (int i = 0; i < size; i++) {
                    ContentValues cv = new ContentValues();
                    cv.put(EpubInfoTableDirectory.TITLE, list.get(i).getTitle());
                    cv.put(EpubInfoTableDirectory.TAG, "warranty");
                    cv.put(EpubInfoTableDirectory.LINK, list.get(i).getHtmlLink());
                    cv.put(EpubInfoTableDirectory.CARTYPE, cartype);
                    cv.put(EpubInfoTableDirectory.EPUBTYPE, epub_type);
                    newHeadId = db.insertOrThrow(EpubInfoTableDirectory.TABLE_NAME, null, cv);
                }
                db.close();

            } catch (Exception e) {
                Logger.error("Problem", e + " ");
            }
            return newHeadId;
        } else {
            return -1;
        }

    }

    public ArrayList<SearchModel> getAllSearchList(Context context, int cartype) {

        ArrayList<SearchModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT  * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.CARTYPE + "= " + cartype;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SearchModel info = new SearchModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                    list.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return list;
    }

    public long insertInSearchTable(Context context, ArrayList<SearchModel> list, int cartype) {
        SQLiteDatabase db = getWritableDatabase(context);
        int size = list.size();
        if (db.isOpen()) {
            try {
                for (int i = 0; i < size; i++) {
                    ContentValues cv = new ContentValues();
                    cv.put(SearchTagTableDirectory.SEARCHTAG, list.get(i).getSearchTag());
                    cv.put(SearchTagTableDirectory.DATE, list.get(i).getDate());
                    cv.put(SearchTagTableDirectory.COUNT, list.get(i).getCount());
                    cv.put(SearchTagTableDirectory.CARTYPE, cartype);
                    //newHeadId = db.insertOrThrow(SearchTagTableDirectory.TABLE_NAME, null, cv);
                    newHeadId = db.insertWithOnConflict(SearchTagTableDirectory.TABLE_NAME, SearchTagTableDirectory.SEARCHTAG, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }
                db.close();

            } catch (Exception e) {
                Logger.error("Problem Search ", e + " ");
            }
            return newHeadId;
        } else {
            return -1;
        }

    }

    public int updateInSearchTable(Context context, int count, String date, String searchtag) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("count", count);
        int result = db.update("search_info", values, "search" + " = '" + searchtag + "'", null);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    // This method for get count for specific search tag
    public int getCountForSpecificTag(Context context, String searchtag, int carType, String langType) {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT  * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.SEARCHTAG + " = '" + searchtag + "' AND " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + langType + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    count = Integer.parseInt(cursor.getString(3));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return count;
    }

    public ArrayList<SearchModel> getCountWiseList(Context context, int carType, String selectedLanguage) {
        ArrayList<SearchModel> List = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + selectedLanguage + "' ORDER BY " + SearchTagTableDirectory.COUNT + " DESC limit 10";

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SearchModel info = new SearchModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                    List.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return List;
    }

    public ArrayList<SearchModel> getDateWiseList(Context context, int carType, String selectedLanguage) {
        ArrayList<SearchModel> List = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + selectedLanguage + "' ORDER BY " + SearchTagTableDirectory.DATE + " DESC limit 10";

//        String selectQuery = "SELECT * FROM search_info WHERE car_type = '13' ORDER BY datetime(date) ASC Limit 10";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SearchModel info = new SearchModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                    List.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return List;
    }


    /*
     * This method for deleting recent search keyword that
     * stored in database and display it into recent fragment.
     */
    public boolean deleteRecentSearchesFromSearchTable(Context context, int carType, String selectedLanguage) {
        SQLiteDatabase db = getWritableDatabase(context);

        String deleteQuery = "DELETE FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + selectedLanguage + "'";

        if (db != null) {
            db.execSQL(deleteQuery);
            return true;
        }

        return false;
    }


    public ArrayList<SearchModel> getTotalList(Context context, int cartype) {
        ArrayList<SearchModel> List = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.CARTYPE + "= " + cartype;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SearchModel info = new SearchModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                    List.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return List;
    }

    // this method for checking search tag is existing or not into database
    public boolean isTagExists(Context context, String tag, int carType, String languageType) {
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.SEARCHTAG + " = '" + tag + "' AND " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + languageType + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return false;

    }

    public String getSearchTagFromDB(Context context, String tag, int carType, String languageType) {
        String searchTag = "";
        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + SearchTagTableDirectory.TABLE_NAME + " WHERE " + SearchTagTableDirectory.SEARCHTAG + " = '" + tag + "' AND " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + languageType + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    searchTag = cursor.getString(1);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return searchTag;
    }

    public int updateSearchCountInSearchTable(Context context, int count, String date, String searchTag, int carType, String langType) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put(SearchTagTableDirectory.DATE, date);
        values.put(SearchTagTableDirectory.COUNT, count);
        String whereClause = SearchTagTableDirectory.SEARCHTAG + " = '" + searchTag + "' AND " + SearchTagTableDirectory.CARTYPE + " = '" + carType + "' AND " + SearchTagTableDirectory.LANGUAGE_TYPE + " = '" + langType + "'";
        int result = db.update(SearchTagTableDirectory.TABLE_NAME, values, whereClause, null);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    /*
     * This method for insert search key into database
     */
    public long insertNewKeywordInSearchTable(Context context, SearchModel searchModel, int carType, String langType) {
        SQLiteDatabase db = getWritableDatabase(context);
        if (db.isOpen()) {
            try {
                ContentValues cv = new ContentValues();
                cv.put(SearchTagTableDirectory.SEARCHTAG, searchModel.getSearchTag());
                cv.put(SearchTagTableDirectory.DATE, searchModel.getDate());
                cv.put(SearchTagTableDirectory.COUNT, searchModel.getCount());
                cv.put(SearchTagTableDirectory.CARTYPE, carType);
                cv.put(SearchTagTableDirectory.LANGUAGE_TYPE, langType);
                //newHeadId = db.insertOrThrow(SearchTagTableDirectory.TABLE_NAME, null, cv);
                newHeadId = db.insertWithOnConflict(SearchTagTableDirectory.TABLE_NAME, SearchTagTableDirectory.SEARCHTAG, cv, SQLiteDatabase.CONFLICT_REPLACE);

                db.close();

            } catch (Exception e) {
                Logger.error("Problem Search ", e + " ");
            }
            return newHeadId;
        } else {
            return -1;
        }

    }

    public void deleteFromCarInfoTable(Context context, String carId) {
        SQLiteDatabase libraryListDb = getWritableDatabase(context);

        String whereClause = CarInfoTableEntity._ID + " = ?";
        String[] whereArgs = {carId};

        libraryListDb.delete(CarInfoTableEntity.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteCarFromCarInfoTableByIds(Context context, String[] ids) {
        SQLiteDatabase playListDb = getWritableDatabase(context);
        String args = TextUtils.join(", ", ids);
        playListDb.execSQL(String.format("DELETE FROM " + CarInfoTableEntity.TABLE_NAME + " WHERE " + CarInfoTableEntity._ID + " IN (%s);", args));
    }

    public long insertNotificationData(Context context, String car_id, String lang_id, String epub_id) {
        SQLiteDatabase db = getWritableDatabase(context);

        if (db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put(PushNotificationTableEntity.CAR_ID, car_id);
            cv.put(PushNotificationTableEntity.LANGUAGE_ID, lang_id);
            cv.put(PushNotificationTableEntity.EPUB_ID, epub_id);
            cv.put(PushNotificationTableEntity.STATUS, PushNotificationTableEntity.UPDATE_AVAILABLE);
            cv.put(PushNotificationTableEntity.DATE_TIME, NissanApp.getInstance().getDateTime());
            newHeadId = db.insert(PushNotificationTableEntity.TABLE_NAME, "null", cv);

            db.close();
            return newHeadId;

        } else {
            return -1;
        }
    }

    public ArrayList<PushContentInfo> getNotificationList(Context context, int carID, int langID) {

        ArrayList<PushContentInfo> list = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + PushNotificationTableEntity.TABLE_NAME + " WHERE " + PushNotificationTableEntity.CAR_ID + " = '" + carID + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langID + "' AND " + PushNotificationTableEntity.STATUS + " = '" + PushNotificationTableEntity.UPDATE_AVAILABLE + "'";
        Logger.error("query_push", "___________" + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    PushContentInfo info = new PushContentInfo();
                    info.setCarId(cursor.getInt(1) + "");
                    info.setLangId(cursor.getInt(2) + "");
                    info.setePubId(cursor.getInt(3) + "");
                    info.setStatus(cursor.getInt(4) + "");
                    info.setDateTime(cursor.getInt(5) + "");
                    list.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        return list;
    }

    public PushContentInfo getNotificationData(Context context, int carID, int langID, int ePubId) {

        PushContentInfo info = new PushContentInfo();

        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + PushNotificationTableEntity.TABLE_NAME + " WHERE " + PushNotificationTableEntity.CAR_ID + " = '" + carID + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langID + "' AND " + PushNotificationTableEntity.STATUS + " = '" + PushNotificationTableEntity.UPDATE_AVAILABLE + "' AND " + PushNotificationTableEntity.EPUB_ID + " = '" + ePubId + "'";
        Logger.error("select_query_single_push", "___________" + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    info.setCarId(cursor.getInt(1) + "");
                    info.setLangId(cursor.getInt(2) + "");
                    info.setePubId(cursor.getInt(3) + "");
                    info.setStatus(cursor.getInt(4) + "");
                    info.setDateTime(cursor.getInt(5) + "");

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        return info;
    }

    public int updatePushContentStatus(Context context, int cardId, int langId, int ePubId) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put(PushNotificationTableEntity.STATUS, PushNotificationTableEntity.UPDATE_UNAVAILABLE);
        String whereClause = PushNotificationTableEntity.CAR_ID + " = '" + cardId + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langId + "' AND " + PushNotificationTableEntity.EPUB_ID + " = '" + ePubId + "'";
        int result = db.update(PushNotificationTableEntity.TABLE_NAME, values, whereClause, null);
        Logger.error("updatePushContent_query", "_______" + whereClause);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    public int updateAllPushContentStatusForSingleCar(Context context, int cardId, int langId) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put(PushNotificationTableEntity.STATUS, PushNotificationTableEntity.UPDATE_UNAVAILABLE);
        String whereClause = PushNotificationTableEntity.CAR_ID + " = '" + cardId + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langId + "'";
        int result = db.update(PushNotificationTableEntity.TABLE_NAME, values, whereClause, null);
        Logger.error("updateAllPushContentStatusForSingleCar_query", "_______" + whereClause);
        if (db.isOpen()) {
            db.close();
        }
        return result;
    }

    public boolean checkIfCarInstalledBeforePush(Context context, int carId) {
        ArrayList<CarInfo> carList = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase(context);
        String selectQuery = "SELECT * FROM " + CarInfoTableEntity.TABLE_NAME + " WHERE " + CarInfoTableEntity.STATUS + " = '1'";
        Logger.error("select_query_car_info", "___________" + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    CarInfo info = new CarInfo();
                    info.setId(cursor.getInt(0));
                    info.setName(cursor.getInt(1) + "");
                    info.setStatus(cursor.getInt(2) + "");
                    info.setDateTime(cursor.getInt(3) + "");
                    info.setRegion(cursor.getInt(4) + "");
                    info.setSelectedLanguage(cursor.getInt(5) + "");
                    carList.add(info);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        for (CarInfo info : carList) {
            if (info.getId() == carId && "1".equalsIgnoreCase(info.getStatus()))
                return false;

        }

        return true;
    }

    public int makeAllPushEntryStatusChange(Context context, int carId, int langId, int ePubId) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put(PushNotificationTableEntity.STATUS, PushNotificationTableEntity.UPDATE_UNAVAILABLE);
        String whereClause = PushNotificationTableEntity.CAR_ID + " = '" + carId + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langId + "' AND " + PushNotificationTableEntity.EPUB_ID + " = '" + ePubId + "'";
        int result = db.update(PushNotificationTableEntity.TABLE_NAME, values, whereClause, null);
        Logger.error("makeAllPushEntryStatusChange", "_______" + whereClause);
        if (db.isOpen()) {
            db.close();
        }

        return result;
    }

    public int makeAllPushEntryStatusChangeLangauge(Context context, int carId, int langId) {
        SQLiteDatabase db = getWritableDatabase(context);
        ContentValues values = new ContentValues();
        values.put(PushNotificationTableEntity.STATUS, PushNotificationTableEntity.UPDATE_UNAVAILABLE);
        String whereClause = PushNotificationTableEntity.CAR_ID + " = '" + carId + "' AND " + PushNotificationTableEntity.LANGUAGE_ID + " = '" + langId + "'";
        int result = db.update(PushNotificationTableEntity.TABLE_NAME, values, whereClause, null);
        Logger.error("makeAllPushEntryStatusChangeLanguage", "_______" + whereClause);
        if (db.isOpen()) {
            db.close();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private SQLiteDatabase getWritableDatabase(Context context) {
        try {
            SQLiteOpenHelper helper = (SQLiteOpenHelper) ((Class) NissanDbHelper.class).getConstructor(Context.class).newInstance(context);
            return helper.getWritableDatabase();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
