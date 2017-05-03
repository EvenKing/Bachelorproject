package no.nc_spectrum.hendelseapplication;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bruker on 02.05.2017.
 */

public class UpdateCheck extends Service{
    int counter = 0;
    static int eventlength = 0, notificationCounter = 0;
    static final int UPDATE_INTERVAL = 60000; //TODO: Change time interval between checks here
    private Timer timer = new Timer();
    private String priorityLvl = "1"; //TODO: Change priority level for notifications here!
    String uid, cid, sid = "", signa = "", timestamp = "";
    Context nctx;
    static boolean loggedIn;

    /*TODO
    * Sørg for at all connection er gjort på en kryptert og sikker måte!*/

    @Override
    public IBinder onBind(Intent arg0){

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.i(UpdateCheck.class.getSimpleName(), "Service started!!!");

        uid = intent.getStringExtra("userid");
        cid = intent.getStringExtra("cid");
        loggedIn = intent.getBooleanExtra("loggedin", true);
        nctx = this.getApplicationContext();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if(!isAppRunning()) {
                    Log.d("UpdateCheck ", String.valueOf(++counter));
                    Log.i(UpdateCheck.class.getSimpleName(), "Looking for updates!");
                    new DoBackgroundTask().execute(uid, cid);
                }
                else if(isAppOpen(nctx)){
                    Log.i(UpdateCheck.class.getSimpleName(), "App is open/running");
                    nullify(nctx);
                }
            }
        }, 0, UPDATE_INTERVAL);

        //new DoBackgroundTask().execute(uid, cid);

        return START_STICKY;
    }

    private static boolean isAppOpen(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
            return true;
        }else {
            return false;
        }
    }

    public boolean isAppRunning(){
        if(EventInfoActivity.isRunning == true || EventTabsActivity.isRunning == true || MainActivity.isRunning == true || SettingsActivity.isRunning == true){
            return true;
        }else{
            return false;
        }
    }

    public static void nullify(Context c){
        cancelNotifications(c);
        eventlength = 0;
        notificationCounter = 0;
    }

    public void sendNotification(){

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification_icon);

        nctx = this.getApplicationContext();

        /*if(isAppOpen(nctx)) {
            cancelNotifications(nctx);
            eventlength = 0;
            notificationCounter = 0;
            return;
        }else */
        if(eventlength > 0 || notificationCounter > 3){
            cancelNotifications(nctx);
            mBuilder.setContentTitle(getString(R.string.notification_title_multiple)).setContentText(eventlength + getString(R.string.notification_text));
        }else{
            ++notificationCounter;
            mBuilder.setContentTitle(getString(R.string.notification_title_single)).setContentText(getString(R.string.sid_hint) + sid + ", " + getString(R.string.cid_hint) + cid + ", " + getString(R.string.timestamp_hint) + timestamp);
        }

        /*TODO
        * Forandre denne her til å vise til riktig siden av appen!*/
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("userID", uid);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(counter++, mBuilder.build());
    }

    public static void cancelNotifications(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    private class DoBackgroundTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            String uID = params[0];
            String cID = params[1];
            String data = "";
            int tmp;

            try{
                URL url = new URL("http://mobapp.ncs.no/updated.php");
                String urlParams = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(uID, "UTF-8");
                urlParams += "&" + URLEncoder.encode("cid", "UTF-8") + "=" + URLEncoder.encode(cID, "UTF-8");
                urlParams += "&" + URLEncoder.encode("priority", "UTF-8") + "=" + URLEncoder.encode(priorityLvl, "UTF-8"); //TODO: change priority number for desired notfications

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            }catch (MalformedURLException m) {
                m.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            return data;
        }

        protected void onPostExecute(String s) {
            String err=null;
            try {
                JSONObject root = new JSONObject(s);
                Object objektet = new JSONTokener(root.getString("oppdatert")).nextValue();

                JSONArray noenoe = null;
                JSONObject uforandret = null;

                if(objektet instanceof JSONArray) {
                    noenoe = (JSONArray) objektet;
                }else if(objektet instanceof JSONObject) {
                    uforandret = root.getJSONObject("oppdatert");
                }else {
                    Toast.makeText(getApplicationContext(), "Var hverken array eller objekt!", Toast.LENGTH_LONG).show();
                }

                if(noenoe != null) {
                    if (noenoe.length() < 6) {
                        for(int i=noenoe.length()-1;i>=0;i--) {
                            JSONObject oppdatert = noenoe.getJSONObject(i);
                            sid = oppdatert.getString("sid");
                            cid = oppdatert.getString("cid");
                            signa = oppdatert.getString("signature");
                            timestamp = oppdatert.getString("timestamp");
                            eventlength++;
                            sendNotification();
                        }
                    } else {
                        eventlength = noenoe.length() - 1;
                        JSONObject oppdatert = noenoe.getJSONObject(0);
                        cid = oppdatert.getString("cid");
                        sendNotification();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                String errorMsg = "MyService.onPostExecution Exception: " + e.getMessage();
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                Log.e("Error:", errorMsg);
            }catch (NullPointerException e){
                e.printStackTrace();
                err = "Nullpointerexception: " + e.getMessage();
                Log.d("error:", err);
            }

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loggedIn) {
            int tmp = Integer.parseInt(cid);
            tmp = tmp - eventlength;
            cid = Integer.toString(tmp);
            Intent broadcastIntent = new Intent("nc_spectrum.hendelsesapplication.no.ActivityRecognition.RestartCheck");
            broadcastIntent.putExtra("userid", uid);
            broadcastIntent.putExtra("cid", cid);
            sendBroadcast(broadcastIntent);
        }
        if(timer != null) timer.cancel();
    }

}
