package com.cokimutai.med_manager.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.cokimutai.med_manager.AddMedication;
import com.cokimutai.med_manager.data.DateConverter;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Date;


public class SwallowReminderJobService extends JobService {

    private AsyncTask mBackgroundTask;


    @Override
    public boolean onStartJob( final JobParameters job) {

        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = SwallowReminderJobService.this;
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_SWALLOW_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, true);

            }
        };
        mBackgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
       //Date startReminderOn = AddMedication.beginReminderDate;
       Date endReminderOn = AddMedication.stopReminderDate;
       // Long startAt = DateConverter.toTimestamp(startReminderOn);
        Long endtAt = DateConverter.toTimestamp(endReminderOn);
       long now = System.currentTimeMillis();


        if (mBackgroundTask != null )
            mBackgroundTask.cancel(true);
        return true;
    }
}
