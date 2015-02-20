package riz92.com.tutorialfinder.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

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
import riz92.com.tutorialfinder.MainActivity;
import riz92.com.tutorialfinder.R;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class TfSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "Riz: TfSyncAdapter";
    String [] resultStrs;

    public static final int SYNC_INTERVAL = 15 * 3600 *24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public TfSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
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
        Log.i(LOG_TAG, "--Parsing Loop Start--");
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
        Cursor cursor = getContext().getContentResolver().query(
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

            Uri InsertUri = getContext().getContentResolver()
                    .insert(Contract.CONTENT_URI, values);

            Log.e(LOG_TAG, "New Data saved with index: " + ContentUris.parseId(InsertUri));
            notifyCourse(title);
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
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String dataFlag = MainActivity.PlaceholderFragment.COURSERA;

        FlagDatabase fDB = new FlagDatabase(getContext());
        String search;

        if(fDB.getCourse("Programming")!=null){
            search = "Programming";
        } else if(fDB.getCourse("Math")!=null){
            search = "Math";
        } else if(fDB.getCourse("Culture")!=null){
            search = "Culture";
        } else if(fDB.getCourse("Health")!=null){
            search = "Health";
        } else {
            search = "Programming";
        }
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
                Log.e(LOG_TAG, "no input stream");
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
                Log.e(LOG_TAG, "stream was empty");
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

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




        dataFlag = MainActivity.PlaceholderFragment.UDACITY;


        urlConnection = null;
        reader = null;

        // Will contain the raw JSON response as a string.
        forecastJsonStr = null;

        format = "json";
        units = "metric";
        numDays = 14;
        baseUrl= null;

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
                Log.e(LOG_TAG, "no input stream");
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
                Log.e(LOG_TAG, "stream was empty");
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

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

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TfSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public void notifyCourse(String contentText){
        // NotificationCompatBuilder is a very convenient way to build backward-compatible
        // notifications.  Just throw in some data.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.id.icon)
                        .setContentTitle("New Course Available")
                        .setContentText(contentText);

        // Make something interesting happen when the user clicks on the notification.
        // In this case, opening the app is sufficient.
        Intent resultIntent = new Intent(getContext(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
    }
}
