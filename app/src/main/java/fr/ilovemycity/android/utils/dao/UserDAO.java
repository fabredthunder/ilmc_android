package fr.ilovemycity.android.utils.dao;

/**
 * Created by Fab on 17/11/2016.
 * All rights reserved
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import fr.ilovemycity.android.models.User;

public final class UserDAO extends DAOBase {

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PROFILE_PICTURE = "picture";
    public static final String COLUMN_DATECREATED = "createdAt";
    public static final String COLUMN_ADMIN = "admin";
    public static final String COLUMN_CITY_ID = "cityId";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_ID + " TEXT, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_PASSWORD + " TEXT, " +
            COLUMN_TOKEN + " TEXT, " +
            COLUMN_FIRSTNAME + " TEXT, " +
            COLUMN_LASTNAME + " TEXT, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_PROFILE_PICTURE + " TEXT, " +
            COLUMN_DATECREATED + " TEXT, " +
            COLUMN_ADMIN + " BOOLEAN, " +
            COLUMN_CITY_ID + " TEXT );";


    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public Context context;

    public UserDAO(Context pContext) {
        super(pContext);
        context = pContext;
    }

    private User populateModel(Cursor cursor) {

        return new User(
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_TOKEN)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_FIRSTNAME)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_LASTNAME)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_PROFILE_PICTURE)),
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_DATECREATED)),
                cursor.getInt(cursor.getColumnIndex(UserDAO.COLUMN_ADMIN)) != 0,
                cursor.getString(cursor.getColumnIndex(UserDAO.COLUMN_CITY_ID))
        );
    }

    private ContentValues populateContent(User user) {
        ContentValues values = new ContentValues();
        values.put(UserDAO.COLUMN_USERNAME, user.getUsername() != null ? user.getUsername() : "");
        values.put(UserDAO.COLUMN_ID, user.getId() != null ? user.getId() : "");
        values.put(UserDAO.COLUMN_EMAIL, user.getEmail() != null ? user.getEmail() : "");
        values.put(UserDAO.COLUMN_PASSWORD, user.getPassword() != null ? user.getPassword() : "");
        values.put(UserDAO.COLUMN_TOKEN, user.getToken() != null ? user.getToken() : "");
        values.put(UserDAO.COLUMN_FIRSTNAME, user.getFirstname() != null ? user.getFirstname() : "");
        values.put(UserDAO.COLUMN_LASTNAME, user.getLastname() != null ? user.getLastname() : "");
        values.put(UserDAO.COLUMN_PHONE, user.getPhone() != null ? user.getPhone() : "");
        values.put(UserDAO.COLUMN_PROFILE_PICTURE, user.getPicture());
        values.put(UserDAO.COLUMN_DATECREATED, user.getCreatedAt());
        values.put(UserDAO.COLUMN_ADMIN, user.getAdmin());
        values.put(UserDAO.COLUMN_CITY_ID, user.getCityId());
        return values;
    }

    public long createUser(User _user) {

        if (!getUsers().isEmpty()) {
            for (User user : getUsers()) {
                deleteUser(user.getId());
            }
        }

        ContentValues values = populateContent(_user);
        return getDb().insert(UserDAO.TABLE_NAME, null, values);

    }

    public long updateUser(User user) {
        ContentValues values = populateContent(user);
        return getDb().update(UserDAO.TABLE_NAME, values, UserDAO.COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())});
    }

    public User getUser() {
        String select = "SELECT * FROM " + UserDAO.TABLE_NAME;
        Cursor cursor = getDb().rawQuery(select, null);

        if (cursor.moveToNext()) {
            return populateModel(cursor);
        }

        cursor.close();
        this.close();
        return null;
    }

    public List<User> getUsers() {
        String select = "SELECT * FROM " + UserDAO.TABLE_NAME;
        Cursor cursor = getDb().rawQuery(select, null);
        List<User> userList = new ArrayList<>();

        while (cursor.moveToNext()) {
            userList.add(populateModel(cursor));
        }

        cursor.close();
        this.close();
        return userList;
    }

    public int deleteUser(String id) {
        return getDb().delete(UserDAO.TABLE_NAME, UserDAO.COLUMN_ID + " = ?", new String[]{id});
    }

}