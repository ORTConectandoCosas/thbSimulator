package uy.edu.ort.cc;

import java.util.Random;

public class DHTSensor{

    public float readHumidity(){
        Random r = new Random();
        float low = 40;
        float high = 100;
        float result = low + r.nextFloat() * (high - low);
        return result;
    }

    public float readTemperature(){
        Random r = new Random();
        float low = 0;
        float high = 90;
        float result = low + r.nextFloat() * (high - low);
        return result;
    }
}
