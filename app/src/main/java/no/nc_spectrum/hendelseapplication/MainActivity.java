package no.nc_spectrum.hendelseapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    private Context context = this;
    private EditText phoneEditText;
    private EditText pwdEditText;
    private String phoneNumber;
    private String password;
    private Intent ii;
    private final String strUrl = "http://mobapp.ncs.no/login.php";

    private String NAME=null, PASSWORD=null, USERID=null, CID=null;

    static boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneEditText = (EditText)findViewById(R.id.phoneEditText);
        pwdEditText = (EditText) findViewById(R.id.passwordEditText);

        isRunning = true; //Used to check if the app is active (in the foreground)
    }

    protected void onResume(){
        super.onResume();
        isRunning = true;
        UpdateCheck.nullify(getApplicationContext());
    }

    protected void onStop(){
        super.onStop();
        isRunning = false;
    }


    public void login(View view) { //Listener for when the Login button is pushed

        phoneNumber = phoneEditText.getText().toString();
        password = pwdEditText.getText().toString();

        BackGround b = new BackGround();

        if(!isInteger(phoneNumber)){ //to prevent SQL injections
            Toast.makeText(getApplicationContext(), "Brukernavn eller passord er feil!", Toast.LENGTH_LONG).show();
        }
        else if(!phoneNumber.isEmpty() && !password.isEmpty()) {
            if(isMyServiceRunning(UpdateCheck.class)){
                UpdateCheck.loggedIn = false;
                stopService(new Intent(MainActivity.this, UpdateCheck.class)); //stops notification service when button pressed
            }                                      //this is to prevent notifications from being sent when invalid credentials are filled in
            b.execute(phoneNumber, password); //service is started again here...
        }
        else {
            Toast.makeText(getApplicationContext(), "Skriv inn brukernavn og passord!", Toast.LENGTH_LONG).show();
        }

    }

    private static boolean isInteger(String str){ //Checks if String is an integer
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) { //Checks if a service is running
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) { //Checks login credentials and launches EventTabs if they match
            String name = params[0];
            String password = params[1];
            String data="";
            int tmp;
            HttpURLConnection httpURLConnection = null;
            InputStream is = null;



            try {

                URL url = new URL(strUrl);

                String urlParams = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                urlParams += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();

                httpURLConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            String err = null;

            try {
                JSONObject root = new JSONObject(s);

                JSONObject user_data = root.getJSONObject("user_data");
                boolean error = user_data.getBoolean("error");

                if(!error) {

                    NAME = user_data.getString("userName");
                    PASSWORD = user_data.getString("userPass");
                    USERID = user_data.getString("userID");
                    CID = user_data.getString("cid");

                    Intent i = new Intent(context, EventTabsActivity.class);

                    i.putExtra("userName", NAME); //NOT USED, only left here for further development purposes
                    i.putExtra("userPass", PASSWORD); //NOT USED
                    i.putExtra("err", err); //NOT USED
                    i.putExtra("userID", USERID); //Sent to "EventTabs" because it is needed for downloading of the event data via the fileserver

                    startActivity(i); //Starts "EventTabs" activity

                    //Starts notification service
                    ii = new Intent(getBaseContext(), UpdateCheck.class);
                    ii.putExtra("userid", USERID);
                    ii.putExtra("cid", CID);
                    ii.putExtra("loggedin", true);

                    Log.i(MainActivity.class.getSimpleName()," Har laget Intent!");
                    startService(ii); //Starter tjenesten her
                    finish(); //dreper main-activity

                } else {
                    String errorMsg = user_data.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show(); //viser feilmelding på skjermen
                }
            } catch (JSONException e) {
                e.printStackTrace();
                String errorMsg = "onPostExecution Exception: " + e.getMessage();
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show(); //viser feilmelding på skjermen
            }catch (NullPointerException e){
                e.printStackTrace();
                err = "Nullpointerexception: " + e.getMessage();
            }catch (Exception e){
                e.printStackTrace();
                Log.i(e.getMessage(), "");
            }

        }
    }
    protected void onDestroy(){
        super.onDestroy();
        isRunning = false;
    }


}
