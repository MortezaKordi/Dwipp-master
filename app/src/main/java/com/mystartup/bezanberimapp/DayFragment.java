package com.mystartup.bezanberimapp;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class DayFragment extends Fragment {

    private TextView txtCalories,txtPassCalories,txtProgressPercent;
    private RoundCornerProgressBar progressBar;

    public DayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_day, container, false);
        int caloriesBurnedExercises = 0;
        txtCalories = view.findViewById(R.id.txt_calories);
        txtPassCalories = view.findViewById(R.id.txtPassiveCalor);
        txtProgressPercent = view.findViewById(R.id.txtProgressPercentage);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setSecondaryProgressColor(getContext().getResources().getColor(R.color.exercise_colour));
        progressBar.setProgressColor(getContext().getResources().getColor(R.color.blueLike));
        progressBar.setProgressBackgroundColor(getContext().getResources().getColor(R.color.progress_background));


        //limit line init
        LimitLine limitLineMax = new LimitLine(2500,String.format("%d calories",2500));
        LimitLine limitLineAverage = new LimitLine(1500);
        LimitLine limitLineMin = new LimitLine(500);

        //max line settings
        limitLineMax.enableDashedLine(10f,10f,0);
        limitLineMax.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        limitLineMax.setTextSize(10f);
        limitLineMax.setLineColor(getContext().getResources().getColor(R.color.limit_line));
        limitLineMax.setTextColor(getContext().getResources().getColor(R.color.limit_line));
        limitLineMax.setTypeface(Typeface.SANS_SERIF);

        //average line settings
        limitLineAverage.disableDashedLine();
        limitLineAverage.setLineColor(getContext().getResources().getColor(R.color.limit_line));
        limitLineAverage.setTypeface(Typeface.SANS_SERIF);

        //min line settings
        limitLineMin.disableDashedLine();
        limitLineMin.setLineColor(getContext().getResources().getColor(R.color.limit_line));
        limitLineMin.setTypeface(Typeface.SANS_SERIF);

        //line chart init and config
        LineChart lineChart = view.findViewById(R.id.day_bar_chart);

        lineChart.setNoDataTextColor(R.color.blueLike);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraBottomOffset(10f);

        //right axis
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);


        //xaxis config
        XAxis xAxis = lineChart.getXAxis();

        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setAxisMaximum(24);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //left axis configuration
        lineChart.getAxisLeft().removeAllLimitLines();
        lineChart.getAxisLeft().addLimitLine(limitLineAverage);
        lineChart.getAxisLeft().addLimitLine(limitLineMax);
        lineChart.getAxisLeft().addLimitLine(limitLineMin);
        lineChart.getAxisLeft().setAxisMaximum(2500);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        lineChart.getAxisLeft().setAxisMinValue(0);
        lineChart.getAxisLeft().setDrawLimitLinesBehindData(true);



        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if((int)value == 24){
                    return "00";
                }else {
                    return String.format("%02d", (int) Math.round(value));
                }
            }
        });

        //data settings

        LineDataSet set1;
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(0, 0));;
        values.add(new Entry(15.30f, 1335));

        //formatter
        DecimalFormat formatter1 = new DecimalFormat("#,### calories");
        DecimalFormat formatter2 = new DecimalFormat("#,###");

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
            set1.setDrawIcons(false);
            set1.setLineWidth(1f);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.color.blueLike);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(R.color.limit_line);
            }
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.setColor(R.color.blueLike,0);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            data.setHighlightEnabled(false);
            lineChart.setData(data);
            lineChart.invalidate();
            float lastValue = (values.get(values.size()-1).getY());
            txtPassCalories.setText(formatter2.format(lastValue));
            txtProgressPercent.setText(String.format("%d%%",(int)lastValue*100/2500));
            progressBar.setProgress(lastValue*100/2500);
            progressBar.setSecondaryProgress(caloriesBurnedExercises);
            progressBar.invalidate();
        }


        //setting and formatting calories
        txtCalories.setText(formatter1.format(values.get(values.size()-1).getY()));

        ArrayList<String> theDates = new ArrayList<>();
        theDates.add("farvar");
        theDates.add("ordi");
        theDates.add("khor");
        theDates.add("tir");
        theDates.add("mor");
        theDates.add("shah");
        theDates.add("mehr");
        theDates.add("aban");
        theDates.add("azar");
        theDates.add("dey");
        theDates.add("bah");
        theDates.add("esf");




//        BarDataSet dataSet = new BarDataSet(barEntries, "Label");
//
//        BarData barData = new BarData(dataSet);
//        barChart.setData(barData);
//

        return view;

    }

}
