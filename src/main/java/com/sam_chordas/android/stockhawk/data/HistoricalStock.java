package com.sam_chordas.android.stockhawk.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 18-08-2016.
 */
public class HistoricalStock {
    private String mYear;
    private String mMonth;
    private String mDate;
    private float mMaxVal;
    private String mSymbol;

    public static final String _TAG = "historical_data";

    @Override
    public String toString() {
        return mSymbol + Float.toString(mMaxVal);
    }

    public HistoricalStock(JSONObject json) {
        try {
            mSymbol = json.getString("Symbol");
            String[] date = json.getString("Date").split("-");
            mYear = date[0];
            mMonth = date[1];
            mDate = date[2];
            mMaxVal = Float.parseFloat(json.getString("High"));
        } catch(JSONException e) {
            Log.e(_TAG, "json exception : " + json.toString());
        }
    }

    public String getDate() {
        return mDate;
    }

    public String getCalendarDate() {
        return mYear + "-" + mMonth + "-" + mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public float getMaxVal() {
        return mMaxVal;
    }

    public void setMaxVal(int maxVal) {
        mMaxVal = maxVal;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        mMonth = month;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }
}
