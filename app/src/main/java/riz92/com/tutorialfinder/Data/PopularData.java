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
 * Created by Rizwan Asif on 12/30/2014.
 */
public class PopularData extends SQLiteOpenHelper {


    public PopularData(Context context) {
        super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + Contract.TABLE_COURSES + "("
                + Contract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.KEY_NAME + " TEXT,"
                + Contract.KEY_COURSE_NUM + " TEXT,"
                + Contract.KEY_LINK + " TEXT,"
                + Contract.KEY_LEVEL + " TEXT,"
                + Contract.KEY_DETAILS + " TEXT,"
                + Contract.KEY_PHOTO + " TEXT"
                + ")";
        db.execSQL(CREATE_COURSES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_COURSES);

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
        values.put(Contract.KEY_NAME, mCourse.getName()); // Contact Name
        values.put(Contract.KEY_COURSE_NUM, mCourse.getcourseNum()); // Contact Phone
        values.put(Contract.KEY_LINK, mCourse.getLink());
        values.put(Contract.KEY_LEVEL, mCourse.getLevel());
        values.put(Contract.KEY_DETAILS, mCourse.getDetails());
        values.put(Contract.KEY_PHOTO, mCourse.getPhoto());

        // Inserting Row
        db.insert(Contract.TABLE_COURSES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public course getCourse(int id) {
        Log.i("Rizwan", "Accessing DB at id: " + id);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Contract.TABLE_COURSES, new String[] { Contract.KEY_ID,
                        Contract.KEY_NAME, Contract.KEY_COURSE_NUM, Contract.KEY_LINK, Contract.KEY_LEVEL, Contract.KEY_DETAILS, Contract.KEY_PHOTO }, Contract.KEY_ID + "=?",
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

        Cursor cursor = db.query(Contract.TABLE_COURSES, new String[] { Contract.KEY_ID,
                        Contract.KEY_NAME, Contract.KEY_COURSE_NUM, Contract.KEY_LINK, Contract.KEY_LEVEL, Contract.KEY_DETAILS, Contract.KEY_PHOTO }, Contract.KEY_NAME + "=?",
                new String[] { title }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        try{
            cursor.isNull(0);
            course mCourse;

            mCourse = new course(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));
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
        String selectQuery = "SELECT  * FROM " + Contract.TABLE_COURSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                course contact = new course();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setcourseNum(cursor.getString(2));
                contact.setLink(cursor.getString(3));
                contact.setLevel(cursor.getString(4));
                contact.setDetails(cursor.getString(5));
                contact.setPhoto(cursor.getString(6));
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
        values.put(Contract.KEY_NAME, mCourse.getName()); // Contact Name
        values.put(Contract.KEY_COURSE_NUM, mCourse.getcourseNum()); // Contact Phone
        values.put(Contract.KEY_LINK, mCourse.getLink());
        values.put(Contract.KEY_LEVEL, mCourse.getLevel());
        values.put(Contract.KEY_DETAILS, mCourse.getDetails());
        values.put(Contract.KEY_PHOTO, mCourse.getPhoto());


        // updating row
        return db.update(Contract.TABLE_COURSES, values, Contract.KEY_ID + " = ?",
                new String[] { String.valueOf(mCourse.getId()) });
    }

    // Deleting single contact
    public void deleteCourse(course contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contract.TABLE_COURSES, Contract.KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }


    // Getting contacts Count
    public int getCoursesCount() {
        String countQuery = "SELECT  * FROM " + Contract.TABLE_COURSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Contract.TABLE_COURSES);
        db.delete(Contract.TABLE_COURSES,null,null);
        db.close();
    }
}
