package riz92.com.tutorialfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import riz92.com.tutorialfinder.Data.SaveData;
import riz92.com.tutorialfinder.Data.course;
import riz92.com.tutorialfinder.SavedCourses.SavedCourses;


public class MainActivity2 extends Activity {
    static Activity activity;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        getFragmentManager().beginTransaction()
                .add(R.id.detail_act, new MainActivity2_Fragment())
                .commit();
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.detailsActivityTitle);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }



    static class downloadStuff extends AsyncTask<String, Void, Bitmap>{
        ImageView img;
        Bitmap mIcon_val;
        @Override
        protected Bitmap doInBackground(String... params) {
            URL newurl = null;
            mIcon_val= null;
            try {
                newurl = new URL(params[0]);
                 mIcon_val= BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon_val;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img = (ImageView) activity.findViewById(R.id.imageView2);
            if(mIcon_val !=null && img !=null) {
                img.setImageBitmap(mIcon_val);
            }
        }
    }

    public static class MainActivity2_Fragment extends Fragment{
        private static final String LOG_TAG = "Ris: MainActivity2: ";

        TextView txtTitle;
        TextView txtId;
        TextView txtLevel;
        TextView txtLink;
        TextView txtDetails;
        ImageView imageView;

        Intent intent;
        Intent goBackIntent;

        static course cn;
        SaveData sdb;
        private static ShareActionProvider mShareActionProvider;
        private boolean isSaved;

        @Override
        public void onStart() {
            super.onStart();
            sdb = new SaveData(getActivity());
            if(sdb.getCourse(cn.getName())!=null){
                isSaved=true;
            }else {
                isSaved=false;
            }
        }

        public MainActivity2_Fragment() {
            super();
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_main2_fragment, container, false);

            txtTitle = (TextView) rootView.findViewById(R.id.textView);
            txtId = (TextView) rootView.findViewById(R.id.textView2);
            txtLevel = (TextView) rootView.findViewById(R.id.textView3);
            txtLink = (TextView) rootView.findViewById(R.id.textView4);
            txtDetails = (TextView) rootView.findViewById(R.id.textView5);
            imageView = (ImageView) rootView.findViewById(R.id.imageView2);

            goBackIntent = new Intent(getActivity(), MainActivity.class);
            activity = getActivity();

            getOrigin();

            txtTitle.setText(cn.getName());
            txtId.setText(cn.getcourseNum());
            txtLevel.setText(cn.getLevel());
            txtLink.setText(cn.getLink());
            txtDetails.setText(cn.getDetails());

            new downloadStuff().execute(cn.getPhoto());
            return rootView;
        }

        private void getOrigin(){
            if(MainActivity.mTwoPane){

                Bundle arguments = getArguments();
                if(arguments!=null){
                    Log.e(LOG_TAG, "Here");
                    String theCallingClass = arguments.getString(MainActivity.PlaceholderFragment.DETAIL_EXTRA);
                    if(theCallingClass.equals(NavigationDrawerFragment.TWOPANE_DRAWER_EXTRA)){
                        cn = NavigationDrawerFragment.mCourse;
                    } else if(theCallingClass.equals(MainActivity.PlaceholderFragment.TWOPANE_MAIN)){
                        cn = MainActivity.mCourse;
                    } else {
                        Log.e(LOG_TAG, "Arguments broken");
                    }
                }
            }else {
                intent = getActivity().getIntent();
                String theCallingClass = intent.getStringExtra(MainActivity.PlaceholderFragment.DETAIL_EXTRA);
                if(theCallingClass.equals(MainActivity.PlaceholderFragment.TWOPANE_MAIN)) {
                    cn = MainActivity.mCourse;
                } else if (theCallingClass.equals(NavigationDrawerFragment.TWOPANE_DRAWER_EXTRA)){
                    cn = NavigationDrawerFragment.mCourse;
                }
            }
        }

        public static void saveThisCourse(Context context){
            SaveData sDB = new SaveData(context);
            if(sDB.getCourse(cn.getName())== null) {
                sDB.addCourse(cn);
            } else {
            }
        }

        public static void deleteThisCourse(Context context){
            SaveData sDB = new SaveData(context);
            if(sDB.getCourse(cn.getName())== null) {
            } else {
                sDB.deleteCourse(cn);
            }
        }

        private Intent createShareIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "#Tutorial Finder: " + cn.getLink());
            return shareIntent;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_main_activity2, menu);
            MenuItem shareMenu = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) shareMenu.getActionProvider();
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
            MenuItem deleteButton = menu.findItem(R.id.deleteButton);
            if(!isSaved) {
                deleteButton.setIcon(R.drawable.ic_save_white);
            }

            if(MainActivity.mTwoPane){
                deleteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(isSaved){
                            //Put up the Yes/No message box
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder
                                    .setTitle("Deleting Course: " + cn.getName())
                                    .setMessage("Are you sure?")
                                    .setIcon(R.drawable.ic_warning_amber)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Yes button clicked, do something
                                            deleteThisCourse(getActivity());
                                            Toast.makeText(getActivity(), "Course Deleted", Toast.LENGTH_SHORT).show();
                                            startActivity(goBackIntent);
                                        }
                                    })
                                    .setNegativeButton("No", null)						//Do nothing on no
                                    .show();
                        } else {
                            saveThisCourse(getActivity());
                            isSaved = true;
                            getActivity().invalidateOptionsMenu();
                            Toast.makeText(getActivity(), "Course Saved", Toast.LENGTH_SHORT).show();
                            startActivity(goBackIntent);
                        }
                        return true;
                    }
                });
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.e(LOG_TAG, "clicked");
            if(item.getTitle().equals(getString(R.string.action_delete))){
                if(isSaved){
                    //Put up the Yes/No message box
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder
                            .setTitle("Deleting Course: " + cn.getName())
                            .setMessage("Are you sure?")
                            .setIcon(R.drawable.ic_warning_amber)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes button clicked, do something
                                    deleteThisCourse(getActivity());
                                    Toast.makeText(getActivity(), "Course Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(goBackIntent);
                                }
                            })
                            .setNegativeButton("No", null)						//Do nothing on no
                            .show();
                } else {
                    saveThisCourse(getActivity());
                    isSaved = true;
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getActivity(), "Course Saved", Toast.LENGTH_SHORT).show();
                }
            }
            return super.onOptionsItemSelected(item);
        }


        public static interface MainActicity2Callbacks {
            /**
             * Called when an item in the navigation drawer is selected.
             */
            void onMainActivity2ItemSelected(int position);
        }

    }

}
