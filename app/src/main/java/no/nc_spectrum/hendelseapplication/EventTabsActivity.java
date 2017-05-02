package no.nc_spectrum.hendelseapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

public class EventTabsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    private static String userID = "";
    private Context context = this;

    private static String strUrl = "http://mobapp.ncs.no/event.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_tabs);

        Intent intent = getIntent();

        userID = intent.getStringExtra("userID");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_logout){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id == R.id.action_refresh){
            Intent i = new Intent(this, EventTabsActivity.class);
            i.putExtra("userID", userID);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private ListView mListViewAll;
        private ListView mListViewHigh;
        private ListView mListViewMedium;
        private ListView mListViewLow;
        private List<HashMap<String, Object>> allEvents;
        private List<HashMap<String, Object>> highEvents;
        private List<HashMap<String, Object>> mediumEvents;
        private List<HashMap<String, Object>> lowEvents;

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                View rootView = inflater.inflate(R.layout.fragment_sub_page01, container, false);

                DownloadTaskAll downloadTaskAll = new DownloadTaskAll();
                downloadTaskAll.execute(strUrl);
                mListViewAll = (ListView) rootView.findViewById(R.id.lv_events_all);

                mListViewAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        try {
                            HashMap<String, Object> event = findHashMapAt(position, allEvents);

                            //Starts new activity
                            Intent i = new Intent(getActivity(), EventInfoActivity.class);
                            i.putExtra("event", event);

                            startActivity(i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                View rootView = inflater.inflate(R.layout.fragment_sub_page02, container, false);

                DownloadTaskHigh downloadTaskHigh = new DownloadTaskHigh();
                downloadTaskHigh.execute(strUrl);

                mListViewHigh = (ListView) rootView.findViewById(R.id.lv_events_high);

                mListViewHigh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        try {
                            HashMap<String, Object> event = findHashMapAt(position, highEvents);

                            //Starts new activity
                            Intent i = new Intent(getActivity(), EventInfoActivity.class);
                            i.putExtra("event", event);

                            startActivity(i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                View rootView = inflater.inflate(R.layout.fragment_sub_page03, container, false);

                DownloadTaskMedium downloadTaskMedium = new DownloadTaskMedium();
                downloadTaskMedium.execute(strUrl);

                mListViewMedium = (ListView) rootView.findViewById(R.id.lv_events_medium);

                mListViewMedium.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        try {
                            HashMap<String, Object> event = findHashMapAt(position, mediumEvents);

                            //Starts new activity
                            Intent i = new Intent(getActivity(), EventInfoActivity.class);
                            i.putExtra("event", event);

                            startActivity(i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return rootView;
            }

            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 4){
                View rootView = inflater.inflate(R.layout.fragment_sub_page04, container, false);

                DownloadTaskLow downloadTaskLow = new DownloadTaskLow();
                downloadTaskLow.execute(strUrl);

                mListViewLow = (ListView) rootView.findViewById(R.id.lv_events_low);

                mListViewLow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        try {
                            HashMap<String, Object> event = findHashMapAt(position, lowEvents);

                            //Starts new activity
                            Intent i = new Intent(getActivity(), EventInfoActivity.class);
                            i.putExtra("event", event);

                            startActivity(i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return rootView;
            }
            else{
                View rootView = inflater.inflate(R.layout.fragment_sub_page01, container, false);

                DownloadTaskAll downloadTaskAll = new DownloadTaskAll();
                downloadTaskAll.execute(strUrl);
                mListViewAll = (ListView) rootView.findViewById(R.id.lv_events_all);

                return rootView;
            }
        }


        private HashMap<String, Object> findHashMapAt(int position, List<HashMap<String, Object>> list)
        {
            for ( HashMap<String, Object> hm : list )
            {
                try {
                    if ((Integer) hm.get("position") == position) {
                        return hm;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }


        private String downloadUrl(String strUrl, String priority) throws IOException {

            String data = "";
            HttpURLConnection httpURLConnection = null;
            InputStream is = null;
            OutputStream os = null;

            try {

                URL url = new URL(strUrl);

                String urlParams = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                urlParams += "&" + URLEncoder.encode("prioLvl", "UTF-8") + "=" + URLEncoder.encode(priority, "UTF-8");

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                is = httpURLConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();

                is.close();

                httpURLConnection.disconnect();


            } catch (Exception e) {
                Log.d("Exception Dloading url", e.toString());
                return "Exception: "+ e.toString(); //TODO: Modify this?
            }
            return data;

        }

        private class DownloadTaskAll extends AsyncTask<String, Integer, String> {
            String data = null;

            @Override
            protected String doInBackground(String... url) {
                try {
                    data = downloadUrl(url[0] , "0");
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {

                ListViewLoaderTaskAll listViewLoaderTaskAll = new ListViewLoaderTaskAll();
                listViewLoaderTaskAll.execute(result);

            }
        }

        private class ListViewLoaderTaskAll extends AsyncTask<String, Void, SimpleAdapter> {

            JSONObject jObject;

            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try {
                    jObject = new JSONObject(strJson[0]);
                    EventJSONParser eventJSONParser = new EventJSONParser();
                    eventJSONParser.parse(jObject);
                } catch (Exception e) {
                    Log.d("JSON Exception1", e.toString());
                }

                EventJSONParser eventJSONParser = new EventJSONParser();

                allEvents = null;

                try {
                    allEvents = eventJSONParser.parse(jObject);
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }


                String[] from = {"signature", "timestamp", "priority_icon", "hostname" };
                int[] to = {R.id.tv_event_name, R.id.tv_event_date, R.id.iv_smiley, R.id.tv_hostname };
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), allEvents, R.layout.lv_events_layout, from, to);

                return adapter;
            }

            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                mListViewAll.setAdapter(adapter);


                for (int i = 0; i < adapter.getCount(); i++) { //To get the position of each listview item later...
                    HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                    hm.put("position", i);
                }

                adapter.notifyDataSetChanged();

            }
        }


        private class DownloadTaskHigh extends AsyncTask<String, Integer, String> {
            String data = null;

            @Override
            protected String doInBackground(String... url) {
                try {
                    data = downloadUrl(url[0], "1" ); //TODO: Change priority level here
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {

                ListViewLoaderTaskHigh listViewLoaderTaskHigh = new ListViewLoaderTaskHigh();
                listViewLoaderTaskHigh.execute(result);

            }
        }

        private class ListViewLoaderTaskHigh extends AsyncTask<String, Void, SimpleAdapter> {

            JSONObject jObject;

            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try {
                    jObject = new JSONObject(strJson[0]);
                    EventJSONParser eventJSONParser = new EventJSONParser();
                    eventJSONParser.parse(jObject);
                } catch (Exception e) {
                    Log.d("JSON Exception1", e.toString());
                }

                EventJSONParser eventJSONParser = new EventJSONParser();

                highEvents = null;

                try {
                    highEvents = eventJSONParser.parse(jObject);

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }


                String[] from = {"signature", "timestamp", "priority_icon", "hostname" };
                int[] to = {R.id.tv_event_name, R.id.tv_event_date, R.id.iv_smiley, R.id.tv_hostname };
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), highEvents, R.layout.lv_events_layout, from, to);

                return adapter;
            }

            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                mListViewHigh.setAdapter(adapter);


                for (int i = 0; i < adapter.getCount(); i++) { //To get the position of each listview item later...
                    HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                    hm.put("position", i);
                }

                adapter.notifyDataSetChanged();

            }
        }


        private class DownloadTaskMedium extends AsyncTask<String, Integer, String> {
            String data = null;

            @Override
            protected String doInBackground(String... url) {
                try {
                    data = downloadUrl(url[0] , "2"); //TODO: Change priority level here
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {

                ListViewLoaderTaskMedium listViewLoaderTaskMedium = new ListViewLoaderTaskMedium();
                listViewLoaderTaskMedium.execute(result);

            }
        }

        private class ListViewLoaderTaskMedium extends AsyncTask<String, Void, SimpleAdapter> {

            JSONObject jObject;

            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try {
                    jObject = new JSONObject(strJson[0]);
                    EventJSONParser eventJSONParser = new EventJSONParser();
                    eventJSONParser.parse(jObject);
                } catch (Exception e) {
                    Log.d("JSON Exception1", e.toString());
                }

                EventJSONParser eventJSONParser = new EventJSONParser();

                mediumEvents = null;

                try {
                    mediumEvents = eventJSONParser.parse(jObject);

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }


                String[] from = {"signature", "timestamp", "priority_icon", "hostname" };
                int[] to = {R.id.tv_event_name, R.id.tv_event_date, R.id.iv_smiley, R.id.tv_hostname };
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), mediumEvents, R.layout.lv_events_layout, from, to);

                return adapter;
            }

            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                mListViewMedium.setAdapter(adapter);


                for (int i = 0; i < adapter.getCount(); i++) { //To get the position of each listview item later...
                    HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                    hm.put("position", i);
                }

                adapter.notifyDataSetChanged();

            }
        }


        private class DownloadTaskLow extends AsyncTask<String, Integer, String> {
            String data = null;

            @Override
            protected String doInBackground(String... url) {
                try {
                    data = downloadUrl(url[0] , "3"); //TODO: CHANGE HERE!
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {

                ListViewLoaderTaskLow listViewLoaderTaskLow = new ListViewLoaderTaskLow();
                listViewLoaderTaskLow.execute(result);

            }
        }

        private class ListViewLoaderTaskLow extends AsyncTask<String, Void, SimpleAdapter> {

            JSONObject jObject;

            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try {
                    jObject = new JSONObject(strJson[0]);
                    EventJSONParser eventJSONParser = new EventJSONParser();
                    eventJSONParser.parse(jObject);
                } catch (Exception e) {
                    Log.d("JSON Exception1", e.toString());
                }

                EventJSONParser eventJSONParser = new EventJSONParser();

                lowEvents = null;

                try {
                    lowEvents = eventJSONParser.parse(jObject);

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }


                String[] from = {"signature", "timestamp", "priority_icon", "hostname" };
                int[] to = {R.id.tv_event_name, R.id.tv_event_date, R.id.iv_smiley, R.id.tv_hostname };
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), lowEvents, R.layout.lv_events_layout, from, to);

                return adapter;
            }

            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                mListViewLow.setAdapter(adapter);


                for (int i = 0; i < adapter.getCount(); i++) { //To get the position of each listview item later...
                    HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                    hm.put("position", i);
                }

                adapter.notifyDataSetChanged();

            }
        }



    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_tab);
                case 1:
                    return getString(R.string.high_tab);
                case 2:
                    return getString(R.string.medium_tab);
                case 3:
                    return getString(R.string.low_tab);
            }
            return null;
        }
    }


}
