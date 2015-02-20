package riz92.com.tutorialfinder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import riz92.com.tutorialfinder.Data.FlagDatabase;
import riz92.com.tutorialfinder.Data.course;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class StartupFragment extends Fragment{

        RadioGroup radioGroup;
        TextView txt;
        FlagDatabase fDB;
        ImageView intoImg;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public StartupFragment(){

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.startup_fragment, container, false);
            radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupp);
            txt = (TextView) rootView.findViewById(R.id.intoQ);
            fDB = new FlagDatabase(getActivity());
            intoImg = (ImageView) rootView.findViewById(R.id.imageViewInto);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(isOnline()) {
                        if (checkedId == R.id.radioButton) {
                            Log.e("Riz", "1");
                            StartUpActivity.search = "Programming";
                        } else if (checkedId == R.id.radioButton2) {
                            Log.e("Riz", "2");
                            StartUpActivity.search = "Math";
                        } else if (checkedId == R.id.radioButton3) {
                            Log.e("Riz", "3");
                            StartUpActivity.search = "Culture";
                        } else if (checkedId == R.id.radioButton4) {
                            Log.e("Riz", "4");
                            StartUpActivity.search = "Health";
                        }
                        txt.setText("Please wait while we set things up for you...");
                        course mCourse = new course(StartUpActivity.search,null,null,null,null,null);
                        fDB.addCourse(mCourse);
                        radioGroup.setVisibility(View.GONE);
                        intoImg.setImageResource(R.drawable.startupintro);
                        ((Callback) getActivity()).onItemSelected("Done");
                    } else {
                        Toast.makeText(getActivity(), "Not connected to the internet, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rootView;
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

        public interface Callback {
            /**
             * Callback for when an item has been selected.
             */
            public void onItemSelected(String date);
        }

    }

