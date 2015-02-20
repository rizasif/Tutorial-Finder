package riz92.com.tutorialfinder;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riz92.com.tutorialfinder.Data.Contract;
import riz92.com.tutorialfinder.Data.FlagDatabase;
import riz92.com.tutorialfinder.Data.course;



public class StartUpActivity extends Activity implements StartupFragment.Callback {
    Intent intent;
    ProgressBar pbar;
    String[] resultStrs;
    public static String search;
    static Activity activity;
    public static int flag;
    FlagDatabase fDB;
    public static final String startupFlag = "startupflag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        getFragmentManager().beginTransaction()
                .replace(R.id.startActivity, new startupImageFragment())
                .commit();

        intent = new Intent(this, MainActivity.class);

        search = null;
        activity = this;
        flag =0;

         fDB= new FlagDatabase(this);

        if(fDB.getCourse(startupFlag)!=null){
            DelayStartActivity(2000);
        } else {
            DelayShowScreen(2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fDB.getCourse(startupFlag)!=null){
            DelayStartActivity(2000);
        }
    }

    private void DelayStartActivity(int duration){
        new Handler().postDelayed(new Runnable(){  //Import android.os.handler
            @Override
            public void run(){
                //Your function e.g StartActivity();
                startActivity(intent);
            }}, duration);
    }

    private void DelayShowScreen(int duration){
        new Handler().postDelayed(new Runnable(){  //Import android.os.handler
            @Override
            public void run(){
                //Your function e.g StartActivity();
                getFragmentManager().beginTransaction()
                        .replace(R.id.startActivity, new StartupFragment())
                        .commit();

            }}, duration);
    }

    public void comeOn(){

        new FetchDataTask(getApplicationContext()).execute(MainActivity.PlaceholderFragment.COURSERA);
        new FetchDataTask(getApplicationContext()).execute(MainActivity.PlaceholderFragment.UDACITY);
        course mCourse = new course(startupFlag,null,null,null,null,null);
        fDB.addCourse(mCourse);
    }

    @Override
    public void onItemSelected(String date) {
        comeOn();
    }


    public static class startupImageFragment extends Fragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.start_image_fragment, container, false);

            return rootView;
        }

    }



    public class FetchDataTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchDataTask.class.getSimpleName();
        private final Context mContext;

        public FetchDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar = (ProgressBar) activity.findViewById(R.id.progressBar2);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            flag++;
            if(flag==2){
                pbar.setVisibility(View.GONE);
                Intent intent1 = new Intent(activity, MainActivity.class);
                startActivity(intent1);
            }
        }

        public void getDataFromJson(String dataFlag, String JsonStr, String searchValue)
                throws JSONException {

            final String _LIST;
            final String _TITLE;
            final String _KEY;
            final String _SITE;
            final String _LEVEL;
            final String _DESCRIPTION;
            final String _LANGUAGE;
            final String _PHOTO;

            // These are the names of the JSON objects that need to be extracted.
            if(dataFlag.equals(MainActivity.PlaceholderFragment.COURSERA)) {
                _LIST = "elements";
                _TITLE = "name";
                _KEY = "id";
                _SITE = "shortName";
                _LEVEL = "targetAudience";
                _DESCRIPTION = "shortDescription";
                _LANGUAGE = "language";
                _PHOTO = "photo";
            }else {
                _LIST = "courses";
                _TITLE = "title";
                _KEY = "key";
                _SITE = "homepage";
                _LEVEL = "level";
                _DESCRIPTION = "summary";
                _PHOTO = "image";
                _LANGUAGE = "language";
            }


            JSONObject jsonObject = new JSONObject(JsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray(_LIST);
            JsonStr=null;

            resultStrs = new String[jsonArray.length()];
            Log.i(LOG_TAG,"--Parsing Loop Start--");
            for(int i = 0; i < jsonArray.length(); i++) {
                if(dataFlag.equals(MainActivity.PlaceholderFragment.COURSERA)){
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

                } else {
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
//            riz92.com.tutorialfinder.Data.course mCourse = new course(title, String.valueOf(key), homepage, level, description, photo);
//            if(mCourse!=null) {
//                PopularData DbH = new PopularData(getApplicationContext());
//                DbH.addCourse(mCourse);
//            }

            //Using Content Provider
            // First, check if the location with this city name exists in the db
            Cursor cursor = mContext.getContentResolver().query(
                    Contract.CONTENT_URI,
                    new String[]{Contract.KEY_ID},
                    Contract.KEY_NAME + " = ?",
                    new String[]{title},
                    null);

            if (cursor.moveToFirst()) {
                int IdIndex = cursor.getColumnIndex(Contract.KEY_ID);
                Log.e(LOG_TAG, "Data already exists in database with id: " + IdIndex);
            } else {
                ContentValues values = new ContentValues();
                values.put(Contract.KEY_NAME, title); // Contact Name
                values.put(Contract.KEY_COURSE_NUM, key); // Contact Phone
                values.put(Contract.KEY_LINK, homepage);
                values.put(Contract.KEY_LEVEL, level);
                values.put(Contract.KEY_DETAILS, description);
                values.put(Contract.KEY_PHOTO, photo);

                Uri locationInsertUri = mContext.getContentResolver()
                        .insert(Contract.CONTENT_URI, values);

                Log.e(LOG_TAG, "New Data saved with index: " + ContentUris.parseId(locationInsertUri));
            }
        }

        private String makeURL(String BASE, String Fields)
        {
            final String BASE_URL = BASE;
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("fields",Fields)
                    .build();
            Log.v(LOG_TAG, "Built Uri: " + builtUri.toString());
            return builtUri.toString();
        }

        @Override
        protected Void doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            String dataFlag = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 14;
            String baseUrl= null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                switch (dataFlag){
                    case MainActivity.PlaceholderFragment.COURSERA:
                        baseUrl = makeURL("https://api.coursera.org/api/catalog.v1/courses","name,shortDescription,id,shortName,targetAudience,aboutTheCourse,language");
                        break;
                    case MainActivity.PlaceholderFragment.UDACITY:
                        baseUrl = "https://www.udacity.com/public-api/v0/courses";
                        break;
                }

                URL url = new URL(baseUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getDataFromJson(dataFlag,forecastJsonStr,search);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
    }

}
