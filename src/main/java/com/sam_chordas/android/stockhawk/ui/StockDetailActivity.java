package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalStock;
import com.sam_chordas.android.stockhawk.rest.DownloadCompleteListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StockDetailActivity extends Activity implements DownloadCompleteListener {

    public static final String _TAG = "stock_detail_activity";
    private TextView mDate;
    private TextView mSymbol;
    private TextView mMaxVal;
    private LineChart mChart;
    private ArrayList<HistoricalStock> mStockDetails;

    public void onDownloadCompleteListener() {
        logDownloadedDetails();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                plotChart();
            }
        });
    }

    public void setViewReferences() {
        mSymbol = (TextView)findViewById(R.id.textview_detail_stock_name);
        mDate = (TextView)findViewById(R.id.stock_date);
        mMaxVal = (TextView)findViewById(R.id.stock_max_val);
        mChart = (LineChart)findViewById(R.id.stock_linechart);
    }

    public void displayHighlightedStockData(HistoricalStock stock) {
        mDate.setText(stock.getCalendarDate());
        mDate.setContentDescription(getString(R.string.cd_stock_date));
        mMaxVal.setText(Float.toString(stock.getMaxVal()));
        mMaxVal.setContentDescription(getString(R.string.cd_stock_value));
    }

    public void beautifyChart() {
        mChart.setBorderColor(getResources().getColor(R.color.white));
        mChart.setDescriptionColor(getResources().getColor(R.color.white));
        mChart.setBorderColor(getResources().getColor(R.color.white));
        mChart.setHighlightPerDragEnabled(true);
        mChart.setDescriptionColor(getResources().getColor(R.color.white));
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mDate.setText(Float.toString(h.getX()));
                mMaxVal.setText(Float.toString(h.getY()));
                displayHighlightedStockData(mStockDetails.get((int)h.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void styleXAxis() {
        XAxis axis = mChart.getXAxis();
        axis.setTextColor(getResources().getColor(R.color.white));
        axis.setValueFormatter(new DateXAxisValueFormatter());
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setLabelRotationAngle(60f);
    }

    public void styleLeftRightAxis() {
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMinValue(0f);
        //yAxis.setAxisMaxValue(1000f);
        yAxis.setTextColor(getResources().getColor(R.color.white));


        yAxis = mChart.getAxisRight();
        yAxis.setAxisMinValue(0f);
        //yAxis.setAxisMaxValue(1000f);
        yAxis.setTextColor(getResources().getColor(R.color.white));
    }

    public void styleDataSet(LineDataSet dataSet) {
        dataSet.setColor(getResources().getColor(R.color.material_blue_500));
        dataSet.setCubicIntensity(1f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
    }

    public void downloadParseStoreStockDetails(String symbol, final DownloadCompleteListener listener) {
        /*
            https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22YHOO%22%20and%20startDate%20%3D%20%222015-08-16%22%20and%20endDate%20%3D%20%222016-08-19%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
        */
        if(mStockDetails == null) {
            mStockDetails = new ArrayList<>();
        }
        try {
            String requestURL = buildQuery(symbol);
            Log.e(_TAG, "request url : " + requestURL);
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(requestURL)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                    Log.e(_TAG, "callback failure : " + e.toString());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(! response.isSuccessful()) {
                        throw new IOException("IO failure");
                    }
                    //Log.e(_TAG, "response : " + response.body().string());
                    //Log.e(_TAG, "response : " + response.toString());
                    try {
                        JSONObject resp = new JSONObject(response.body().string());
                        resp = resp.getJSONObject("query");
                        resp = resp.getJSONObject("results");
                        JSONArray stockDetails = resp.getJSONArray("quote");
                        Log.e(_TAG, "downloaded");
                        for(int i = stockDetails.length() - 1; i >= 0; i--) {
                            HistoricalStock stock = new HistoricalStock(stockDetails.getJSONObject(i));
                            //mStockDetails.add(new HistoricalStock(stockDetails.getJSONObject(i)));
                            mStockDetails.add(stock);
                        }
                        //plotChart();
                        listener.onDownloadCompleteListener();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch(Exception e) {
            Log.e(_TAG, e.toString());
        }
    }

    private String buildQuery(String symbol) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlBuilder.append(Uri.encode("select * from yahoo.finance.historicaldata where symbol = "));//, "UTF-8"));
            urlBuilder.append(Uri.encode("\"" + symbol + "\""));
            urlBuilder.append(Uri.encode(" and "));
            urlBuilder.append(Uri.encode("startDate = "));//, "UTF-8"));
            urlBuilder.append(Uri.encode("\"" + getPreviousYearDate() + "\""));
            urlBuilder.append(Uri.encode(" and "));
            urlBuilder.append(Uri.encode("endDate= "));//, "UTF-8"));
            urlBuilder.append(Uri.encode("\"" + getCurrentDate() + "\""));
            // &format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys&callback="
            urlBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
            return urlBuilder.toString();
        } catch(Exception e) {
            Log.e(_TAG, e.toString());
            return null;
        }
    }

    private String getPreviousYearDate() {
        String[] date = getCurrentDate().split("-");
        date[0] = Integer.toString(Integer.parseInt(date[0]) - 1);
        StringBuilder sb = new StringBuilder();
        sb.append(date[0]);
        sb.append("-");
        sb.append(date[1]);
        sb.append("-");
        sb.append(date[2]);
        return sb.toString();
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("-");
        if(month < 10) {
            sb.append("0");
        }
        sb.append(month + 1);
        sb.append("-");
        sb.append(date);
        return sb.toString();
    }

    public void logDownloadedDetails() {
        Log.e(_TAG, Integer.toString(mStockDetails.size()));
        for(int i = 0; i < mStockDetails.size(); i++) {
            HistoricalStock stock = mStockDetails.get(i);
            Log.e(_TAG, stock.getCalendarDate() + " : " + Float.toString(stock.getMaxVal()));
        }
    }

    public void plotChart() {
        beautifyChart();
        styleXAxis();
        styleLeftRightAxis();
        List<Entry> entries = new ArrayList<Entry>();
        int len = mStockDetails.size() - 1;
        for(int i = len; i >= 0; i--) {
            HistoricalStock stock = mStockDetails.get(i);
            entries.add(new Entry((float)(len - i), stock.getMaxVal()));
        }
        LineDataSet lDataSet = new LineDataSet(entries, "trial_data");
        styleDataSet(lDataSet);
        LineData lData = new LineData(lDataSet);
        mChart.setData(lData);
        mChart.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Utils.isConnected(this))  {
            setContentView(R.layout.layout_no_network);
            return;
        } else {
            setContentView(R.layout.activity_line_graph);
            Intent intent = getIntent();
            String symbol = intent.getStringExtra(MyStocksActivity.STOCK_SYMBOL);
            String curVal = intent.getStringExtra(MyStocksActivity.STOCK_CUR_VAL);
            if(mStockDetails == null) {
                mStockDetails = new ArrayList<>();
            }
            downloadParseStoreStockDetails(symbol, this);
            logDownloadedDetails();
            setViewReferences();

            mSymbol.setText(symbol);
            mSymbol.setContentDescription(getString(R.string.cd_stock_symbol));
            mDate.setText(getCurrentDate());
            mMaxVal.setText(curVal);
        }
    }

    public class DateXAxisValueFormatter implements AxisValueFormatter {
        @Override
        public int getDecimalDigits() {
            return 0;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //Log.e(_TAG, Float.toString(value));
            //return "sai";
            return mStockDetails.get((int)value).getCalendarDate();
        }
    }
}
