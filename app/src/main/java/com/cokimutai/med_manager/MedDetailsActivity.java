package com.cokimutai.med_manager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cokimutai.med_manager.data.AddMedViewModelFactory;
import com.cokimutai.med_manager.data.AddMedicationViewModel;
import com.cokimutai.med_manager.data.DateConverter;
import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.data.MedEntry;
import com.cokimutai.med_manager.utilities.PreferenceUtilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MedDetailsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MedDetailsActivity.class.getSimpleName();

    private MedDatabase db;

    private TextView mSwallowCountDisplay;

    private TextView medicineTitle;
    private TextView to_tv;
    private TextView percentageHolder;
    private TextView from_tv;
    private TextView endDateTextView;
    private TextView startDateTextView;
    private TextView medDescriptionTextView;
    private TextView dosage_label;
    private TextView medTypeTvBox;
    private TextView dosageValue;
    private TextView lastTakenView;
    private ImageButton mSwallowBtn;
    private ConstraintLayout med_details_layout;

    private int swallowCount = 0;
    long now = System.currentTimeMillis();

    public static final String EXTRA_MED_ID = "EXTRA_MED_ID";
    public static  MedEntry EXTRA_MED_ID_ENTRY ;
    public static final String INSTANCE_MED_ID = "instanceMedId";

    private static final int DEFAULT_MED_ID = 1;
    private int medicationId = DEFAULT_MED_ID;

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private static final String TIME_FORMAT = "HH:mm";

    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long SEVEN_DAYS_MILLIS = 7 * DAY_MILLIS;

    private static final String NEWDAY_PREFERENCES = "ConterPrefs";
    int newPercentage = 100;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MMM");
    private TimeLeftView timeLeftView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);

        db = MedDatabase.getsInstance(getApplicationContext());

        findViewByTheirIds();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MED_ID)) {
            medicationId = savedInstanceState.getInt(INSTANCE_MED_ID, DEFAULT_MED_ID);
        }
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_MED_ID)) {

            if (medicationId == DEFAULT_MED_ID) {
            medicationId = getIntent().getIntExtra(EXTRA_MED_ID, DEFAULT_MED_ID);
            queryMedication(medicationId);
        }

        }

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        //percentageHolder.setText(String.valueOf(100));

       // Toast.makeText(this, getString(R.string.touch_instructions), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_MED_ID, medicationId);
        super.onSaveInstanceState(outState);
    }

    private void findViewByTheirIds() {
        mSwallowCountDisplay = (TextView)findViewById(R.id.swallowed_num_tv);
        medicineTitle = (TextView)findViewById(R.id.medicine_title);
        to_tv = (TextView)findViewById(R.id.to_tv);
        from_tv = (TextView)findViewById(R.id.from_tv);
        startDateTextView = (TextView)findViewById(R.id.from_date_tv);
        endDateTextView = (TextView)findViewById(R.id.to_date_tv);
        medDescriptionTextView = (TextView)findViewById(R.id.med_desc_tv);
        dosage_label = (TextView)findViewById(R.id.dosage_label);
        dosageValue = (TextView)findViewById(R.id.dosage_value_label_tv);
        lastTakenView = (TextView) findViewById(R.id.last_taken_tv);
        mSwallowBtn = (ImageButton) findViewById(R.id.take_medication_button);
        med_details_layout = findViewById(R.id.med_details_layout);
        percentageHolder = findViewById(R.id.percentageHolder_tv);
        timeLeftView = findViewById(R.id.timeLeftView);
        medTypeTvBox = findViewById(R.id.medTypetextView);
    }

    private void queryMedication(final int medId) {
            AddMedViewModelFactory factory = new AddMedViewModelFactory(db, medId);
        final AddMedicationViewModel viewModel =
                ViewModelProviders.of(this, factory).get(AddMedicationViewModel.class);
        viewModel.getMed().observe(this, new Observer<MedEntry>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable MedEntry medEntry) {
                viewModel.getMed().removeObserver(this);
                Log.d(TAG, "Receiving data update from LiveData Updating...");
               populateUI(medEntry);
            }
        });

    }
    private void populateUI(MedEntry medEntry) {
        if (medEntry == null)
            return;
        int medTaken = medEntry.getMedicinesTaken();
        swallowCount = medTaken;


        String startDate = dateFormat.format(medEntry.getStartDate());
        String endDate = dateFormat.format(medEntry.getEndDate());


        Date lstTaken = medEntry.getTimeTaken();

        long now = System.currentTimeMillis();
        final Date startingOn = medEntry.getStartDate();
        Date endsOn = medEntry.getEndDate();
        final long startsOn = DateConverter.toTimestamp(startingOn);
        final long ends = DateConverter.toTimestamp(endsOn);

        Log.i(TAG, "Medication DATE is: " + startDate);

        if (now < startsOn || now > ends + DAY_MILLIS) {
            int imgResId = R.drawable.swallow_btn_icon;
            setImageButtonEnabled(this, false, mSwallowBtn, imgResId);

            // mSwallowBtn.setEnabled(false);
        }

        Date today = Calendar.getInstance().getTime();
        final long todayInLong = DateConverter.toTimestamp(today);

        medicineTitle.setText(medEntry.getMedName());
        medDescriptionTextView.setText("Description: " + medEntry.getDescription());
       // medTypeTvBox.setText(medEntry.getMedicationType());
        startDateTextView.setText((startDate));
        endDateTextView.setText(endDate);
        dosageValue.setText(medEntry.getTabletsValue() + "X" + medEntry.getTimesValue());
        mSwallowCountDisplay.setText(String.valueOf(medTaken));


        String date = "never";
        if (lstTaken != null) {
            long lastTakenAt = DateConverter.toTimestamp(lstTaken);
            if (now - lastTakenAt < (DAY_MILLIS)) {
                if (now - lastTakenAt < (HOUR_MILLIS)) {
                    long minutes = Math.round((now - lastTakenAt) / MINUTE_MILLIS);
                    date = (minutes) + "m";
                } else {
                    long minutes = Math.round((now - lastTakenAt) / HOUR_MILLIS);
                    date = (minutes) + "h";
                }
            } else {
                Date dateDate = new Date(lastTakenAt);
                date = sDateFormat.format(dateDate);
            }
            date = "\u2022 " + date;
        }
        lastTakenView.setText("[Last taken: " + date + " ]");

        //increment medication taken by one to represent a dosage taken in one time
        swallowCount++;

    }
    private int[] setPercentageShown(long startDate, long endDate, String startingDate){

         final int[] percentageValue = new int[1];
        final long totalDurationInMillis = endDate - startDate;
        Date todayDate = Calendar.getInstance().getTime();
        final long todayDateInLong = DateConverter.toTimestamp(todayDate);
        String dateleo = dateFormat.format(todayDate);


        if ( startingDate.compareTo(dateleo) == 0){
            percentageValue[0] = 100;

        }else if (todayDateInLong < startDate || todayDateInLong > endDate + DAY_MILLIS){
            timeLeftView.setVisibility(View.INVISIBLE);
        }else{
            CountDownTimer ct = new CountDownTimer(totalDurationInMillis, 1000) {
                @Override
                 public void onTick(long millisUntilFinished) {
                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                    int dailyDeduct = (int) (100 / (days + 1));
                    Log.i(TAG, "DAYS are: " + days);

                    for (int i = 0; i <= days; i++) {
                        Calendar mCalender = Calendar.getInstance();
                        int mHours = mCalender.get(Calendar.HOUR_OF_DAY);
                        int mMinutes = mCalender.get(Calendar.MINUTE);
                        int mSeconds = mCalender.get(Calendar.SECOND);

                        if (((mHours * 3600) + (mMinutes * 60) + mSeconds) > 1800) {
                            // returnThisValue = returnThisValue - mPercentage;
                            SharedPreferences sPreferences = getApplicationContext().getSharedPreferences(
                                    NEWDAY_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sPreferences.edit();
                            int defaultValue = sPreferences.getInt("initialPercentage", newPercentage);
                            defaultValue = defaultValue - dailyDeduct;
                            editor.putInt("initialPercentage", defaultValue);
                            editor.commit();

                            percentageValue[0] = sPreferences.getInt("initialPercentage", newPercentage);
                            //   percentageHolder.setText(percentageValue[0]);
                            //   timeLeftView.setValue(percentageValue[0]);

                            break;
                        }
                        percentageValue[0] = newPercentage - dailyDeduct;
                        Log.i(TAG, "defaultVal is: " + 9694);
                    }
                }

                @Override
                public void onFinish() {
                    //timeLeftView.setVisibility(View.INVISIBLE);

                }
            };
            ct.start();
        }
            return percentageValue;
    }

    public void incrementSwallowCount(View view){


        final Date nowDate = DateConverter.toDate(now);

            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                private Handler mHandler = new Handler(Looper.getMainLooper());

                @Override
                public void run() {
                       db.medDao().insertMecinesTaken  (swallowCount, medicationId, nowDate);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwallowCountDisplay.setText(String.valueOf(swallowCount));
                            lastTakenView.setText("[Last taken \u2022 moments ago ]");

                        }
                    });
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_SWALLOW_COUNT.equals(key)){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.action_edit){
            editMedication();
        }
        else if (selectedItem == R.id.action_delete){
            deleteMedication();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMedication() {

        finish();
    }

    private void editMedication() {
        Intent intent = new Intent(MedDetailsActivity.this, AddMedication.class);
        intent.putExtra(AddMedication.EXTRA_MED_ID, medicationId);
        startActivity(intent);
        finish();
    }
    public static void setImageButtonEnabled(Context ImContext, boolean enabled,
                                             ImageButton img, int iconResId){
        img.setEnabled(enabled);
        Drawable originalIcon = ImContext.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        if (!enabled){
            Toast.makeText(ImContext, "Medication dates not Current!", Toast.LENGTH_LONG).show();
        }
        img.setImageDrawable(icon);

    }

    /**
     * Mutates and applies filter that converts the given drawable to gray image
     * This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar
     *
     * @param drawable item to be muted
     * @return a muted version of the given drawable with a color filter applied
     *
     */
    private static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }
}
