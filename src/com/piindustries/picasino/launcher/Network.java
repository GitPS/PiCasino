package com.piindustries.picasino.launcher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by phil on 12/4/13.
 */
public class Network {
    static final int port = 65001;

    static public void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(User.class);
        kryo.register(Update.class);
    }
}
