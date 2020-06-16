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
    private String telemetryTopic = "v1/devices/me/telemetry";
    private String requestTopic = "v1/devices/me/rpc/request/+";

    // New topics. Response used to send responses to request from server
    // attributes used to receive shared or client attributes form server
    private String responseTopic = "v1/devices/me/rpc/response/+"; // RPC responce
    private String attributesTopic = "v1/devices/me/attributes";  // Attributes


    private String clientId = "fe339e70-8fc0-11ea-beda-b3e525f9fbf0";
    private String deviceToken = "YtJVYrXCWXJsCrfTbN5l";

    //mqtt connection parameters
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttConnectOptions connectOptions = new MqttConnectOptions();
    private MqttClient thbMqttClient = null;

    /*
        Sensor declaration
     */

    private DHTSensor temperatureSensor = new DHTSensor();
    private ServoMotor servo = new ServoMotor();

    /*
    sleep value to simulate delay(sleepValue)
     */
    private int delayValue = 6000;

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

            //suscribe to receive commands mqttAsyncClient.subscribe(["/topic1", "/topic2", "/topic3"], [0,1,2]);
            thbMqttClient.subscribe(requestTopic, 0);

            //suscribe to attribuites topic
            thbMqttClient.subscribe(attributesTopic,0);

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
                System.out.println("=>Telemetria enviada: " + jsonString);

                //publish to telemetry topic
                MqttMessage msg = new MqttMessage(jsonString.getBytes());
                msg.setQos(0);
                thbMqttClient.publish(telemetryTopic, msg);


                // simulate a delay()
                TimeUnit.MILLISECONDS.sleep(delayValue);

            } catch (MqttException e) {
                System.out.println("Mqtt exception" + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Timer exception" + e.getMessage());
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
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            String message = mqttMessage.toString();
            // Check the topic to take action
            if (topic.contains(attributesTopic) == true) {
                // process attributes
                System.out.println("<= Payload Atributos: " + message);
                processAttributes(message);

            } else if (topic.contains(requestTopic.substring(0,26)) == true) {
                processRequest(message);
            } else {
                System.out.println("ERROR: message for another topic");
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    };

    private void processRequest(String message) {
        // this method it is used to process any request method from server
        JsonParser parser = new JsonParser();
        JsonElement jsonMessage = parser.parse(message);
        JsonObject jsonObject = jsonMessage.getAsJsonObject();
        JsonElement methodValue = jsonObject.get("method");
        JsonElement paramsValue = jsonObject.get("params");

        // {"method": valor, "params": valor}

        System.out.println("<= Payload recibido: " + jsonMessage.toString());
        System.out.println("<= Comando: " + methodValue.toString());
        System.out.println("<= Parametros comnado: " + paramsValue.toString());

        if(methodValue.equals("setValue"))
                servo.write(paramsValue.getAsFloat());

    }

    private void processAttributes(String message) {
        // this method it is used to process any request method from server
        JsonParser parser = new JsonParser();
        JsonElement jsonMessage = parser.parse(message);
        JsonObject jsonObject = jsonMessage.getAsJsonObject();
        JsonElement attributeValue = jsonObject.get("delayValue");


        // {"attribute": valor}

        System.out.println("<= Payload recibido: " + jsonMessage.toString());
        System.out.println("<= Attribute delayValue: " + attributeValue.toString());
        delayValue = attributeValue.getAsInt();

        //do whatever you want with the attribute, for the exmaple using delayValue to change loop frecuency


    }

}
