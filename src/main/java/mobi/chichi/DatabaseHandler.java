package mobi.chichi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "chichi";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IDNO = "idno";
    private static final String KEY_realname = "realname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_firstpage = "firstpage";
    private static final String KEY_bio = "bio";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_gender = "gender";
    private static final String KEY_contrycode = "contrycode";
    private static final String KEY_nposts = "nposts";
    private static final String KEY_profileimage = "profileimage";
    private static final String KEY_username = "username";
    private static final String KEY_ndaric = "ndaric";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_IDNO + " TEXT,"
                + KEY_realname + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_firstpage + " TEXT,"
                + KEY_bio + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_gender + " TEXT,"
                + KEY_contrycode + " TEXT,"
                + KEY_nposts + " TEXT,"
                + KEY_profileimage + " TEXT,"
                + KEY_username + " TEXT,"
                + KEY_ndaric + " TEXT"

                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion & oldVersion < 5) {
           // db.execSQL("ALTER TABLE " + TABLE_LOGIN + " ADD COLUMN " + KEY_emailverify + " TEXT");
        }

    }


    /**
     * Storing user details in database
     */
    public void addUser(String idno, String realname, String email, String firstpage, String bio, String phone, String gender, String contrycode, String nposts, String profileimage, String username, String ndaric) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDNO, idno); // idno
        values.put(KEY_realname, realname); // realname
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_firstpage, firstpage); // firstpage
        values.put(KEY_bio, bio); // bio
        values.put(KEY_PHONE, phone); // phone
        values.put(KEY_gender, gender); // phone
        values.put(KEY_contrycode, contrycode); // phone
        values.put(KEY_nposts, nposts); // nposts
        values.put(KEY_profileimage, profileimage); // profileimage
        values.put(KEY_username, username); // username
        values.put(KEY_ndaric, ndaric); // ndaric

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }


    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("idno", cursor.getString(1));
            user.put("realname", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("firstpage", cursor.getString(4));
            user.put("bio", cursor.getString(5));
            user.put("phone", cursor.getString(6));
            user.put("gender", cursor.getString(7));
            user.put("contrycode", cursor.getString(8));
            user.put("nposts", cursor.getString(9));
            user.put("profileimage", cursor.getString(10));
            user.put("username", cursor.getString(11));
            user.put("ndaric", cursor.getString(12));

        }
        cursor.close();
        db.close();
        // return user
        return user;
    }


    /**
     * Re crate database
     * Delete all tables and create them again
     */
    public void resetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }
}
