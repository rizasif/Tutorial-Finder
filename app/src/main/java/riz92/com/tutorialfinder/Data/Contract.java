package riz92.com.tutorialfinder.Data;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class Contract {

    // All General Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "popularCoursesManager";

    // table name
    public static final String TABLE_COURSES = "popularcourses";

    // Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_COURSE_NUM = "coursenum";
    public static final String KEY_LINK = "link";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_PHOTO = "photo";




    //--For Provider--//
    public static final String CONTENT_AUTHORITY = "riz92.com.tutorialfinder";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR_DATA = "popular_course";

    //Specific for popular data
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_DATA).build();

    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_DATA;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_DATA;

    public static Uri buildPopularUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI,id);
    }

    public static Uri builfPopularNameUri(String name){
        return CONTENT_URI.buildUpon().appendPath(name).build();
    }

}
