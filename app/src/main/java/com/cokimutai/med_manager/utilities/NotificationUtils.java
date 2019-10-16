package com.cokimutai.med_manager.utilities;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.cokimutai.med_manager.MedDetailsActivity;
import com.cokimutai.med_manager.R;
import com.cokimutai.med_manager.data.MedDatabase;


public class NotificationUtils extends IntentService {

    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static final int SWALLOW_REMINDER_NOTIFICATION_ID = 1009;

    private static final String SWALLOW_REMINDER_MEDNAME = "med_name";
    private MedDatabase db;

    public static final String SWALLOW_REMINDER_NOTIFICATION_CHAN_ID = "swallo_time_notif_channel";

    public NotificationUtils(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db = MedDatabase.getsInstance(getApplicationContext());
        int theMedId = intent.getIntExtra(NotificationUtils.SWALLOW_REMINDER_NOTIFICATION_CHAN_ID, 1);
        String medName = intent.getStringExtra(SWALLOW_REMINDER_MEDNAME);
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Intent action = new Intent(this, MedDetailsActivity.class);
        action = setMedicationIdIntent(action);

        PendingIntent operation =
                PendingIntent.getActivity(this, theMedId, action,PendingIntent.FLAG_UPDATE_CURRENT );

        //itsTimeToSwallow(getApplicationContext(),theMedId);

    }
    private Intent setMedicationIdIntent(Intent intent){
        int theMedId = intent.getIntExtra(NotificationUtils.SWALLOW_REMINDER_NOTIFICATION_CHAN_ID, 1);
        intent.putExtra(MedDetailsActivity.EXTRA_MED_ID, theMedId);


        return intent;
    }
    private void queryMedication(final int medId, Context mContext) {
       /* AddMedViewModelFactory factory = new AddMedViewModelFactory(db, medId);
        final AddMedicationViewModel viewModel =
                ViewModelProviders.of(mContext, factory).get(AddMedicationViewModel.class);
        viewModel.getMed().observe(this, new Observer<MedEntry>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable MedEntry medEntry) {
                viewModel.getMed().removeObserver(this);
                Log.d(TAG, "Receiving data update from LiveData Updating...");
                //populateUI(medEntry);
            }
        });*/

    }

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void itsTimeToSwallow(Context context, int medId, String medName){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nC = new NotificationChannel(
                    SWALLOW_REMINDER_NOTIFICATION_CHAN_ID,
                    "Primary",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(nC);
        }

        NotificationCompat.Builder nB = new NotificationCompat.Builder(context,
                SWALLOW_REMINDER_NOTIFICATION_CHAN_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_search)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(medName)
                .setContentText(medName)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "It's time to take your Medication, Please do not procatinate!!!"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, medId))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
            nB.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(medId, nB.build());
    }

    private static PendingIntent contentIntent(Context context, int id){
        Intent startActivityIntent = new Intent(context, MedDetailsActivity.class);
        startActivityIntent.putExtra(MedDetailsActivity.EXTRA_MED_ID, id);
        return PendingIntent.getActivity(context,
                id,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable._bunch_and_a_glass);
        return largeIcon;
    }

}
