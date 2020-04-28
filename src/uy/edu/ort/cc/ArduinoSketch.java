package uy.edu.ort.cc;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ArduinoSketch{
    private String thbServer = "tcp://demo.thingsboard.io:1883";
    private String clientId = "ASIMLATOR";
    private String token = "30Y3Hf2lC3N43UqWusNC";
    private MemoryPersistence persistence = new MemoryPersistence();

    private MqttClient thbMqttClient = null;

    public void setUp() {
        try {
            thbMqttClient = new MqttClient(thbServer, clientId, persistence);

        } catch (MqttException e) {
            System.out.println("Mqtt exception" + e.getMessage());
        }

    }

    public void loop() {

    }

}
