package com.vaavud.vaavudSDK.core.mjolnir;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.vaavud.vaavudSDK.core.model.MagneticFieldPoint;

public class MagneticFieldSensorManager implements SensorEventListener {

    private Context mainContext;
    private SensorManager mSensorManager;
    private Sensor mMagmeter;
    private long startTime;
    private MagneticDataManager myDataManager;

    public MagneticFieldSensorManager(Context _mainContext, MagneticDataManager _myDataManager) {
        this.mainContext = _mainContext;
        this.myDataManager = _myDataManager;
        initializeMagmeter();
    }

    private boolean initializeMagmeter() {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) mainContext.getSystemService(Context.SENSOR_SERVICE);

            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                // Success! There's a magnetometer.
                mMagmeter = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                return true;
            } else {
                // Danm! No magnetometer.
                //Log.e("MagneticFieldSensorManager", "No magnetic field sensor detected on the device");
                return false;
            }
        }

        return true;
    }

    public void startLogging() {
        mSensorManager.registerListener(this, mMagmeter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopLogging() {
//        Log.d("MagneticFieldManager","Unregister");
        mSensorManager.unregisterListener(this,mMagmeter);
    }

    public String getMagneticFieldSensorName() {
        if (mMagmeter != null) {
            return mMagmeter.getName();
        }
        return null;
    }

    public void clear() {
        startTime = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (startTime == 0) {
            this.startTime = event.timestamp;
        }

        float time = (float) (event.timestamp - startTime) / 1000000000;

        Float[] magneticfieldReading = new Float[]{event.values[0], event.values[1], event.values[2]};
//        Log.d("MagnetifFieldSensor",time+" "+magneticfieldReading);
        myDataManager.addMagneticFieldReading(new MagneticFieldPoint((long) time, magneticfieldReading));
    }
}
