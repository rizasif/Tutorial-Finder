package riz92.com.tutorialfinder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import riz92.com.tutorialfinder.Data.DatabaseHandler;
import riz92.com.tutorialfinder.Data.course;

/**
 * Created by Rizwan Asif on 12/7/2014.
 */
public class ParseCourseraJson {

    String[] resultStrs;
    String LOG_TAG = "Rizwan: parseCourseraJsonClass";
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
        myContext=context;

        // These are the names of the JSON objects that need to be extracted.
        final String _LIST = "elements";
        final String _TITLE ="name";
        final String _KEY = "id";
        final String _SITE = "shortName";
        final String _LEVEL = "targetAudience";
        final String _DESCRIPTION = "shortDescription";
        final String _LANGUAGE = "language";
        final String _PHOTO = "photo";

        JSONObject jsonObject = new JSONObject(JsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray(_LIST);
        JsonStr=null;

        resultStrs = new String[jsonArray.length()];
        Log.i(LOG_TAG,"--Parsing Loop Start--");
        for(int i = 0; i < jsonArray.length(); i++) {

            String title= "No Data Available";
            String description= "No Data Available";
            String homepage= "No Data Available";
            String level= "No Data Available";
            String language = "No Data Available";
            String photo = "";
            int key= 0;

            // Get the JSON object representing the day
            JSONObject course = jsonArray.getJSONObject(i);

            title = course.getString(_TITLE);
            description = course.getString(_DESCRIPTION);

            if(course.has(_LANGUAGE))
                language = course.getString(_LANGUAGE);


            if(course.has(_SITE)) {
                homepage = "https://www.coursera.org/course/" + course.getString(_SITE);
            }


            if(course.has(_LEVEL)) {
                level = "Not Specified";

                switch (course.getInt(_LEVEL)) {
                    case 0:
                        level = "Basic Undergraduates";
                        break;
                    case 1:
                        level = "Advanced undergraduates or beginning graduates";
                        break;
                    case 2:
                        level = "Advanced graduates";
                        break;
                    case 4:
                        level = "Not Specified";
                        break;
                }
            }
            key = course.getInt(_KEY);

            if(course.has(_PHOTO)){
                photo = course.getString(_PHOTO);
            }

            resultStrs[i] = title + "-" + key + "-" + level;

        if(language.equals("en")){
            filterData(title, String.valueOf(key), homepage, level, description, photo,searchValue);
        }

        }
        Log.i(LOG_TAG,"--Parsing Loop End--");


//        data saved locally in resultStrs;
    }

    private void filterData(String title, String key, String homepage, String level, String description, String photo, String searchValue){

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
