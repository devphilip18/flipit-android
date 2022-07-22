package com.devphilip18.flipit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


public class MainActivity extends Activity implements SensorEventListener {


    private AudioManager myAudioManager;
    private final float[] gravityReading = new float[3];
    private final float[] linearAccelReading = new float[3];
    private SensorManager sensorManager;
    private NotificationManager notificationManager;
    private float gravity,linear;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ringButton = findViewById(R.id.ring);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        notificationManager  = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);




        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_title)
                    .setTitle(R.string.dialog_message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    System.exit(0);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        ringButton.setOnClickListener(v -> {
            //if((notificationManager.getCurrentInterruptionFilter() != NotificationManager.INTERRUPTION_FILTER_ALL)) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.i("Ringer", " SET");
            onResume();

        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //Log.i("Gravity", String.valueOf(sensor));
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor,
                    1000000, 5);
        }
        Sensor linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //Log.i("Linear", String.valueOf(linearAccelSensor));
        if (gravitySensor != null) {
            sensorManager.registerListener(this, linearAccelSensor,
                    1000000, 5);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }
    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            System.arraycopy(event.values, 0, gravityReading,
                    0, gravityReading.length);
            float zaxisGravity = gravityReading[2];
            gravity = zaxisGravity;

            Log.i("Gravity", String.valueOf(zaxisGravity));
        }
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            //if( zGravity < 0 ) updateSilentMode();


            System.arraycopy(event.values, 0, linearAccelReading,
                    0, linearAccelReading.length);
            float zaxisLinear = linearAccelReading[2];
            linear = zaxisLinear;

            Log.i("Linear", String.valueOf(zaxisLinear));
        }

            //if( zLinear < 0 ) updateSilentMode();


            if (gravity < 0 && linear < 0) updateSilentMode();


    }

    private void updateSilentMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // if (!notificationManager.isNotificationPolicyAccessGranted()) {
            //     Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            //     startActivity(intent);
            // }
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            //myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);


            Log.i("Mode", "Silent");
            onPause();
        }
    }




}
