package no.nc_spectrum.hendelseapplication;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Amir on 13.03.2017.
 */

public class HendelseRegister extends TreeMap<Integer, Hendelse> {

    //legger til et event-objekt i lista
   // @Override
    public boolean put(Object objekt)
    {
        if(objekt instanceof Hendelse)
        {
            if(!exists(((Hendelse)objekt).getCid())) // TODO: bruker man cid?
            {
                put(((Hendelse) objekt).getCid(),(Hendelse)objekt);
                return true;
            }
        }
        return false;
    }

    //sjekker om innkommende parameter allerede eksisterer som key-verdi
    //@Override
    public boolean exists(int cid)
    {
        return containsKey(cid);
    }


    //fjerner et objekt i fra lista, hvis det finnes
    //@Override
    public boolean remove(int cid)
    {
        if(exists(cid))
        {
            remove(cid);
            return true;
        }
        return false;
    }

    //@Override
    public Object getObject(int cid)
    {
        if(exists(cid))
        {
            return get(cid);
        }
        return null;
    }

    //henter ut hendelse ved hjelp av nøkkel-verdi
    public Hendelse getEvent(int cid)
    {
        if(exists(cid))
        {
            return get(cid);
        }
        return null;
    }

   // @Override
    public Map getMap()
    {
        return this;
    }

}
