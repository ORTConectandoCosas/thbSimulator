package uy.edu.ort.cc;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ArduinoSketch{
    private String thbServer = "tcp://demo.thingsboard.io:1883";
    private String publishTopic = "v1/devices/me/telemetry";
    private String clientId = "ASIMLATOR";
    private String deviceToken = "30Y3Hf2lC3N43UqWusNC";
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttConnectOptions connectOptions = new MqttConnectOptions();

    private MqttClient thbMqttClient = null;

    public void setUp() {
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

    public void loop() {

        try {
            String jsonString = "{\"temperature\" : 25.5,\"humidity\" : 69}";

            MqttMessage msg = new MqttMessage(jsonString.getBytes());
            msg.setQos(0);
            thbMqttClient.publish(publishTopic, msg);
        } catch (MqttException e) {
            System.out.println("Mqtt exception" + e.getMessage());
        }

    }

}
