package riz92.com.tutorialfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import riz92.com.tutorialfinder.Data.DatabaseHandler;
import riz92.com.tutorialfinder.Data.PopularData;
import riz92.com.tutorialfinder.Data.SaveData;
import riz92.com.tutorialfinder.Data.course;
import riz92.com.tutorialfinder.SavedCourses.SavedCourses;
import riz92.com.tutorialfinder.service.TfService;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    String LOG_TAG = "Rizwan: MainActivityFragment";


    public static boolean mTwoPane;
    public static course mCourse;
    public static Activity carrierActivity;
    public static boolean presenceFlag;

    //git test change

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.navigation_drawer_wide)!=null){
            mTwoPane = true;
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.navigation_drawer_wide, new NavigationDrawerFragment())
                        .commit();
                getFragmentManager().beginTransaction()
                        .replace(R.id.Container, new PlaceholderFragment())
                        .commit();
            }
        }else {
            mTwoPane = false;
            //hellp
        }

        TfService.carrierActivity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.app_name);
        restoreActionBar();

        if(!MainActivity.mTwoPane) {
            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mTwoPane){
            getFragmentManager().beginTransaction()
                    .replace(R.id.navigation_drawer_wide, new NavigationDrawerFragment())
                    .commit();
            getFragmentManager().beginTransaction()
                    .replace(R.id.Container, new PlaceholderFragment())
                    .commit();
        } else {
            if(!presenceFlag) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                presenceFlag = true;
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }




    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        String LOG_TAG = "Rizwan: MainActivityFragment";
        public static final String UDACITY = "udacity";
        public static final String COURSERA = "coursera";
        public static final String KHAN = "khan";
        public static final String SEARCH_EXTRA = "thesearch";
        public static final String DETAIL_EXTRA = "detailextra";
        public static final String TWOPANE_MAIN = "main";
        public static ArrayAdapter<String> dataArrayAdapter;
        ListView arList;
        EditText searchET;
        TextView statusTxt;
        Intent intent;
        Intent savedActivityIntent;
        String defaultListView;
        DatabaseHandler DbH;

        SearchView searchView;


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            DbH = new DatabaseHandler(getActivity());
            statusTxt = (TextView) rootView.findViewById(R.id.statusView);
            intent = new Intent(getActivity(), MainActivity2.class);
            savedActivityIntent = new Intent(getActivity(), SavedCourses.class);
            clearDataBase();
            makeList(rootView);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        public void go2Saved(View view){
            startActivity(savedActivityIntent);
        }

        private void clearDataBase(){
            DatabaseHandler DbH2 = new DatabaseHandler(getActivity());
            if(DbH2.getCoursesCount()!=0) {
                List<course> myList= DbH2.getAllCourses();
                for(course cn :myList){
                    DbH2.deleteCourse(cn);
                }
                Log.i(LOG_TAG, "Old Database Deleted");
            }
        }

        public static void display(Context context, ArrayAdapter<String> myAdptr){
            DatabaseHandler db = new DatabaseHandler(context);
            List<course> myList= db.getAllCourses();
            for(course cn : myList){
                Log.i("Riz", cn.getName());
                myAdptr.add(cn.getName());
            }
        }

        public void update(String searchValue)
        {
            if (isOnline()) {

                if(searchValue != null) {
                    Log.i(LOG_TAG, "--Starting Service--");
                    clearDataBase();
                    dataArrayAdapter.clear();
                    Intent intentUdacity = new Intent(getActivity(), TfService.class);
                    intentUdacity.putExtra(TfService.EXTRA, UDACITY);
                    intentUdacity.putExtra(SEARCH_EXTRA, searchValue);
                    getActivity().startService(intentUdacity);

                    Intent intentCoursera = new Intent(getActivity(), TfService.class);
                    intentCoursera.putExtra(TfService.EXTRA, COURSERA);
                    intentCoursera.putExtra(SEARCH_EXTRA, searchValue);
                    getActivity().startService(intentCoursera);
                }
            }
            else {
                Toast.makeText(getActivity(),"Not connected to the internet", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isOnline() {
            ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }
            return false;
        }

        public void makeList(View rootView)
        {
            PopularData pd = new PopularData(getActivity());
            List<course> myList = pd.getAllCourses();

            dataArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.data_listview_item,R.id.dataListViewItem,new ArrayList<String>());

            arList = (ListView) rootView.findViewById(R.id.listView);
            arList.setAdapter(dataArrayAdapter);

            for(course cn : myList){
                dataArrayAdapter.add(cn.getName());
            }

            arList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        presenceFlag = false;
                        if(statusTxt.getText().toString().equals(getString(R.string.default_status))){
                            PopularData pd = new PopularData(getActivity());
                            mCourse = pd.getCourse(adapterView.getItemAtPosition(position).toString());
                        } else {
                            mCourse = DbH.getCourse(adapterView.getItemAtPosition(position).toString());
                        }


                        if(!mTwoPane) {
                            intent.putExtra(PlaceholderFragment.DETAIL_EXTRA, TWOPANE_MAIN);
                            startActivityForResult(intent, 0);
                        } else {
                            Bundle args = new Bundle();
                            args.putString(MainActivity.PlaceholderFragment.DETAIL_EXTRA, TWOPANE_MAIN);

                            MainActivity2.MainActivity2_Fragment fragment;
                            fragment = new MainActivity2.MainActivity2_Fragment();
                            fragment.setArguments(args);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.Container, fragment)
                                    .commit();
                        }
                }});
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.search_menu, menu);

            searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null && TextUtils.getTrimmedLength(query) > 0) {
                        Log.e(LOG_TAG, "Search: " + query);
                        update(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(LOG_TAG,"Menu Item Clicked");
            if(mTwoPane){
                if(item.getTitle().equals(getString(R.string.action_delete))) {
                    Log.i(LOG_TAG,"Menu Item Clicked: save/delete button");
                    boolean isSaved;
                    SaveData sdb = new SaveData(getActivity());
                    if (sdb.getCourse(MainActivity2.MainActivity2_Fragment.cn.getName()) != null) {
                        isSaved = true;
                    } else {
                        isSaved = false;
                    }
                    if (item.getTitle().equals(getString(R.string.action_delete))) {
                        if (isSaved) {
                            //Put up the Yes/No message box
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder
                                    .setTitle("Deleting Course: " + MainActivity2.MainActivity2_Fragment.cn.getName())
                                    .setMessage("Are you sure?")
                                    .setIcon(android.R.drawable.stat_sys_warning)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Yes button clicked, do something
                                            MainActivity2.MainActivity2_Fragment.deleteThisCourse(getActivity());
                                            Toast.makeText(getActivity(), "Course Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("No", null)                        //Do nothing on no
                                    .show();
                        } else {
                            MainActivity2.MainActivity2_Fragment.saveThisCourse(getActivity());
                            isSaved = true;
                            getActivity().invalidateOptionsMenu();
                            Toast.makeText(getActivity(), "Course Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            return super.onOptionsItemSelected(item);
        }

    }

}
