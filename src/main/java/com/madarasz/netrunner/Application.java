package com.madarasz.netrunner;

import com.madarasz.netrunner.brokers.NetrunnerDBBroker;

/**
 * Created by jenkins on 05/06/15.
 */
public class Application {

    public static void main(String[] args) {
        System.out.println(NetrunnerDBBroker.readSets());
    }
}
