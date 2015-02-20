package riz92.com.tutorialfinder.SavedCourses;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import riz92.com.tutorialfinder.Data.SaveData;
import riz92.com.tutorialfinder.Data.course;
import riz92.com.tutorialfinder.MainActivity2;
import riz92.com.tutorialfinder.R;

public class SavedCourses extends Activity {
    ArrayAdapter myAdptr;
    SaveData sDB;
    Intent intent;
    public static course mCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_courses);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        intent = new Intent(this, MainActivity2.class);
        sDB = new SaveData(this);
        List<course> tempList = sDB.getAllCourses();
        List<String> myList = new ArrayList<String>();
        for(course cn: tempList) {
            myList.add(cn.getName());
        }
        final ListView arList = (ListView) findViewById(R.id.saved_data_listview);
        myAdptr = new ArrayAdapter<String>(this,R.layout.saved_data_item, R.id.saved_data_item_textview,myList);
        arList.setAdapter(myAdptr);

            arList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.e("Riz", adapterView.getItemAtPosition(position).toString());
                    mCourse = sDB.getCourse(adapterView.getItemAtPosition(position).toString());
                    startActivityForResult(intent, 0);
                }
                });
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_saved_courses, container, false);


            return rootView;
        }

    }
}
