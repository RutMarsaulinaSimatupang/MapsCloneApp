package com.example.mapsproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class BarometerSensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private float currentPressure = 1013.25f; // Nilai tekanan default
    private TextView pressureTextView;
    private List<Float> pressureHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10; // Maksimal 10 data riwayat tekanan
    private static final float ALTITUDE_METERS = 50.0f; // Ganti dengan ketinggian aktual

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menginflate layout untuk fragmen ini
        View view = inflater.inflate(R.layout.fragment_barometer_sensor, container, false);

        pressureTextView = view.findViewById(R.id.pressureTextView); // Pastikan TextView ini ada di layout Anda

        // Inisialisasi sensor barometer
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (pressureSensor == null) {
            Log.e("Barometer", "Pressure sensor not found!");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            currentPressure = event.values[0];
            Log.d("Pressure", "Current pressure: " + currentPressure);

            // Perbarui riwayat tekanan
            if (pressureHistory.size() >= MAX_HISTORY_SIZE) {
                pressureHistory.remove(0); // Hapus nilai tertua jika riwayat penuh
            }
            pressureHistory.add(currentPressure);

            // Perbarui tampilan tekanan dan analisis tren
            updatePressureDisplay(currentPressure);
            updateTrendDisplay();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Tidak digunakan
    }

    private void updatePressureDisplay(float pressure) {
        // Sesuaikan tekanan ke permukaan laut
        float seaLevelPressure = adjustPressureToSeaLevel(pressure, ALTITUDE_METERS);
        String pressureInfo;
        if (seaLevelPressure < 1000) {
            pressureInfo = "Bad Weather ( " + seaLevelPressure + " hPa)";
        } else if (seaLevelPressure < 1020) {
            pressureInfo = "Cloudy ( " + seaLevelPressure + " hPa)";
        } else {
            pressureInfo = "Bright ( " + seaLevelPressure + " hPa)";
        }
        pressureTextView.setText(pressureInfo);
    }

    private void updateTrendDisplay() {
        if (pressureHistory.size() >= 2) {
            float latest = pressureHistory.get(pressureHistory.size() - 1);
            float previous = pressureHistory.get(pressureHistory.size() - 2);

            String trendInfo;
            if (latest > previous) {
                trendInfo = "Tekanan meningkat: Cuaca membaik.";
            } else if (latest < previous) {
                trendInfo = "Tekanan menurun: Cuaca memburuk.";
            } else {
                trendInfo = "Tekanan stabil: Cuaca stabil.";
            }
            Log.d("PressureTrend", trendInfo);
        }
    }

    private float adjustPressureToSeaLevel(float pressure, float altitudeMeters) {
        // Rumus untuk menyesuaikan tekanan berdasarkan ketinggian
        return (float) (pressure / Math.pow(1 - (0.0065 * altitudeMeters) / 288.15, 5.255));
    }
}
