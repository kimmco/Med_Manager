package com.cokimutai.med_manager.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.cokimutai.med_manager.MedDetailsActivity;
import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.utilities.NotificationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    String end_date;
    private MedDatabase db;
    private Context mContext;
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;


    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public static final String REMINDER_ID = "com.cokimutai.med_manager.reminders.REMINDER_ID";
    public static final String REMINDER_ENDING_DATE = "com.cokimutai.med_manager.reminders.ENDING_DATE";
    public static final String REMINDER_PERIOD = "com.cokimutai.med_manager.reminders.PERIOD";
    public static final String REMINDER_START_DATE = "com.cokimutai.med_manager.reminders.STARTING_DATE";
    public static final String EXTRA_MED_TIMES_VALUE = "com.cokimutai.med_manager.reminders.TIMES_VALUE";
    public static final String EXTRA_MED_NAME = "com.cokimutai.med_manager.reminders.EXTRA_MED_NAME";

    @Override
    public void onReceive(final Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            int medReceivedID = Integer.parseInt(intent.getStringExtra(MedDetailsActivity.EXTRA_MED_ID));
            String mediName = intent.getStringExtra(EXTRA_MED_NAME);
            scheduleAlarm(context, medReceivedID, mediName);
        }
       /* this.mContext = context;

       // int medId = intent.getIntExtra(REMINDER_ID, 8597);
        int mReceivedID = Integer.parseInt(intent.getStringExtra(MedDetailsActivity.EXTRA_MED_ID));
        String mediName = intent.getStringExtra(EXTRA_MED_NAME);
        String startingDate = intent.getStringExtra(REMINDER_ENDING_DATE);

        NotificationUtils.itsTimeToSwallow(context, mReceivedID, mediName);*/


    }

    public static void scheduleAlarm(Context context, int medId, String medName) {
        AlarmManager manager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        Intent intent = new Intent(context, NotificationUtils.class);
        intent.putExtra(NotificationUtils.SWALLOW_REMINDER_NOTIFICATION_CHAN_ID, medId);
        PendingIntent operation = PendingIntent.getService(
                context, medId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar startTime = Calendar.getInstance();
        try {
            String alarmPref = sharedPreferences.getString("keyAlarm", "07:00");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            startTime.setTime(format.parse(alarmPref));
        }catch (ParseException e){
            return;

        }
        String timeString = sharedPreferences.getString("keyAlarm", "12:00");
        String[] timeStringSplit = timeString.split(":");
        int hour = Integer.parseInt(timeStringSplit[0]);
        int minute = Integer.parseInt(timeStringSplit[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if (Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DATE, 1);
        }
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, operation);
    }

    public void setRepeatAlarm(Context context, Calendar mAlarmCcalendar, int medId,
                               String medName,long repeatTime){
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //put id into the intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MedDetailsActivity.EXTRA_MED_ID, Integer.toString(medId));
        intent.putExtra(EXTRA_MED_NAME, medName);
        mPendingIntent = PendingIntent.getBroadcast(context,medId, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar mCurrentCalendar = Calendar.getInstance();
        long currentTime = mCurrentCalendar.getTimeInMillis();
        long differenceInTheTimes = mAlarmCcalendar.getTimeInMillis() - currentTime;

        // Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + differenceInTheTimes,
                repeatTime, mPendingIntent);

    }
    public void canacelAlarm(Context context, int medId){
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        mPendingIntent = PendingIntent.getBroadcast(context, medId, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

    }

}
