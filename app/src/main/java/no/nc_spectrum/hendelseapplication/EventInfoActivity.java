package no.nc_spectrum.hendelseapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class EventInfoActivity extends AppCompatActivity {

    private HashMap<String, Object> event;

    /*
    private String sid = "";
    private String cid = "";
    private String signature = "";
    private String timestamp = "";
    private String priority = "";
    private String src_ip = "";
    private String dst_ip = "";
    private String src_port = "";
    private String dst_port = "";
    private String info_text = "";
    */

    TextView tv_signature, tv_sid, tv_cid, tv_timestamp, tv_risk, tv_srcip, tv_dstip, tv_srcport, tv_dstport, tv_infotext;

    ImageView iv_priorityicon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_sid = (TextView) findViewById(R.id.tv_sid);
        tv_cid = (TextView) findViewById(R.id.tv_cid);
        tv_timestamp = (TextView) findViewById(R.id.tv_timestamp);
        tv_risk = (TextView) findViewById(R.id.tv_risk);
        tv_srcip = (TextView) findViewById(R.id.tv_srcip);
        tv_dstip = (TextView) findViewById(R.id.tv_dstip);
        tv_srcport = (TextView) findViewById(R.id.tv_srcport);
        tv_dstport = (TextView) findViewById(R.id.tv_dstport);
        tv_infotext = (TextView) findViewById(R.id.tv_infotext);

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

        iv_priorityicon.setImageResource((int) event.get("priority_icon"));

    }
}
