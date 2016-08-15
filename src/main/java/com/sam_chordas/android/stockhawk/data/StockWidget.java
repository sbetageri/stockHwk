package com.sam_chordas.android.stockhawk.data;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Admin on 14-08-2016.
 */
public class StockWidget {
    private static final String _TAG = "stock_widget_tag";
    public String mSymbol;
    public String mBidPrice;

    public String getSymbol() {
        return mSymbol;
    }

    public String getBidPrice() {
        return mBidPrice;
    }

    public StockWidget(String symbol, String bidPrice) {
        mSymbol = symbol;
        mBidPrice = bidPrice;
    }

    @Override
    public String toString() {
        return mSymbol + " : " + mBidPrice;
    }

    public static ArrayList<StockWidget> getStockList(Cursor cursor) {
        ArrayList<StockWidget> stockList = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor != null && cursor.getCount() > 0) {
            for(int i = 0; i < cursor.getCount(); i++) {
                String sym = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
                String price = cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                StockWidget obj = new StockWidget(sym, price);
                stockList.add(obj);
                cursor.moveToNext();
            }
        }
        return stockList;
    }
}
