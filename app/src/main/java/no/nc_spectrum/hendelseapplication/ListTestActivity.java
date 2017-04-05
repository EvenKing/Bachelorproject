package no.nc_spectrum.hendelseapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class ListTestActivity extends AppCompatActivity {

    private ListView mListView;
    private List<HashMap<String, Object>> events;
    private String userID = "";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        String strUrl = "http://mobapp.ncs.no/event.php";

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(strUrl);
        mListView = (ListView) findViewById(R.id.lv_events);

    }


    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        int tmp;
        HttpURLConnection httpURLConnection = null;
        InputStream is = null;
        OutputStream os = null;

        try {

            URL url = new URL(strUrl);

            String urlParams = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");

            if (httpURLConnection != null) httpURLConnection.disconnect();
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setDoOutput(true);
            os = httpURLConnection.getOutputStream();
            os.write(urlParams.getBytes());
            os.flush();
            os.close();

            if (is != null) is.close();

            is = httpURLConnection.getInputStream();

            /*
            while((tmp=is.read())!=-1){
                data+= (char)tmp;
            }

            is.close();
            */



            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

            is.close();



        } catch (Exception e) {
            Log.d("Exception Dloading url", e.toString());
            return "Exception: "+ e.toString();
        } finally {

            //is.close();
            httpURLConnection.disconnect();


            return data;
        }


    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(context, "userID: " + userID + "\n" + result , Toast.LENGTH_LONG).show();

            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(result);

        }
    }

    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {

        JSONObject jObject;

        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try {
                jObject = new JSONObject(strJson[0]);
                EventJSONParser tracksJSONParser = new EventJSONParser();
                tracksJSONParser.parse(jObject);
            } catch (Exception e) {
                Log.d("JSON Exception1", e.toString());
            }

            EventJSONParser tracksJSONParser = new EventJSONParser();

            events = null;

            try {
                events = tracksJSONParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }


            String[] from = {"signature", "timestamp", "priority_icon" };
            int[] to = {R.id.tv_event_name, R.id.tv_event_date, R.id.iv_smiley };
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), events, R.layout.lv_events_layout, from, to);

            return adapter;
        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {

            mListView.setAdapter(adapter);

            /*for (int i = 0; i < adapter.getCount(); i++) { //To get the position of each listview item later...
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                hm.put("position", i);
            }*/

        }
    }
}
