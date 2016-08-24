package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.StockWidget;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import java.util.ArrayList;

/**
 * Created by Admin on 14-08-2016.
 */
public class StockWidgetService extends RemoteViewsService {

    public static final String CLICKABLE_INTENT = "clickable_intnet";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewFactory(this.getApplicationContext(), intent);
    }
}

class StockRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Intent mIntent;
    private ArrayList<StockWidget> stockList = new ArrayList<>();
    private static final String _TAG = "stock_remoteviewfactory";

    public StockRemoteViewFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onDataSetChanged() {
        Log.e(_TAG, "why isn't it coming here");
        loadDbDataToList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return stockList.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        StockWidget sw = stockList.get(position);
        Log.e(_TAG, "get view at : " + Integer.toString(position));
        Log.e(_TAG, sw.toString());
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_stock_symbol, sw.getSymbol());
        String cdSymbol = mContext.getString(R.string.cd_stock_symbol);
        rv.setContentDescription(R.id.widget_stock_symbol, cdSymbol + sw.getSymbol());

        String cdBidPrice = mContext.getString(R.string.cd_bid_price);
        rv.setTextViewText(R.id.widget_stock_bidprice, sw.getBidPrice());
        rv.setContentDescription(R.id.widget_stock_bidprice, cdBidPrice + sw.getBidPrice());

        Intent intent = new Intent();
        intent.setAction(StockWidgetService.CLICKABLE_INTENT);
        intent.putExtra(MyStocksActivity.STOCK_SYMBOL, sw.getSymbol());
        intent.putExtra(MyStocksActivity.STOCK_CUR_VAL, sw.getBidPrice());
        rv.setOnClickFillInIntent(R.id.stock_widget_item, intent);
        return rv;
    }

    @Override
    public void onCreate() {
            /*
           QuoteProvider.Quotes.CONTENT_URI,
        new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
        QuoteColumns.ISCURRENT + " = ?",
        new String[]{"1"},

             */
        Log.e(_TAG, "service.onCreate");
        loadDbDataToList();
    }

    private void loadDbDataToList() {
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"}, null);
        Log.e(_TAG, "CURSOR COUNT : " + Integer.toString(cursor.getCount()));
        stockList = StockWidget.getStockList(cursor);
        Log.e(_TAG, "stock list size : " + Integer.toString(stockList.size()));
    }
}
