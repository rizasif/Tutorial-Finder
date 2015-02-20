package riz92.com.tutorialfinder.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class FlagDatabase extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FlagsManager";

    // Contacts table name
    private static final String TABLE_COURSES = "flagtable";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";

    public FlagDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_COURSES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addCourse(course mCourse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, mCourse.getName()); // Contact Name

        // Inserting Row
        db.insert(TABLE_COURSES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public course getCourse(int id) {
        Log.i("Rizwan", "Accessing DB at id: " + id);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COURSES, new String[] { KEY_ID,
                        KEY_NAME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        course mCourse;
        if(cursor.getString(0)!=null) {
            mCourse = new course(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            cursor.close();
            return mCourse;
        } else {
            cursor.close();
            return null;
        }
        // return contact
    }

    public void test (Cursor cursor){
        try{
            cursor.isNull(0);
        } catch (CursorIndexOutOfBoundsException e)
        {
            Log.e("Riz", "gotcha");
        }
    }

    public course getCourse(String title) {
        Log.i("Rizwan", "Accessing DB at Name: " + title);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COURSES, new String[] { KEY_ID,
                        KEY_NAME }, KEY_NAME + "=?",
                new String[] { title }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        try{
            cursor.isNull(0);
            course mCourse;

            mCourse = new course(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    null,
                    null,
                    null,
                    null,
                    null);
            cursor.close();
            return mCourse;
        } catch (CursorIndexOutOfBoundsException e)
        {
            Log.e("Riz", "gotcha");
            return null;
        }

    }
    // return contact


    // Getting All Contacts
    public List<course> getAllCourses() {
        List<course> contactList = new ArrayList<course>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COURSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                course contact = new course();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateCourse(course mCourse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, mCourse.getName()); // Contact Name


        // updating row
        return db.update(TABLE_COURSES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(mCourse.getId()) });
    }

    // Deleting single contact
    public void deleteCourse(course contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }


    // Getting contacts Count
    public int getCoursesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COURSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_COURSES);
        db.delete(TABLE_COURSES,null,null);
        db.close();
    }
}
