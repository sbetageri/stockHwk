package com.sam_chordas.android.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockWidgetService;

/**
 * Created by Admin on 14-08-2016.
 */
public class StockAppWidgetProvider extends AppWidgetProvider {
    public static final String _TAG = "stockAppWidgetProvider";

    public static final String UPDATE_WIDGET = "com.sam_chordas.android.UPDATE_WIDGET";
    public static final String APPWIDGET_UPADTE = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(_TAG, "on receive");
        if(intent.getAction().equals(UPDATE_WIDGET)) {
            ComponentName name = new ComponentName(context, StockAppWidgetProvider.class);
            int[] appWidgetIds = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(name);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        } else if (intent.getAction().equals(APPWIDGET_UPADTE)) {
            Log.e(_TAG, "receive : update :-> : ");
            ComponentName name = new ComponentName(context, StockAppWidgetProvider.class);
            int[] appWidgetIds = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(name);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i = 0; i < appWidgetIds.length; i++) {
            Log.e(_TAG, "on update");
            Intent intent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget_list);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stock_widget_list_layout_view, intent);
            rv.setEmptyView(appWidgetIds[i], R.id.empty_list_widget_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
