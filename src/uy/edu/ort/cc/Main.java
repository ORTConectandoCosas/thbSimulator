package uy.edu.ort.cc;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    private boolean connected = false;

    public static void main(String[] args) {
        try {
            ArduinoSketch anExampleScketch = new ArduinoSketch();
            anExampleScketch.setUp();
            anExampleScketch.loop();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}
