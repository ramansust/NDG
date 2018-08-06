package com.nissan.alldriverguide.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nissan.alldriverguide.utils.Logger;

/**
 * @author Raman
 * @version 1.0
 */

public class NissanDbHelper extends SQLiteOpenHelper implements BaseDbHelper {

    static final String DATABASE_NAME = "nissan.sqlite";
    static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + CarInfoTableEntity.TABLE_NAME + " (" +
            CarInfoTableEntity._ID + INTEGER_PRIMARY + COMMA_SEP +
            CarInfoTableEntity.NAME + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.STATUS + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.DATE_TIME + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.REGION + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.LANGUAGE + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.VERSION_NAME + TEXT_TYPE + COMMA_SEP +
            CarInfoTableEntity.VERSION_CODE + INTEGER_TYPE + ")";

    private static final String CREATE_EPUBTABLE_QUERY = "CREATE TABLE " + EpubInfoTableDirectory.TABLE_NAME + " (" +
            EpubInfoTableDirectory._ID + INTEGER_PRIMARY + COMMA_SEP +
            EpubInfoTableDirectory.TITLE + TEXT_TYPE + COMMA_SEP +
            EpubInfoTableDirectory.TAG + TEXT_TYPE + COMMA_SEP +
            EpubInfoTableDirectory.LINK + TEXT_TYPE + COMMA_SEP +
            EpubInfoTableDirectory.CARTYPE + INTEGER_TYPE + COMMA_SEP +
            EpubInfoTableDirectory.EPUBTYPE + INTEGER_TYPE + COMMA_SEP +
            EpubInfoTableDirectory.INDEX + INTEGER_TYPE + ")";

    private static final String CREATE_SEARCHTAGTABLE = "CREATE TABLE " + SearchTagTableDirectory.TABLE_NAME + " (" +
            SearchTagTableDirectory._ID + INTEGER_PRIMARY + COMMA_SEP +
            SearchTagTableDirectory.SEARCHTAG + TEXT_TYPE + COMMA_SEP +
            SearchTagTableDirectory.DATE + TEXT_TYPE + COMMA_SEP +
            SearchTagTableDirectory.COUNT + INTEGER_TYPE + COMMA_SEP +
            SearchTagTableDirectory.CARTYPE + INTEGER_TYPE + COMMA_SEP +
            SearchTagTableDirectory.LANGUAGE_TYPE + TEXT_TYPE + ")";

    private static final String CREATE_TABLE_QUERY_PUSH_NOTIFICATION_TABLE = "CREATE TABLE " + PushNotificationTableEntity.TABLE_NAME + " (" +
            PushNotificationTableEntity._ID + INTEGER_PRIMARY + COMMA_SEP +
            PushNotificationTableEntity.CAR_ID + TEXT_TYPE + COMMA_SEP +
            PushNotificationTableEntity.LANGUAGE_ID + TEXT_TYPE + COMMA_SEP +
            PushNotificationTableEntity.EPUB_ID + TEXT_TYPE + COMMA_SEP +
            PushNotificationTableEntity.STATUS + TEXT_TYPE + COMMA_SEP +
            PushNotificationTableEntity.DATE_TIME + TEXT_TYPE + ")";

    private static final String DROP_SEARCH_TAG_TABLE = "DROP TABLE IF EXISTS " + SearchTagTableDirectory.TABLE_NAME;
    private static final String DROP_PUSH_NOTI_TABLE = "DROP TABLE IF EXISTS " + PushNotificationTableEntity.TABLE_NAME;
    private static final String DROP_EPUBTABLE_QUERY = "DROP TABLE IF EXISTS " + EpubInfoTableDirectory.TABLE_NAME;
    private static final String ADD_COLUMN_VERSION_NAME = "ALTER TABLE " + CarInfoTableEntity.TABLE_NAME + " ADD COLUMN " + CarInfoTableEntity.VERSION_NAME + TEXT_TYPE;
    private static final String ADD_COLUMN_VERSION_CODE = "ALTER TABLE " + CarInfoTableEntity.TABLE_NAME + " ADD COLUMN " + CarInfoTableEntity.VERSION_CODE + INTEGER_TYPE;

    public NissanDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
        db.execSQL(CREATE_EPUBTABLE_QUERY);
        db.execSQL(CREATE_SEARCHTAGTABLE);
        db.execSQL(CREATE_TABLE_QUERY_PUSH_NOTIFICATION_TABLE);
    }

/*
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DROP_TABLE_QUERY);
//        onCreate(db);

        // if user want to update a table ( want to add a column in the table) then a alter query is required as given below
        db.execSQL(ADD_COLUMN_VERSION_NAME);
        db.execSQL(ADD_COLUMN_VERSION_CODE);
        // it is also mandatory to update the create table query in  onCreate(SQLiteDatabase db) {  db.execSQL(CREATE_TABLE_QUERY_DURING_UPDATE_TABLE); }
    }
*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.error("confirm",
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        if (newVersion > oldVersion) {
            db.execSQL(DROP_SEARCH_TAG_TABLE);
            db.execSQL(DROP_PUSH_NOTI_TABLE);
            checkColumnExists(db);
        }
        manualOnCreate(db);
    }

    private void checkColumnExists(SQLiteDatabase db) {

        int columnIndex = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + CarInfoTableEntity.TABLE_NAME, null); // grab cursor for all data

        columnIndex = cursor.getColumnIndex(CarInfoTableEntity.VERSION_NAME);  // see if the column is there
        if (columnIndex < 0) {
            // missing_column not there - add it
            db.execSQL(ADD_COLUMN_VERSION_NAME);
        }

        columnIndex = cursor.getColumnIndex(CarInfoTableEntity.VERSION_CODE);  // see if the column is there
        if (columnIndex < 0) {
            // missing_column not there - add it
            db.execSQL(ADD_COLUMN_VERSION_CODE);
        }

    }

    private void manualOnCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_SEARCHTAGTABLE);
        db.execSQL(CREATE_TABLE_QUERY_PUSH_NOTIFICATION_TABLE);

    }

    public void onDeleteTable(SQLiteDatabase db) {
        Logger.error("Drop Table", ".............");
        db.execSQL(DROP_EPUBTABLE_QUERY);
        db.execSQL(CREATE_EPUBTABLE_QUERY);
    }
}
