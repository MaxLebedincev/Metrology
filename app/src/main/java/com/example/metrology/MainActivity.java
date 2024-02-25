package com.example.metrology;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor accleroSensor;
    private SensorEventListener sensorEventView;
    private SensorEventListener sensorEventReal;
    private LineChart lineChart;
    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1 = findViewById(R.id.calcul);
        Button btn2 = findViewById(R.id.left);
        Button btn3 = findViewById(R.id.right);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        btn2.setEnabled(false);
        btn3.setEnabled(false);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null){
            accleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorEventView = new SensorEventListener(){
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

                        TextView acceloText = (TextView)findViewById(R.id.myTxt);

                        float x = (float) Math.round(event.values[0] * 100) / 100;
                        float y = (float) Math.round(event.values[1] * 100) / 100;
                        float z = (float) Math.round(event.values[2] * 100) / 100;

                        String result = "X: " + x +
                                        "\nY: " + y +
                                        "\nZ: " + z;

                        acceloText.setText(result);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        } else {
            Toast.makeText(this, "Sensor service not detected.", Toast.LENGTH_SHORT).show();
        }



        // График
        lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("Students Record");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        xValues = Arrays.asList("Nadun","Kamal","Jhon","Jerry");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(1000f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(-13f);
        yAxis.setAxisMaximum(13f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);


        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 60f));
        entries1.add(new Entry(1, 70f));
        entries1.add(new Entry(2, 85f));
        entries1.add(new Entry(3, 95f));

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 50f));
        entries2.add(new Entry(1, 85f));
        entries2.add(new Entry(2, 65f));
        entries2.add(new Entry(3, 80f));


        LineDataSet dataSet1 = new LineDataSet(entries1, "Maths");
        dataSet1.setColor(Color.BLUE);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Science");
        dataSet2.setColor(Color.RED);

        LineData lineData = new LineData(dataSet1, dataSet2);

        lineChart.setData(lineData);

        lineChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventView, accleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventView);
    }

    List<List<Entry>> entries = new ArrayList<List<Entry>>() {{
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
    }};

    static int counterEntries = 0;
    static int counterButton = 0;
    static int counterPage = 0;

    public void CalculationGrapics(float x, float y, float z, int[] numbersEntryList, int step) {

        if (counterEntries == (step - 1)) {
            counterEntries = 0;
            return;
        }

        if (counterEntries == 0){
            entries.set(numbersEntryList[0], new ArrayList<>());
            entries.set(numbersEntryList[1], new ArrayList<>());
            entries.set(numbersEntryList[2], new ArrayList<>());
        }

        if (counterEntries >= 0 && counterEntries < step) {
            entries.get(numbersEntryList[1]).add(new Entry(counterEntries, x));
            entries.get(numbersEntryList[2]).add(new Entry(counterEntries, y));
            entries.get(numbersEntryList[2]).add(new Entry(counterEntries, z));
        }

        counterEntries++;

        try {
            TimeUnit.MICROSECONDS.sleep(2000 / step);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.calcul){

            Button btn = (Button) findViewById(R.id.calcul);
            Button btnRight = findViewById(R.id.right);

            if (sensorManager != null && accleroSensor != null){
                sensorEventReal = new SensorEventListener(){
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

                            float x = (float) Math.round(event.values[0] * 100) / 100;
                            float y = (float) Math.round(event.values[1] * 100) / 100;
                            float z = (float) Math.round(event.values[2] * 100) / 100;

                            if (counterEntries == 0) {
                                Toast.makeText(getApplicationContext(), "Расчет начат.", Toast.LENGTH_SHORT).show();
                                btn.setEnabled(false);
                            }

                            if (counterButton == 0) {
                                CalculationGrapics(x, y, z, new int[] {0, 1, 2}, 10);
                            }
                            else if (counterButton == 1) {
                                CalculationGrapics(x, y, z, new int[] {3, 4, 5}, 100);
                            }
                            else if (counterButton == 2) {
                                CalculationGrapics(x, y, z, new int[] {6, 7, 8}, 1000);
                            }

                            if (counterEntries == 9 && counterButton == 0 && counterPage == 0){
                                btnRight.setEnabled(true);
                            }

                            if (
                                (counterEntries == 9 && counterButton == 0) ||
                                (counterEntries == 99 && counterButton == 1) ||
                                (counterEntries == 999 && counterButton == 2)
                            ) {
                                counterButton = (counterButton == 2) ? 0 : ++counterButton;
                                counterEntries = 0;
                                sensorManager.unregisterListener(sensorEventReal);
                                btn.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Расчет завершен.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                sensorManager.registerListener(sensorEventReal, accleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "Sensor service not detected.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (v.getId() == R.id.left){
            if (counterPage != 0) {
                counterPage--;

                if (counterPage == 0){
                    Button btn = (Button) findViewById(R.id.left);
                    btn.setEnabled(false);
                }
                else if (counterPage == 7) {
                    Button btn = (Button) findViewById(R.id.right);
                    btn.setEnabled(true);
                }

                LineDataSet dataSet = new LineDataSet(entries.get(counterPage), "Science");
                dataSet.setColor(Color.RED);

                LineData lineData = new LineData(dataSet);

                lineChart.setData(lineData);

                lineChart.invalidate();
            }

        }
        else if (v.getId() == R.id.right){

            if (counterPage != 8) {
                counterPage++;

                if (counterPage == 8){
                    Button btn = (Button) findViewById(R.id.right);
                    btn.setEnabled(false);
                }
                else if (counterPage == 1) {
                    Button btn = (Button) findViewById(R.id.left);
                    btn.setEnabled(true);
                }

                LineDataSet dataSet = new LineDataSet(entries.get(counterPage), "Science");
                dataSet.setColor(Color.RED);

                LineData lineData = new LineData(dataSet);

                lineChart.setData(lineData);

                lineChart.invalidate();
            }
        }
    }
}