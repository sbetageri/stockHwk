package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StockDetailActivity extends Activity {

    public static final String _TAG = "stock_detail_activity";

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
            Calendar c = new GregorianCalendar();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int date = c.get(Calendar.DATE);
            Log.e(_TAG, "symbol : " + symbol);
            Log.e(_TAG, "Year : " + Integer.toString(year));
            Log.e(_TAG, "Month : " + Integer.toString(month));
            Log.e(_TAG, "day : " + Integer.toString(day));
            Log.e(_TAG, "date : " + Integer.toString(date));
        }
    }
}
