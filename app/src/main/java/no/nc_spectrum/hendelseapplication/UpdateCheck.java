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
import android.widget.CheckBox;
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

import no.nc_spectrum.hendelseapplication.SettingsActivity;

/**
 * Created by Bruker on 02.05.2017.
 */

public class UpdateCheck extends Service{               //service to continuously run in the background
    int counter = 0;                                        //this variable is actually quite redundant, used for testing
    static int eventlength = 0, notificationCounter = 0;        //holds amount of new events and notifications on screen
    static final int UPDATE_INTERVAL = 20000; //TODO: Change time interval between checks here  here we define time interval for checks
    private Timer timer = new Timer();                          //the timer-object to initialize the task-interval
    private String priorityLvl = "1"; //TODO: Change priority level for notifications here!     priority of event to be found
    String uid, cid, cid2, sid = "", signa = "", timestamp = "";      //these variables hold information on events and the user
    Context nctx;                       //context, created here in order to use for onDestroy()-function
    static boolean loggedIn;            //variable for whether or not to restart this service when app is destroyed/closed
    private String updateURL = "http://mobapp.ncs.no/updated.php";   //variable holds address to php-file to check for updates

    /*TODO
    * Sørg for at all connection er gjort på en kryptert og sikker måte!*/

    @Override
    public IBinder onBind(Intent arg0){         //Ibinder, required for service-classes. Unused in this project

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){   //initialized on first call

        Log.i(UpdateCheck.class.getSimpleName(), "Service started!!!");     //used for testing

        uid = intent.getStringExtra("userid");                  //variable to store ID of user
        cid = intent.getStringExtra("cid");                     //variable to store ID of latest event
        loggedIn = intent.getBooleanExtra("loggedin", true);    //initializes variable to say user is logged in to app
        nctx = this.getApplicationContext();                    //takes in application contezt

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {                     //scheduler to run a task on a set interval
                if(!isAppRunning()) {                                       //checks if app is not logged into or on any page
                    Log.d("UpdateCheck ", String.valueOf(++counter));           //used for testing
                    Log.i(UpdateCheck.class.getSimpleName(), "Looking for updates!");   //used for testing
                    new DoBackgroundTask().execute(uid, cid);                   //starts a new process to be done in background
                }
                else if(isAppOpen(nctx)){                               //if app is open
                    Log.i(UpdateCheck.class.getSimpleName(), "App is open/running");    //used for testing
                    nullify(nctx);                                      //resets all counters for events and notifications
                }
            }
        }, 0, UPDATE_INTERVAL);                 //the interval set

        return START_STICKY;                    //used to get service to start up after being stopped
    }

    private static boolean isAppOpen(Context context) {         //function to see if app is open and in use
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        return (services.get(0).topActivity.getPackageName().equalsIgnoreCase(context.getPackageName()));
    }

    public boolean isAppRunning(){                  //function to see if app is open on any page or login-screen
        return (EventInfoActivity.isRunning || EventTabsActivity.isRunning || MainActivity.isRunning || SettingsActivity.isRunning);
    }

    public static void nullify(Context c){  //function used to remove active notifications and reset counter for them and events
        cancelNotifications(c);             //calls function to remove all active notifications
        eventlength = 0;
        notificationCounter = 0;
    }

    public void sendNotification(){         //function to send notification to user
        //creates the notification with the notification-icon in the task-bar
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon);
        nctx = this.getApplicationContext();        //gets the application context

        if(eventlength > 1 || notificationCounter > 1){  //if there is already a notification active, or more than one new event
            cancelNotifications(nctx);                  //removes all other notifications
                                        //sets title and content of new notification
            mBuilder.setContentTitle(getString(R.string.notification_title_multiple)).setContentText(eventlength + " " + getString(R.string.notification_text));
        }else{                          //if there are no notifications active, and only one new event
            ++notificationCounter;      //updates counter for notifications
                                        //sets title and content for the notification to show that specific event
            mBuilder.setContentTitle(getString(R.string.notification_title_single)).setContentText(getString(R.string.sid_hint) + sid + ", " + getString(R.string.cid_hint) + cid + ", "
                    + getString(R.string.timestamp_hint) + timestamp);
        }

        //sets it so when user taps on notification they're sent to the login-screen:
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("userID", uid);                           //er denne nødvendig?
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);      //was used to keep more notifications on screen
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i(UpdateCheck.class.getSimpleName(), eventlength + " updates!");    //used for testing

        mNotificationManager.notify(counter++, mBuilder.build());       //sends the notification out to user
    }

    public static void cancelNotifications(Context ctx) {       //function to remove all active notifications
        //creates object to hold notifications:
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();                                           //cancels all notifications in object
    }

    private class DoBackgroundTask extends AsyncTask<String, String, String> {      //function to work in background
        protected String doInBackground(String... params) {
            String uID = params[0];                         //these store user-ID, and event-ID from latest event
            String cID = params[1];
            String data = "";                       //used to store return-value from server
            int tmp;                                //used for traversing the response from server

            try{
                URL url = new URL(updateURL);      //URL to connect to file-server which holds our php-files
                                                    //String used to hold parameters to send to php-file
                String urlParams = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(uID, "UTF-8");
                urlParams += "&" + URLEncoder.encode("cid", "UTF-8") + "=" + URLEncoder.encode(cID, "UTF-8");
                urlParams += "&" + URLEncoder.encode("priority", "UTF-8") + "=" + URLEncoder.encode(priorityLvl, "UTF-8");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();     //opens connection to server
                httpURLConnection.setDoOutput(true);                        //sets it so we send to the server
                OutputStream os = httpURLConnection.getOutputStream();      //opens connection to send to server
                os.write(urlParams.getBytes());                             //sends to server in bytes
                os.flush();                                                 //cleans out the stream
                os.close();                                                 //closes the stream

                InputStream is = httpURLConnection.getInputStream();        //opens connection to receive from server
                while((tmp=is.read())!=-1){                                 //reads through the response while not ended
                    data+= (char)tmp;                                       //stores each character to create a String
                }

                is.close();                                                 //closes the stream
                httpURLConnection.disconnect();                             //disconnects the connection to the server

                return data;                                                //returns the string to next function
            }catch (MalformedURLException m) {                              //exception-handler for bad URL
                m.printStackTrace();
            }catch (IOException e){                                         //exception for IO
                e.printStackTrace();
            }

            return data;                                                    //if it failed, returns an empty string
        }

        protected void onPostExecute(String s) {                //function to start after the previous function
            String err;                                    //variable in case of error
            try {
                JSONObject root = new JSONObject(s);             //creates JSONobject with the string-return from doInBackground
                //creates open object to take in the object from the JSONobject above, used to see if it is an object or an array
                Object objektet = new JSONTokener(root.getString("oppdatert")).nextValue();

                JSONArray noenoe = null;
                JSONObject uoppdatert;

                if(objektet instanceof JSONArray) {         //if the object is a JSON-array, means it found update(s)
                    noenoe = (JSONArray) objektet;          //creates the JSON array
                }else if(objektet instanceof JSONObject) {  //if it's a JSON object, means it didn't find any updates
                    uoppdatert = (JSONObject) objektet;
                    if(uoppdatert.getString("error") == "TRUE"){    //if error is stored as TRUE
                        Log.i(UpdateCheck.class.getSimpleName(), "En error oppstod med henting fra database"); //logs message
                    }
                    return;         //nothing further to do
                }else {             //this is shown if something is wrong and it couldn't find any response from the server:
                    Toast.makeText(getApplicationContext(), "Var hverken array eller objekt!", Toast.LENGTH_LONG).show();
                }

                if(noenoe != null) {                //if the JSON-array exists
                    JSONObject oppdatert = noenoe.getJSONObject(0);     //creates JSON-object from event in front of the array
                    cid = oppdatert.getString("cid");                   //stores the ID of that event
                    if (noenoe.length() < 3) {     //if the array contains less than 3 objects (2)
                        sid = oppdatert.getString("sid");                   //stores the info about the new event
                        signa = oppdatert.getString("signature");
                        timestamp = oppdatert.getString("timestamp");
                        ++eventlength;                                      //updates the counter for amount of new events
                        Log.i(UpdateCheck.class.getSimpleName(), "Fant 1 update");  //used for testing

                    } else {        //if the array is longer than 2 elements
                        Log.i(UpdateCheck.class.getSimpleName(), "Fant " + (noenoe.length()-1) + " updates!");  //used for testing
                        eventlength += noenoe.length() - 1;                 //adds to the counter for amount of new events
                    }
                    oppdatert = noenoe.getJSONObject(noenoe.length()-1);
                    cid2 = oppdatert.getString("cid");
                    sendNotification();                     //calls on the function to send a new notification
                }

            } catch (JSONException e) {                     //to catch exception if response from doInBackground is not JSON
                e.printStackTrace();
                String errorMsg = "JSONException: " + e.getMessage();
                //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                Log.e("Error:", errorMsg);              //used for testing
            }catch (NullPointerException e){            //used to catch nullpointerexception
                e.printStackTrace();
                err = "Nullpointerexception: " + e.getMessage();
                Log.d("error:", err);                   //used for testing
            }

        }
    }
    @Override
    public void onDestroy() {       //function called when service is stopped/destroyed in order to restart service
        super.onDestroy();          //calls construction to superclass for onDestroy
        if(loggedIn) {                          //if user has not logged out of app
            int tmp = Integer.parseInt(cid);        //stores ID for latest event as int
            tmp = tmp - eventlength;                //subtracts to get ID from first new event found since new checks
            cid = Integer.toString(tmp);            //stores ID as string again
                            //creates intent to be used for broadcast in the app
            Intent broadcastIntent = new Intent("nc_spectrum.hendelsesapplication.no.ActivityRecognition.RestartCheck");
            broadcastIntent.putExtra("userid", uid);    //stores user-ID and event-ID in intent to be sent
            broadcastIntent.putExtra("cid", cid);
            sendBroadcast(broadcastIntent);         //broadcasts intent ot be picked up by the broadcastreceiver
        }
        //if user logged out, then the service is not restarted and broadcast not sent
        if(timer != null) timer.cancel();           //stops timer
    }

}
