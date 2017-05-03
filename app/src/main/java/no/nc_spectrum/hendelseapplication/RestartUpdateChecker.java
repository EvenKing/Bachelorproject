package no.nc_spectrum.hendelseapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Bruker on 02.05.2017.
 */

public class RestartUpdateChecker extends BroadcastReceiver{
    String uid, cid;
    @Override
    public void onReceive(Context context, Intent intent) {
        uid = intent.getStringExtra("userid");
        cid = intent.getStringExtra("cid");
        //Log.i(RestartUpdateChecker.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        Intent i = new Intent(context, UpdateCheck.class);
        i.putExtra("userid", uid);
        i.putExtra("cid", cid);
        context.startService(i);
    }
}
