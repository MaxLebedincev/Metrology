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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //region const
    private static final int[] STEPS = new int[] {10, 100, 1000};
    private static final int[] NUMBER_GRAPHICS = new int[] {0, 1, 2};
    private static final int[][] NUMBER_GRAPHIC_LINES = new int[][] {
        new int[] {0, 1, 2},
        new int[] {3, 4, 5},
        new int[] {6, 7, 8}
    };
    private static final String[] GRAPHIC_NAMES = new String[] {
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
    private static final int[] GRAPHIC_COLORS = new int[] {Color.RED, Color.BLUE, Color.GREEN};
    final int X_AXIS_LABEL_COUNT = 25;
    final int Y_AXIS_LABEL_COUNT = 25;
    final float MIN_ACCELEROMETER_VALUE = -12f;
    final float MAX_ACCELEROMETER_VALUE = 12f;
    final float MIN_PROBABILITY = 0f;
    final float MAX_PROBABILITY = 1f;
    //endregion

    //region sensors
    private SensorManager sensorManager;
    private Sensor accleroSensor;
    private SensorEventListener sensorEventView;
    private SensorEventListener sensorEventReal;
    //endregion

    //region Static value
    private static int counterEntries = 0;
    private static int counterButton = 0;
    private static int counterPage = 0;
    //endregion

    //region List value
    private List<List<Float>> accelerometerVal = new ArrayList<List<Float>>() {{
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
    //endregion

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
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };
        }
        else {
            Toast.makeText(this, "Сервис сенсоров не обнаружен.", Toast.LENGTH_SHORT).show();
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
                                for (int i : NUMBER_GRAPHICS)
                                {
                                    accelerometerVal.set(NUMBER_GRAPHIC_LINES[counterButton][i], new ArrayList<>());
                                    entries.set(NUMBER_GRAPHIC_LINES[counterButton][i], new ArrayList<>());
                                }
                            }

                            CalculationGraphics(x, y, z, NUMBER_GRAPHIC_LINES[counterButton], STEPS[counterButton]);

                            if (
                                (counterButton == 0 || counterButton == 1 || counterButton == 2)
                                &&
                                (counterEntries == STEPS[counterButton])
                            ) {
                                btnRight.setEnabled(true);
                                btn.setEnabled(true);
                                sensorManager.unregisterListener(sensorEventReal);

                                CalculationMathVariable();

                                counterButton = (counterButton == 2) ? 0 : ++counterButton;
                                counterEntries = 0;

                                Toast.makeText(getApplicationContext(), "Расчет завершен.", Toast.LENGTH_SHORT).show();

                                draftGraphic();
                                draftMathVariable();
                            }

                        }
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
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

    //region math func
    private void CalculationGraphics(float x, float y, float z, int[] numbersEntryList, int step) {

        accelerometerVal.get(numbersEntryList[0]).add(x);
        accelerometerVal.get(numbersEntryList[1]).add(y);
        accelerometerVal.get(numbersEntryList[2]).add(z);

        counterEntries++;

        try {
            TimeUnit.MILLISECONDS.sleep(3000 / step);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void CalculationMathVariable(){
        for (int i : NUMBER_GRAPHIC_LINES[counterButton]){
            // mathExpectation
            float mathExpectationTemp = 0;

            for (Float item : accelerometerVal.get(i)) {
                mathExpectationTemp += item;
            }
            mathExpectationTemp /= accelerometerVal.get(i).size();
            mathExpectationTemp = (float) Math.round(mathExpectationTemp * 100) / 100;

            mathExpectation.set(i, mathExpectationTemp);

            // standardDeviation
            float standardDeviationTemp = 0;
            for (Float item : accelerometerVal.get(i)) {
                float arg = item;
                arg -= mathExpectation.get(i);
                arg = (float) Math.pow(arg, 2);
                standardDeviationTemp += arg;
            }
            standardDeviationTemp /= accelerometerVal.get(i).size();
            standardDeviationTemp = (float) Math.sqrt(standardDeviationTemp);
            standardDeviationTemp = (float) Math.round(standardDeviationTemp * 100) / 100;

            standardDeviation.set(i, standardDeviationTemp);

            for (float g = MIN_ACCELEROMETER_VALUE; g < 26f; g++)
            {
                accelerometerVal.get(i).add(g);
            }

            Collections.sort(accelerometerVal.get(i));

            // filling in the values
            float newItem = -99999f;
            float counterKey = 0f;
            for (float item : accelerometerVal.get(i)) {
                if (newItem != item)
                {
                    if (counterKey != 0f) {
                        float y = (float) Math.round((counterKey/(accelerometerVal.get(i).size() - 25f)) * 100) / 100;
                        entries.get(i).add(new Entry(newItem, y));
                    }
                    newItem = item;
                    counterKey = 1f;
                } else {
                    counterKey++;
                }
            }
        }
    }
    //endregion

    //region graphics func
    private void draftGraphic(){
        int currentColorGraphic = counterPage % NUMBER_GRAPHICS.length;

        LineChart lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(MIN_ACCELEROMETER_VALUE);
        xAxis.setAxisMaximum(MAX_ACCELEROMETER_VALUE);
        xAxis.setLabelCount(X_AXIS_LABEL_COUNT);
        xAxis.setAxisLineColor(Color.BLACK);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMinimum(MIN_PROBABILITY);
        yAxis.setAxisMaximum(MAX_PROBABILITY);
        yAxis.setLabelCount(Y_AXIS_LABEL_COUNT);
        yAxis.setAxisLineColor(Color.BLACK);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        LineDataSet dataSet = new LineDataSet(entries.get(counterPage), GRAPHIC_NAMES[counterPage]);
        dataSet.setColor(GRAPHIC_COLORS[currentColorGraphic]);
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
    //endregion
}