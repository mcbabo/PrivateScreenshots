/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.hardware.Sensor
 *  android.hardware.SensorEvent
 *  android.hardware.SensorEventListener
 *  android.hardware.SensorManager
 *  java.lang.Object
 */
package com.shamanland.privatescreenshots.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector
implements SensorEventListener {
    private int accelerationThreshold = 13;
    private Sensor accelerometer;
    private final Listener listener;
    private final SampleQueue queue = new SampleQueue();
    private SensorManager sensorManager;

    public ShakeDetector(Listener listener) {
        this.listener = listener;
    }

    private boolean isAccelerating(SensorEvent sensorEvent) {
        float f = sensorEvent.values[0];
        float f2 = sensorEvent.values[1];
        float f3 = sensorEvent.values[2];
        double d = f * f + f2 * f2 + f3 * f3;
        int n = this.accelerationThreshold;
        double d2 = d DCMPL (double)(n * n);
        boolean bl = false;
        if (d2 > 0) {
            bl = true;
        }
        return bl;
    }

    public void onAccuracyChanged(Sensor sensor, int n) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        boolean bl = this.isAccelerating(sensorEvent);
        long l = sensorEvent.timestamp;
        this.queue.add(l, bl);
        if (this.queue.isShaking()) {
            this.queue.clear();
            this.listener.hearShake();
        }
    }

    public void setSensitivity(int n) {
        this.accelerationThreshold = n;
    }

    public boolean start(SensorManager sensorManager) {
        if (this.accelerometer != null) {
            return true;
        }
        this.accelerometer = sensorManager.getDefaultSensor(1);
        Sensor sensor = this.accelerometer;
        if (sensor != null) {
            this.sensorManager = sensorManager;
            return sensorManager.registerListener((SensorEventListener)this, sensor, 0);
        }
        return false;
    }

    public void stop() {
        if (this.accelerometer != null) {
            this.queue.clear();
            this.sensorManager.unregisterListener((SensorEventListener)this, this.accelerometer);
            this.sensorManager = null;
            this.accelerometer = null;
        }
    }

    public static interface Listener {
        public void hearShake();
    }

    static class Sample {
        boolean accelerating;
        Sample next;
        long timestamp;

        Sample() {
        }
    }

    static class SamplePool {
        private Sample head;

        SamplePool() {
        }

        Sample acquire() {
            Sample sample = this.head;
            if (sample == null) {
                return new Sample();
            }
            this.head = sample.next;
            return sample;
        }

        void release(Sample sample) {
            sample.next = this.head;
            this.head = sample;
        }
    }

    static class SampleQueue {
        private int acceleratingCount;
        private Sample newest;
        private Sample oldest;
        private final SamplePool pool = new SamplePool();
        private int sampleCount;

        SampleQueue() {
        }

        void add(long l, boolean bl) {
            this.purge(l - 500000000L);
            Sample sample = this.pool.acquire();
            sample.timestamp = l;
            sample.accelerating = bl;
            sample.next = null;
            Sample sample2 = this.newest;
            if (sample2 != null) {
                sample2.next = sample;
            }
            this.newest = sample;
            if (this.oldest == null) {
                this.oldest = sample;
            }
            this.sampleCount = 1 + this.sampleCount;
            if (bl) {
                this.acceleratingCount = 1 + this.acceleratingCount;
            }
        }

        void clear() {
            Sample sample;
            while ((sample = this.oldest) != null) {
                this.oldest = sample.next;
                this.pool.release(sample);
            }
            this.newest = null;
            this.sampleCount = 0;
            this.acceleratingCount = 0;
        }

        boolean isShaking() {
            int n;
            int n2;
            Sample sample = this.newest;
            return sample != null && this.oldest != null && sample.timestamp - this.oldest.timestamp >= 250000000L && (n = this.acceleratingCount) >= ((n2 = this.sampleCount) >> 1) + (n2 >> 2);
        }

        void purge(long l) {
            Sample sample;
            while (this.sampleCount >= 4 && (sample = this.oldest) != null && l - sample.timestamp > 0L) {
                Sample sample2 = this.oldest;
                if (sample2.accelerating) {
                    this.acceleratingCount = -1 + this.acceleratingCount;
                }
                this.sampleCount = -1 + this.sampleCount;
                this.oldest = sample2.next;
                if (this.oldest == null) {
                    this.newest = null;
                }
                this.pool.release(sample2);
            }
        }
    }

}

