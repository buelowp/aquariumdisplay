package com.home.pete.aquarium;

public class Constants {
    public static final String MQTT_BROKER_URL = "tcp://172.24.1.13:1883";
    public static final String CLIENT_ID = "rpiandroid";
    public static final String TOPIC = "aquarium/#";
    public static long VIEW_TIMEOUT = 1000 * 60 * 2;     // two minutes

    public static final String TEMPERATURE_TOPIC = "aquarium/temperature";
    public static final String WATERLEVEL_TOPIC = "aquarium/waterlevel";
    public static final String CONTROLS_TOPIC = "aquarium/controls";
    public static final String CONTROL_TOPIC = "aquarium/control";
    public static final String DATABASE_TOPIC = "aquarium/database";
}
