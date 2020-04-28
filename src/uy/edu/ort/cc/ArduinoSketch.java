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
    private String requestTopic = "v1/devices/me/rpc/request/+";

    private String clientId = "ASIMLATOR";
    private String deviceToken = "30Y3Hf2lC3N43UqWusNC";

    //mqtt connection parameters
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttConnectOptions connectOptions = new MqttConnectOptions();
    private MqttClient thbMqttClient = null;

    /*
        Sensor declaration
     */

    private DTHSensor temperatureSensor = new DTHSensor();
    private ServoMotor servo = new ServoMotor();


    // Arduino Setup
    public void setUp() {

        //Connect to Thingsboard server
        connectOptions.setUserName(deviceToken);
        connectOptions.setMaxInflight(200);
        try {
            // create Mattclient
            thbMqttClient = new MqttClient(thbServer, clientId, persistence);
            connectOptions.setMaxInflight(200);

            //connectOptions.setCleanSession(true);
            thbMqttClient.connect(connectOptions);

            //suscribe to receive commands
            thbMqttClient.subscribe(requestTopic);

            // Register callback to call when a command arrives using requestTopic
            thbMqttClient.setCallback(on_message);


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
                //System.out.println("Publish to server" + jsonString);

                // simulate a delay()
                TimeUnit.MILLISECONDS.sleep(2000);

            } catch (MqttException e) {
                System.out.println("Mqtt exception" + e.getMessage());

            }
            catch (InterruptedException e) {
                System.out.println("Interrupt exception" + e.getMessage());
            }
        }

    }

    /*
        Callback to process commands sent from thingsboard server
     */
    MqttCallback on_message = new MqttCallback(){
        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            JsonParser parser = new JsonParser();
            String message = mqttMessage.toString();

            JsonElement jsonMessage = parser.parse(message);
            JsonObject jsonObject = jsonMessage.getAsJsonObject();
            JsonElement methodValue = jsonObject.get("method");
            JsonElement paramsValue = jsonObject.get("params");

            servo.write(paramsValue.getAsFloat());

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    };

}
