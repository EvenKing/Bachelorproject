package no.nc_spectrum.hendelseapplication;

/**
 * Created by Amir on 13.03.2017.
 */

public class Hendelse {

    private int cid;
    private int sid;
    private String signature;
    //TODO: private Date timestamp ?
    private int priority;
    private int src_ip;
    private int dst_ip;
    private int src_port;
    private int dst_port;
    //TODO: private String infoText ?

    // TODO: Modify constructor
    public Hendelse(int cid, int sid, String signature, int priority, int src_ip, int dst_ip, int src_port, int dst_port)
    {
        this.cid = cid;
        this.sid = sid;
        this.signature = signature;
        this.priority = priority;
        this.src_ip = src_ip;
        this.dst_ip = dst_ip;
        this.src_port = src_port;
        this.dst_port = dst_port;
    }


    // get-methods:

    public int getSid() {
        return sid;
    }

    public int getCid() {
        return cid;
    }

    public String getSignature() {
        return signature;
    }

    public int getPriority() {
        return priority;
    }

    public int getSrc_ip() {
        return src_ip;
    }

    public int getDst_ip() {
        return dst_ip;
    }

    public int getSrc_port() {
        return src_port;
    }

    public int getDst_port() {
        return dst_port;
    }
}
