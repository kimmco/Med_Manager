package com.cokimutai.med_manager.sync;

import android.content.Context;

import com.cokimutai.med_manager.utilities.NotificationUtils;
import com.cokimutai.med_manager.utilities.PreferenceUtilities;

public class ReminderTasks {

    public static final String ACTION_INCREMENT_SWALLOWED_COUNT = "increment_swallowed_count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_SWALLOW_REMINDER = "swallow_reminder";

    public static void executeTask(Context context, String action){
        if (ACTION_INCREMENT_SWALLOWED_COUNT == action){
            incrementSwallowCount(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_SWALLOW_REMINDER.equals(action)){
            issueSwallowReminder(context);
        }
    }

    public static void incrementSwallowCount(Context context){
        PreferenceUtilities.incrementSwallowCount(context);
        NotificationUtils.clearAllNotifications(context);
    }

    private static void issueSwallowReminder(Context context){
        //NotificationUtils.itsTimeToSwallow(context);

    }

}
