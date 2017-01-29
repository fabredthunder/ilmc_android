package fr.ilovemycity.android.utils.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Fab on 17/11/2016.
 * All rights reserved
 */

public abstract class DAOBase extends SQLiteOpenHelper {

    protected final static String DB_NAME = "database.db";
    protected final static int DB_VERSION = 1;

    protected SQLiteDatabase mDb = null;

    public DAOBase(Context pContext) {
        super(pContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.i(getClass().getSimpleName(), "Creating database");
            db.execSQL(UserDAO.TABLE_CREATE);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error" + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(getClass().getSimpleName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(UserDAO.TABLE_DROP);
        onCreate(db);
    }

    public SQLiteDatabase open() {
        mDb = super.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return open();
    }

}