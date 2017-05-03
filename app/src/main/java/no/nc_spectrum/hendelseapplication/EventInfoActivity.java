package no.nc_spectrum.hendelseapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class EventInfoActivity extends AppCompatActivity {

    private HashMap<String, Object> event;

    private TextView tv_signature, tv_sid, tv_cid, tv_timestamp, tv_risk, tv_srcip, tv_dstip, tv_srcport, tv_dstport, tv_infotext, tv_hostname;

    private ImageView iv_priorityicon;

    static boolean isRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.eventinfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //TODO: Add this?
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_sid = (TextView) findViewById(R.id.tv_sid);
        tv_cid = (TextView) findViewById(R.id.tv_cid);
        tv_timestamp = (TextView) findViewById(R.id.tv_timestamp);
        tv_risk = (TextView) findViewById(R.id.tv_priority);
        tv_srcip = (TextView) findViewById(R.id.tv_srcip);
        tv_dstip = (TextView) findViewById(R.id.tv_dstip);
        tv_srcport = (TextView) findViewById(R.id.tv_srcport);
        tv_dstport = (TextView) findViewById(R.id.tv_dstport);
        tv_infotext = (TextView) findViewById(R.id.tv_infotext);
        tv_hostname = (TextView) findViewById(R.id.tv_hostname);

        iv_priorityicon = (ImageView) findViewById(R.id.iv_info_smiley);

        Intent intent = getIntent();

        event = (HashMap<String, Object>) intent.getSerializableExtra("event");


        tv_signature.setText(event.get("signature").toString());
        tv_sid.setText(event.get("sid").toString());
        tv_cid.setText(event.get("cid").toString());
        tv_timestamp.setText(event.get("timestamp").toString());
        tv_risk.setText(event.get("priority").toString());
        tv_srcip.setText(event.get("src_ip").toString());
        tv_dstip.setText(event.get("dst_ip").toString());
        tv_srcport.setText(event.get("src_port").toString());
        tv_dstport.setText(event.get("dst_port").toString());
        tv_infotext.setText(event.get("info_text").toString());
        tv_hostname.setText(event.get("hostname").toString());

        iv_priorityicon.setImageResource((int) event.get("priority_icon"));

        isRunning = true;

    }

    protected void onResume(){
        super.onResume();
        UpdateCheck.nullify(getApplicationContext());
        isRunning = true;
    }

    protected void onStop(){
        super.onStop();
        isRunning = false;
    }

    protected void onDestroy(){
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eventinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            UpdateCheck.loggedIn = false;
            startActivity(intent);
            finish();
            return true;
        }
        else if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
