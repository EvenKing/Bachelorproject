package no.nc_spectrum.hendelseapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Amir on 04.04.2017.
 */

public class EventJSONParser {

    public List<HashMap<String, Object>> parse(JSONObject jObject) {

        JSONArray jEvents = null;
        try {
            jEvents = jObject.getJSONArray("alle_hendelser");
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return getEvents(jEvents);
    }

    private List<HashMap<String, Object>> getEvents(JSONArray jEvents) {
        int eventCount = jEvents.length();
        List<HashMap<String, Object>> eventList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> event = null;

        for (int i = 0; i < eventCount; i++) {
            try {
                event = getEvent((JSONObject) jEvents.get(i));
                eventList.add(event);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return eventList;
    }

    private HashMap<String, Object> getEvent(JSONObject jEvent) {

        HashMap<String, Object> event = new HashMap<String, Object>();

        String sid = "";
        String cid = "";
        String signature = "";
        String timestamp = "";
        String priority = "";
        String src_ip = "";
        String dst_ip = "";
        String src_port = "";
        String dst_port = "";
        String hostname = "";
        String info_text = "";


        try {
            sid = jEvent.getString("sid");
            cid = jEvent.getString("cid");
            signature = jEvent.getString("signature");
            timestamp = jEvent.getString("timestamp");
            priority = jEvent.getString("priority");
            src_ip = jEvent.getString("src_ip");
            dst_ip = jEvent.getString("dst_ip");
            src_port = jEvent.getString("src_port");
            dst_port = jEvent.getString("dst_port");
            hostname = jEvent.getString("hostname");
            info_text = jEvent.toString();


            event.put("sid", sid);
            event.put("cid", cid);
            event.put("signature", signature);
            event.put("timestamp", timestamp);
            event.put("src_ip", src_ip);
            event.put("dst_ip", dst_ip);
            event.put("src_port", src_port);
            event.put("dst_port", dst_port);
            event.put("info_text", info_text);
            event.put("hostname", hostname);

            event.put("priority", priority);
            switch (priority){ //TODO: Change this if priorities used change
                case "5" :
                    event.put("priority_icon", R.drawable.ic_happy_smiley);
                    break;
                case "6" :
                    event.put("priority_icon", R.drawable.ic_medium_smiley);
                    break;
                case "7" :
                    event.put("priority_icon", R.drawable.ic_sad_smiley);
                    break;
                default:
                    event.put("priority_icon", R.drawable.ic_default_smiley);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    private String cutString (String text )
    {
        if (text.length() > 28 )
        {
            text = text.substring(0, 24) + "...";
        }
        return text;
    }

}
