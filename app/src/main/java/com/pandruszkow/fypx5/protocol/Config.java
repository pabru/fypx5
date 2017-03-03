package com.pandruszkow.fypx5.protocol;

import com.peak.salut.SalutServiceData;

import java.util.Random;

/**
 * Created by piotrek on 02/03/17.
 */

public class Config {
    public final static String applicationName = "fypx5";
    public final static int portNumber = 5012;
    public final static String peerId = "peer"+new Random().nextInt(100); //to keep the peer Id fairly short. Android NSD name length restriction.
    public final static SalutServiceData salutServiceData = new SalutServiceData(applicationName, portNumber, peerId);
}
