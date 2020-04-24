package com.userdev.winnerstars.models;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.userdev.winnerstars.DetalhesGrupoActivity;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.utils.Comandos;

import java.text.DecimalFormat;

public class Grafico extends LinearLayout {

    Context context;

    public Grafico(Context context, String titulo, PieDataSet dados, String txtTotal) { //Grafico pizza
        super(context);
        this.context = context;
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        addView(titulo(titulo));
        adicionarView(pizza(dados, txtTotal));
        DetalhesGrupoActivity detalhesGrupoActivity = (DetalhesGrupoActivity) context;
    }
    public Grafico(Context context, String titulo, PieDataSet dados, String txtTotal, OnChartValueSelectedListener listener) { //Grafico pizza com clicklistener
        super(context);
        this.context = context;
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        addView(titulo(titulo));
        adicionarView(pizza(dados, txtTotal).setOnChartValueSelectedListener(listener));
        DetalhesGrupoActivity detalhesGrupoActivity = (DetalhesGrupoActivity) context;
    }

    public Grafico(Context context, String titulo, BarData dados, String txtTotal) { //Grafico barras
        super(context);
        this.context = context;
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        addView(titulo(titulo));
        adicionarView(barraHorizontal(dados, txtTotal));
    }

    public Grafico(Context context, BarData dados[], String txtTotal) { //Graficos para criterios de um grupo
        super(context);
        this.context = context;
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        addView(titulo("Médias por critério"));
        addView(subTitulo(context.getString(R.string.crit1)));
        adicionarView(barraHorizontal(dados[0], txtTotal));
        addView(subTitulo(context.getString(R.string.crit2)));
        adicionarView(barraHorizontal(dados[1], txtTotal));
        addView(subTitulo(context.getString(R.string.crit3)));
        adicionarView(barraHorizontal(dados[2], txtTotal));
        addView(subTitulo(context.getString(R.string.crit4)));
        adicionarView(barraHorizontal(dados[3], txtTotal));
        addView(subTitulo(context.getString(R.string.crit5)));
        adicionarView(barraHorizontal(dados[4], txtTotal));
    }

    void adicionarView(View view) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (300 * scale + 0.5f);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
        addView(view);
    }

    public TextView titulo(String titulo) {
        TextView txtTitulo = new TextView(context);
        txtTitulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtTitulo.setText(titulo);
        txtTitulo.setTextSize(40F);
        txtTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtTitulo.setTypeface(Comandos.burbank);
        return txtTitulo;
    }

    public TextView subTitulo(String titulo) {
        TextView txtTitulo = new TextView(context);
        txtTitulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtTitulo.setText(titulo);
        txtTitulo.setTextSize(30F);
        txtTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtTitulo.setTypeface(Comandos.burbank);
        return txtTitulo;
    }

    public PieChart pizza(PieDataSet pieDataSet, String strMedida) {
        PieChart pieChart = new PieChart(context);
        pieDataSet.setDrawIcons(false);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setIconsOffset(new MPPointF(0, 40));
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.getHoloBlue());

        PieData data = new PieData(pieDataSet);

        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        final Integer sum = Math.round(data.getYValueSum());
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Integer.toString(Math.round(value)) + " (" + new DecimalFormat("###,###,##0.0").format((value/sum)*100) + "%)";
            }
        });

        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText(Integer.toString(Math.round(data.getYValueSum())) + " " + strMedida);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(20F);
        pieChart.setRotationEnabled(false);
        Description description = new Description();
        description.setText("Pontos dos integrantes");
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(0);
        pieChart.setData(data);

        return pieChart;
    }

    public HorizontalBarChart barraHorizontal(BarData barData, final String medida) {
        HorizontalBarChart chart = new HorizontalBarChart(context);
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setData(barData);

        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###,###,##0.0").format(value) + " " + medida;
            }
        });
        barData.setValueTextSize(14F);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setAxisMaximum(groupCount);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });
        //Y-axis
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //return new DecimalFormat("###,###,##0.0").format(value);
                return Integer.toString(Math.round(value));
            }
        });
        leftAxis.setDrawGridLines(true);
        //leftAxis.setSpaceTop(35f);
        //leftAxis.setLabelCount(6, true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setDrawZeroLine(true);

        chart.getData().setHighlightEnabled(false);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getLegend().setTextSize(14F);
        chart.invalidate();

        return chart;
    }



    public static View divisor(Context context) {
        View view = new View(context);
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (2 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels);
        layoutParams.setMargins(0,(int) (20 * scale + 0.5f), 0, (int) (20 * scale + 0.5f));
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

}
