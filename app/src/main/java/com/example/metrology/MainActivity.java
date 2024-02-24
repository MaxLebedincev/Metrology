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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor accleroSensor;
    private SensorEventListener sv;
    private LineChart lineChart;
    private List<String> xValues;


    private List<Date> counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.calcul);
        button.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null){
            accleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sv = new SensorEventListener(){
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

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
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
        sensorManager.registerListener(sv, accleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sv);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.calcul){

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Date date = new Date();
            counter.add(date);

//            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Toast.makeText(this, dateFormat.format(date), Toast.LENGTH_SHORT).show();
        }
    }
}