package no.nc_spectrum.hendelseapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Bruker on 02.05.2017.
 */

public class RestartUpdateChecker extends BroadcastReceiver{        //broadcastreceiver used for when app is closed/destroyed
    String uid, cid;                //variables to hold user-ID and event-ID to send to service-class
    @Override
    public void onReceive(Context context, Intent intent) {     //function called when it receives a broadcast
        uid = intent.getStringExtra("userid");                  //stores the values from the intent sent
        cid = intent.getStringExtra("cid");
        Intent i = new Intent(context, UpdateCheck.class);      //creates new Intent for service-class
        i.putExtra("userid", uid);                              //stores the values in the event
        i.putExtra("cid", cid);
        context.startService(i);                                //starts the service with the intent created
    }
}
