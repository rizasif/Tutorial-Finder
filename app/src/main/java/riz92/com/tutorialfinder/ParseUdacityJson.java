package riz92.com.tutorialfinder;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import riz92.com.tutorialfinder.Data.DatabaseHandler;
import riz92.com.tutorialfinder.Data.course;

/**
 * Created by Rizwan Asif on 12/7/2014.
 */
public class ParseUdacityJson {

    String[] resultStrs;
    String LOG_TAG = "Rizwan: parseUdacityJsonClass";
    Context myContext;

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public void getDataFromJson(Context context, String JsonStr, String searchValue)
            throws JSONException {

        myContext = context;
        // These are the names of the JSON objects that need to be extracted.
        final String _LIST = "courses";
        final String _TITLE ="title";
        final String _KEY = "key";
        final String _SITE = "homepage";
        final String _LEVEL = "level";
        final String _DESCRIPTION = "summary";
        final String _PHOTO = "image";

        JSONObject jsonObject = new JSONObject(JsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray(_LIST);
        JsonStr = null;

        resultStrs = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String title;
            String description;
            String homepage;
            String level;
            String key;
            String photo;

            // Get the JSON object representing the day
            JSONObject course = jsonArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            title = course.getString(_TITLE);
            description = course.getString(_DESCRIPTION);
            homepage = course.getString(_SITE);
            level = course.getString(_LEVEL);
            key = course.getString(_KEY);
            photo = course.getString(_PHOTO);

            filterData(title, key, homepage, level, description, photo,searchValue);

            resultStrs[i] = title + "-" + key + "-" + level;


        }
//        data saved locally in resultStrs
    }

    private void filterData(String title, String key, String homepage, String level, String description, String photo ,String searchValue){

        if(title != null){
            if(title.contains(searchValue)){
                saveData(title, key, homepage, level, description, photo);
            }
        }else if (description != null){
            if(description.contains(searchValue)){
                saveData(title, key, homepage, level, description, photo);
            }
        }

    }

    private void saveData(String title, String key, String homepage, String level, String description, String photo){
        riz92.com.tutorialfinder.Data.course mCourse = new course(title, String.valueOf(key), homepage, level, description, photo);
        if(mCourse!=null) {
            DatabaseHandler DbH = new DatabaseHandler(myContext);
            DbH.addCourse(mCourse);
        }
    }

}
