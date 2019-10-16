package com.cokimutai.med_manager.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MedReminderObj {
    Context mContext;
    int count, dosage, interval;
    String med_description, starting_time, starting_date, ending_date, medname;
    AlarmManager alarmManager;
    PendingIntent alarmpendingIntent;
    Calendar current_calendar;
    Intent intent;
    int alarmReqCode;

    public MedReminderObj(Context context,int count,String medname,String med_description, int dosage,
                          int times, String starting_time, String starting_date, String ending_date) {
        this.mContext = context;
       // this.count = count;
        this.medname = medname;
        this.med_description = med_description;
        this.dosage = dosage;
        this.interval = times;
        this.starting_time = starting_time;
        this.starting_date = starting_date;
        this.ending_date = ending_date;
        this.alarmReqCode = count;

    }
    public void setUpReminder(int period){
        int mins = Integer.parseInt(starting_time.substring(starting_time.lastIndexOf(':') + 1));
        int hrs = Integer.parseInt(starting_time.substring(0, starting_time.lastIndexOf(':')));

        current_calendar = new GregorianCalendar();
        //set the current time and date for this calendar
        current_calendar.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hrs);
        calendar.set(Calendar.MINUTE, mins);

        intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.REMINDER_ID, alarmReqCode);
        intent.putExtra(AlarmReceiver.REMINDER_ENDING_DATE, ending_date);
        intent.putExtra(AlarmReceiver.REMINDER_PERIOD, period);
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        int hr = (int) interval;
        long timeInMills = calendar.getTimeInMillis();
        long current_time = System.currentTimeMillis();
        alarmpendingIntent = PendingIntent.getBroadcast(mContext, alarmReqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timeInMills, hr * 60 * 60 * 1000, alarmpendingIntent);

    }
}
