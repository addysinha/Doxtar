package addysden.doctor.doxtar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import addysden.doctor.doxtar.UserDetails;

/**
 * Created by Abhyudaya sinha on 5/21/2016.
 */
public class UpdateUserSQLiteDb extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userDB";

    // Books table name
    private static final String TABLE_USER = "user";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "userName";
    private static final String KEY_USER_CODE = "userCode";

    private static final String[] COLUMNS = {KEY_ID,KEY_USER,KEY_USER_CODE};

    public UpdateUserSQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create user table
        String CREATE_USER_TABLE = "CREATE TABLE user ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userName TEXT, " +
                "userCode TEXT )";

        Log.d("OnCreate", "I am here");

        // create user table
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older user table if existed
        db.execSQL("DROP TABLE IF EXISTS user");

        Log.d("OnUpgrade", "I am here");

        // create fresh user table
        this.onCreate(db);
    }

    public void addUser(UserDetails userDet){
        //for logging
        Log.d("addUser", userDet.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, userDet.getId()); // get userName
        values.put(KEY_USER, userDet.getUserName()); // get userName
        values.put(KEY_USER_CODE, userDet.getUserCode()); // get userCode

        // 3. insert
        db.insert(TABLE_USER, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public UserDetails getUserDetails(String userName){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(TABLE_USER, // a. table
            COLUMNS, // b. column names
            " userName = ?", // c. selections
            new String[] { userName }, // d. selections args
            null, // e. group by
            null, // f. having
            null, // g. order by
            null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        UserDetails userDet = new UserDetails(userName);
        userDet.setId(Integer.parseInt(cursor.getString(0)));
        userDet.setUserName(cursor.getString(1));
        userDet.setUserCode(cursor.getString(2));

        //log
        Log.d("getUser("+ userName +")", userDet.toString());

        // 5. return user
        return userDet;
    }

    // Get All User
    public String getAllUser() {
        List<UserDetails> userDetList = new LinkedList<UserDetails>();
        String userName="", userCode="";

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_USER;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build user and add it to list
        UserDetails userDet = null;
        if (cursor.moveToFirst()) {
            do {
                userDet = new UserDetails();
                userDet.setId(Integer.parseInt(cursor.getString(0)));
                userDet.setUserName(cursor.getString(1));
                userDet.setUserCode(cursor.getString(2));
                userName = cursor.getString(1);
                userCode = cursor.getString(2);

                // Add user to users
                userDetList.add(userDet);
            } while (cursor.moveToNext());
        }

        Log.d("getAllUser()", userDetList.toString());

        if(userName.trim().equals("") && userCode.trim().equals("")) {
            return "";
        } else {
            // return user
            System.out.println(userName + "|" + userCode);
            return userName + "|" + userCode;
        }
    }

    public void deleteUser(String userName) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_USER, //table name
            KEY_USER+" = ?",// selections
            new String[] { userName }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteUser", userName.toString());
    }
}