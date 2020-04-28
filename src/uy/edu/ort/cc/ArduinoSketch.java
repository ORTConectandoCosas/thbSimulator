package uy.edu.ort.cc;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.*;

import java.util.concurrent.TimeUnit;

public class ArduinoSketch{
    /*
       ThingsBoard parameters for MQTT
     */
    private String thbServer = "tcp://demo.thingsboard.io:1883";
    private String publishTopic = "v1/devices/me/telemetry";
    private String clientId = "ASIMLATOR";
    private String deviceToken = "30Y3Hf2lC3N43UqWusNC";
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttConnectOptions connectOptions = new MqttConnectOptions();
    private MqttClient thbMqttClient = null;

    /*
        Sensor declaration
     */

    private DTHSensor temperatureSensor = new DTHSensor();

    // Arduino Setup

    public void setUp() {

        //Connect to Thingsboar server
        connectOptions.setUserName(deviceToken);
        connectOptions.setMaxInflight(200);
        try {
            thbMqttClient = new MqttClient(thbServer, clientId, persistence);
            connectOptions.setMaxInflight(200);
            //connectOptions.setCleanSession(true);
            thbMqttClient.connect(connectOptions);

            System.out.println("Connecting to server");
        } catch (MqttException e) {
            System.out.println("Mqtt exception" + e.getMessage());
        }

    }

    // Arduino Loop
    public void loop() {
        while(true) {

            try {

                //Read sensor
                float temperatureRead = temperatureSensor.readTemperature();
                float humidityRead = temperatureSensor.readHumidity();

                //Build json
                String jsonString = "{\"temperature\" :" + Float.toString(temperatureRead) + "," + "\"humidity\":" + Float.toString(humidityRead) + "}";

                //publish to telemetry topic
                MqttMessage msg = new MqttMessage(jsonString.getBytes());
                msg.setQos(0);
                thbMqttClient.publish(publishTopic, msg);
                System.out.println("Publish to server" + jsonString);

                // simulate a delay()
                TimeUnit.MILLISECONDS.sleep(6000);

            } catch (MqttException e) {
                System.out.println("Mqtt exception" + e.getMessage());

            }
            catch (InterruptedException e) {
                System.out.println("Interrupt exception" + e.getMessage());
            }
        }

    }

}
