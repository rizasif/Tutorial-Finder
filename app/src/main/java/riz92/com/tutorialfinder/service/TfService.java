package riz92.com.tutorialfinder.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import riz92.com.tutorialfinder.MainActivity;
import riz92.com.tutorialfinder.ParseCourseraJson;

import riz92.com.tutorialfinder.ParseUdacityJson;
import riz92.com.tutorialfinder.R;

/**
 * Created by Rizwan Asif on 12/27/2014.
 */
public class TfService extends IntentService {
    String LOG_TAG = "Riz: TfService";
    String ERROR_TAG = "ERROR: ";
    public static final String EXTRA = "TF_SERVCE_EXTRA";
    ProgressBar pBar;
    public static Activity carrierActivity;
    ArrayAdapter<String> myAdpt = MainActivity.PlaceholderFragment.dataArrayAdapter;
    TextView statusView;

    @Override
    public void onCreate() {
        super.onCreate();
        pBar = (ProgressBar) carrierActivity.findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);
        statusView = (TextView) carrierActivity.findViewById(R.id.statusView);
        statusView.setText("Please Wait while we fetch the courses...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pBar.setVisibility(View.GONE);
        MainActivity.PlaceholderFragment.display(carrierActivity, myAdpt);
        statusView.setText("Showing Results for: " + searchValue);
    }

    String searchValue;

    public TfService() {
        super("TfService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String dataFlag = intent.getStringExtra(EXTRA);
        searchValue = intent.getStringExtra(MainActivity.PlaceholderFragment.SEARCH_EXTRA);
        String baseUrl= null;
        Log.i(LOG_TAG,"In onHandleIntent for: " + dataFlag);

        switch (dataFlag){
            case MainActivity.PlaceholderFragment.COURSERA:
                baseUrl = makeURL("https://api.coursera.org/api/catalog.v1/courses","name,shortDescription,id,shortName,targetAudience,aboutTheCourse,language");
                break;
            case MainActivity.PlaceholderFragment.UDACITY:
                baseUrl = "https://www.udacity.com/public-api/v0/courses";
                break;
        }

        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String JsonStr = null;


        try {
            if(baseUrl==null){
                Log.e(LOG_TAG, ERROR_TAG + "baseUri Null");
            }
            URL url = new URL(baseUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Put LOading Here
            Log.e(LOG_TAG, "Waiting..");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.e(LOG_TAG, ERROR_TAG+"inputStream NULL");
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
                JsonStr = null;
            }
            JsonStr = buffer.toString();

            Log.v(LOG_TAG, "Forecast Json String: " + JsonStr);
        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            Log.e(LOG_TAG, ERROR_TAG + "IOException while getting JsonString");
        } finally{
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

        try{
            Log.i(LOG_TAG,"--About to Parse--");
            if(dataFlag.equals(MainActivity.PlaceholderFragment.UDACITY))
            {
                ParseUdacityJson myJson = new ParseUdacityJson();
                myJson.getDataFromJson(this,JsonStr, searchValue);
            }
            else if (dataFlag.equals(MainActivity.PlaceholderFragment.COURSERA))
            {
                ParseCourseraJson myJson = new ParseCourseraJson();
                myJson.getDataFromJson(this,JsonStr, searchValue);
            }
            JsonStr = null;
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
            Log.i(LOG_TAG, "Done with: " + dataFlag);
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

}
