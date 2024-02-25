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
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //region const
    private static final String[] graphicNames = new String[] {
            "Ускорение по оси X, при N = 10",
            "Ускорение по оси Y, при N = 10",
            "Ускорение по оси Z, при N = 10",
            "Ускорение по оси X, при N = 100",
            "Ускорение по оси Y, при N = 100",
            "Ускорение по оси Z, при N = 100",
            "Ускорение по оси X, при N = 1000",
            "Ускорение по оси Y, при N = 1000",
            "Ускорение по оси Z, при N = 1000",

    };
    private static final float[] counterN = new float[] {10f, 100f, 1000f};
    private static final int[] graphicColors = new int[] {Color.RED, Color.BLUE, Color.GREEN};

    private static final int[][] numberGraphicLines = new int[][] {
        new int[] {0, 1, 2},
        new int[] {3, 4, 5},
        new int[] {6, 7, 8}
    };
    //endregion
    //region static value
    private static int counterEntries = 0;
    private static int counterButton = 0;
    private static int counterPage = 0;
    //endregion

    private SensorManager sensorManager;
    private Sensor accleroSensor;
    private SensorEventListener sensorEventView;
    private SensorEventListener sensorEventReal;
    private LineChart lineChart;
    private List<List<Entry>> entries = new ArrayList<List<Entry>>() {{
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

    private List<Float> mathExpectation = new ArrayList<Float>() {{
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
    }};

    private List<Float> standardDeviation = new ArrayList<Float>() {{
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
        add(0f);
    }};

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

        draftGraphic();
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
                                CalculationGraphics(x, y, z, new int[] {0, 1, 2}, 10);
                            }
                            else if (counterButton == 1) {
                                CalculationGraphics(x, y, z, new int[] {3, 4, 5}, 100);
                            }
                            else if (counterButton == 2) {
                                CalculationGraphics(x, y, z, new int[] {6, 7, 8}, 1000);
                            }

                            if (counterEntries == 10 && counterButton == 0 && counterPage == 0){
                                btnRight.setEnabled(true);
                            }

                            if (
                                (counterEntries == 10 && counterButton == 0) ||
                                (counterEntries == 100 && counterButton == 1) ||
                                (counterEntries == 1000 && counterButton == 2)
                            ) {

                                CalculationMathVariable();
                                counterButton = (counterButton == 2) ? 0 : ++counterButton;
                                counterEntries = 0;
                                sensorManager.unregisterListener(sensorEventReal);
                                btn.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Расчет завершен.", Toast.LENGTH_SHORT).show();
                                draftGraphic();
                                draftMathVariable();
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

            }
            draftGraphic();
            draftMathVariable();
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

            }
            draftGraphic();
            draftMathVariable();
        }
    }
    private void CalculationGraphics(float x, float y, float z, int[] numbersEntryList, int step) {

        if (counterEntries == step) {
            counterEntries = 0;
            return;
        }

        if (counterEntries == 0){
            entries.set(numbersEntryList[0], new ArrayList<>());
            entries.set(numbersEntryList[1], new ArrayList<>());
            entries.set(numbersEntryList[2], new ArrayList<>());
        }

        if (counterEntries >= 0 && counterEntries <= step) {
            entries.get(numbersEntryList[0]).add(new Entry(counterEntries, x));
            entries.get(numbersEntryList[1]).add(new Entry(counterEntries, y));
            entries.get(numbersEntryList[2]).add(new Entry(counterEntries, z));
        }

        counterEntries++;

        try {
            TimeUnit.MICROSECONDS.sleep(5000 / step);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void CalculationMathVariable(){
        for (int i : numberGraphicLines[counterButton]){
            float mathExpectationTemp = 0;

            for (Entry item : entries.get(i)) {
                mathExpectationTemp += item.getY();
            }
            mathExpectationTemp /= entries.get(i).size();
            mathExpectationTemp = (float) Math.round(mathExpectationTemp * 100) / 100;

            mathExpectation.set(i, mathExpectationTemp);

            float standardDeviationTemp = 0;
            for (Entry item : entries.get(i)) {
                float arg = item.getY();
                arg -= mathExpectation.get(i);
                arg = (float) Math.pow(arg, 2);
                standardDeviationTemp += arg;
            }
            standardDeviationTemp /= entries.get(i).size();
            standardDeviationTemp = (float) Math.sqrt(standardDeviationTemp);
            standardDeviationTemp = (float) Math.round(standardDeviationTemp * 100) / 100;

            standardDeviation.set(i, standardDeviationTemp);
        }
    }
    private void draftGraphic(){
        int currentNumberGraphic = counterPage / 3;
        int currentColorGraphic = counterPage % 3;

        lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(counterN[currentNumberGraphic]);
        xAxis.setLabelCount(10);
        xAxis.setAxisLineColor(Color.BLACK);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMinimum(-12f);
        yAxis.setAxisMaximum(12f);
        yAxis.setLabelCount(25);
        yAxis.setAxisLineColor(Color.BLACK);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        LineDataSet dataSet = new LineDataSet(entries.get(counterPage), graphicNames[counterPage]);
        dataSet.setColor(graphicColors[currentColorGraphic]);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void draftMathVariable() {
        TextView textMathExpectation = (TextView)findViewById(R.id.textMathExpectation);
        TextView textStandardDeviation = (TextView)findViewById(R.id.textStandardDeviation);
        String strMathExpectation = "μ: " + mathExpectation.get(counterPage);
        textMathExpectation.setText(strMathExpectation);
        String strStandardDeviation = "σ: " + standardDeviation.get(counterPage);
        textStandardDeviation.setText(strStandardDeviation);
    }
}