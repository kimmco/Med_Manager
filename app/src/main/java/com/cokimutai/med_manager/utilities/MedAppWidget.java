package com.cokimutai.med_manager.utilities;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RemoteViews;

import com.cokimutai.med_manager.R;
import com.cokimutai.med_manager.data.MainViewModel;
import com.cokimutai.med_manager.data.MedEntry;

import java.util.List;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class MedAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetId);
        }
    }

    private void updateAppWidget(final Context context, int appWidgetId) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

        MainViewModel model = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);
        model.getMedications().observe((LifecycleOwner) this,
                new Observer<List<MedEntry>>() {
            int medCount;
                    @Override
                    public void onChanged(@Nullable List<MedEntry> medEntries) {

                        if (medEntries != null){
                            medCount = medEntries.size();
                        }else {
                            return;
                        }
                        Random random = new Random();
                        int randomNumber = random.nextInt(medCount);

                        RemoteViews views = new RemoteViews(context.getPackageName(),
                                R.layout.med_app_widget);
                        views.setTextViewText(R.id.widget_text,
                                (CharSequence) medEntries.get(randomNumber));
                       // views.setInt();



                    }
                });



    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

