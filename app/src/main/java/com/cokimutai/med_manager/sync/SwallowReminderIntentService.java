package com.cokimutai.med_manager.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class SwallowReminderIntentService extends IntentService {

    public SwallowReminderIntentService() {
        super("SwallowReminderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action);

    }
}
