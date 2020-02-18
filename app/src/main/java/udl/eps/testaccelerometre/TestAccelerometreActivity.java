package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private TextView view;
    private TextView viewLlum;
    private TextView viewDades;
    private Sensor acc;
    private Sensor llum;
    private long lastUpdate;
    private long lastUpdateLight;
    private float lastValue;




    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);

        viewLlum = findViewById(R.id.textView3);

        viewDades = findViewById(R.id.textView2);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null)
            registerSensors();


        lastUpdate = System.currentTimeMillis();
        lastUpdateLight = System.currentTimeMillis();

    }

    private void registerSensors(){
        if((acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null){
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
            String value = "\n";
            value += "Name: " + acc.getName() + "\n";
            value += "Power: " + acc.getPower() + "\n";
            value += "MinDelay: " + acc.getMinDelay() + "\n";
            value += "MaxDelay: " + acc.getMaxDelay() + "\n";
            value += "Maximum Range: " + acc.getMaximumRange() + "\n";
            value += "Resolution: " + acc.getResolution() + "\n";
            value += "Version: " + acc.getVersion() + "\n";

            viewDades.setText(value);

        } else viewDades.setText(R.string.noAccel);

        if((llum = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)) != null){
            sensorManager.registerListener(this, llum, SensorManager.SENSOR_DELAY_NORMAL);
            String value = "\n";
            value += "Name: " + llum.getName() + "\n";
            value += "Maximum Range: " +  llum.getMaximumRange() + "LX" + "\n";

            viewDades.setText(viewDades.getText().toString()+value);
        } else viewDades.setText(viewDades.toString()+R.string.noLlum);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            getAccelerometer(event);

        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            getLight(event);
        }
    }

    private void getLight(SensorEvent event) {
        float value = event.values[0];
        long actualTime = System.currentTimeMillis();
        String dades = "";

        if (actualTime - lastUpdateLight < 200) return;
        else if(value == lastValue) return;

        lastValue = value;
        lastUpdateLight = actualTime;

        dades += "New value light sensor = " + value;

        if (value <= 15000) dades += "  LOW";
        else if (value >= 25000) dades += " HIGH";
        else dades += " MEDIUM";

        viewLlum.setText(viewLlum.getText().toString()+dades+"\n");

    }


    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2)
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
            if (color) {
                view.setBackgroundColor(Color.GREEN);

            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensors();
    }
}