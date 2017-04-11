package no.nc_spectrum.hendelseapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


    Context context = this;

    EditText phoneEditText;
    EditText pwdEditText;

    String phoneNumber;
    String password;

    String NAME=null, PASSWORD=null, USERID=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneEditText = (EditText)findViewById(R.id.phoneEditText);
        pwdEditText = (EditText) findViewById(R.id.passwordEditText);

    }


    public void login(View view) {

        phoneNumber = phoneEditText.getText().toString();
        password = pwdEditText.getText().toString();

        BackGround b = new BackGround();

        if(!phoneNumber.isEmpty() && !password.isEmpty()) {
            b.execute(phoneNumber, password);
        }
        else {
            Toast.makeText(getApplicationContext(), "Skriv inn brukernavn og passord!", Toast.LENGTH_LONG).show();
        }

    }





    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String password = params[1];
            String data="";
            int tmp;
            HttpURLConnection httpURLConnection = null;
            InputStream is = null;

            try {

                URL url = new URL("http://mobapp.ncs.no/login.php");
                String urlParams = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                urlParams += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
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

                    Intent i = new Intent(context, EventTabsActivity.class);

                    i.putExtra("userName", NAME);
                    i.putExtra("userPass", PASSWORD);
                    i.putExtra("err", err);
                    i.putExtra("userID", USERID);

                    startActivity(i);
                    finish(); //dreper main-activity

                } else {
                    String errorMsg = user_data.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                String errorMsg = "onPostExecution Exception: " + e.getMessage();
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }catch (NullPointerException e){
                e.printStackTrace();
                err = "Nullpointerexception: " + e.getMessage();
            }

        }
    }


}
