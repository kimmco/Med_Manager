package com.cokimutai.med_manager;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cokimutai.med_manager.data.AddMedViewModelFactory;
import com.cokimutai.med_manager.data.AddMedicationViewModel;
import com.cokimutai.med_manager.data.DateConverter;
import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.data.MedEntry;
import com.cokimutai.med_manager.reminders.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedication extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText medNameEditText;
    private EditText medDescriptionEditText;
    private EditText medStartDateEditText;
    private EditText medEndDateEditText;
    private TextView startinTime;
    private Spinner mMedTypeSpinner;
    private Context mContext;
    private SimpleCursorAdapter mAdapterMedTypes;
    private String[] categories = {
            "Tablet",
            "Liquid",
            "Capsule"
    };
    ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    private int hr;
    private int min;


    private Calendar mCalendar;

    public static Date beginReminderDate, stopReminderDate;

    //Extra for the medId to be received via intent
    public static final String EXTRA_MED_ID = "extraMedId";

    //Extra for the default medication id to be used when not in update mode
    private static final int DEFAULT_MED_ID = -1;
    public static final String INSTANCE_MED_ID = "instanceMedId";

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    private static final String TAG = AddMedication.class.getSimpleName();

    NumberPicker medicationQuantityPicker;
    NumberPicker numberOfTimesPicker;
    private int mYear, mMonth, mHour, mMinute, mDay;

    //initialize the medicationQuantityPicker value to 0.

    static long mills;
    static Date date ;

    private long mRepeatTime;

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    private int pickedValue = 0;
    private int numberOfTimesPicked = 0;
    private int totalMedicationTaken = 0;
    private static final int hoursInAday = 12;

    private int mMedId = DEFAULT_MED_ID;

    private MedDatabase db;
    SharedPreferences counterPrefs;
    int counterNewValue = 0;

    public static final String COUNTER_PREFERENCES = "ConterPrefs";
    private String choosenMedType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null){
           actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_48px);
        }
        db = MedDatabase.getsInstance(getApplicationContext());
        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) +1;
        mDay = mCalendar.get(Calendar.DATE);

        findViewByTheirId();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MED_ID)) {
            mMedId = savedInstanceState.getInt(INSTANCE_MED_ID, DEFAULT_MED_ID);
        }

        numberOfTimesPicker.setMinValue(0);
        numberOfTimesPicker.setMaxValue(3);
        numberOfTimesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberOfTimesPicked = newVal;
            }
        });


        medicationQuantityPicker.setMinValue(0);
        medicationQuantityPicker.setMaxValue(8);

        medicationQuantityPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
                pickedValue = newValue;

            }
        });

        startinTime.setOnClickListener(this);
        medStartDateEditText.setOnClickListener(this);
        medEndDateEditText.setOnClickListener(this);
        mMedTypeSpinner.setOnItemSelectedListener(this);
        mMedTypeSpinner.setSelection(0);


        startinTime.setText(String.format("%02d", mCalendar.get(Calendar.HOUR)) +":"+
                String.format("%02d", mCalendar.get(Calendar.MINUTE)));
        mContext = getApplicationContext();

        mAdapterMedTypes = new SimpleCursorAdapter(mContext, android.R.layout.simple_spinner_item, null,
                new String[] {"Tablet", "Liquid"}, new int[] {android.R.id.text1}, 0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMedTypeSpinner.setAdapter(adapter);

    }

    private void findViewByTheirId() {
        medNameEditText = (EditText)findViewById(R.id.medicine_name_edit_text);
        medDescriptionEditText = (EditText)this.findViewById(R.id.medicine_description_edit_text);
        medStartDateEditText = (EditText)this.findViewById(R.id.start_date_edit_text);
        medEndDateEditText = (EditText)findViewById(R.id.date_ends_edit_text);
        startinTime = findViewById(R.id.starting_time);
        medicationQuantityPicker = findViewById(R.id.numberPicker1);
        numberOfTimesPicker = findViewById(R.id.number_of_times_picker);
        mMedTypeSpinner = findViewById(R.id.spinner_med_type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Pre-populate the UI with user previously entered data:
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MED_ID)){
            setTitle("Edit Medication");

            if (mMedId == DEFAULT_MED_ID) {
                mMedId = intent.getIntExtra(EXTRA_MED_ID, DEFAULT_MED_ID);
                AddMedViewModelFactory factory = new AddMedViewModelFactory(db, mMedId);
                final AddMedicationViewModel viewModel = ViewModelProviders.of
                        (this, factory).get(AddMedicationViewModel.class);
                viewModel.getMed().observe(this, new Observer<MedEntry>() {
                    @Override
                    public void onChanged(@Nullable MedEntry medEntry) {
                        viewModel.getMed().removeObserver(this);
                        populateUI(medEntry);
                    }
                });
            }

        }
       // counterNewValue++;
    }
    //Pre-populate the UI with user previously entered data:
    private void populateUI(MedEntry medEntry) {
        if (medEntry == null)return;
        String startDate = dateFormat.format(medEntry.getStartDate());
        String endDate = dateFormat.format(medEntry.getEndDate());

        int _id = medEntry.getId();

        medNameEditText.setText(medEntry.getMedName());
        medDescriptionEditText.setText(medEntry.getDescription());
        medicationQuantityPicker.setValue(0);
        numberOfTimesPicker.setValue((0));
        medStartDateEditText.setText(startDate);
        medEndDateEditText.setText(endDate);
    }

    public void setUpReminder(){
        AddMedViewModelFactory factory = new AddMedViewModelFactory(db, mMedId);
        final AddMedicationViewModel viewModel = ViewModelProviders.of(this, factory).get(AddMedicationViewModel.class);
        viewModel.getMed().observe(this, new Observer<MedEntry>() {
            @Override
            public void onChanged(@Nullable MedEntry medEntry) {

               viewModel.getMed().removeObserver(this);
               setTheDate(medEntry);
            }
        });

     //   ReminderUtilities.scheduleSwallowReminder(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_medication_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.action_add_med){
            createMedication();
           // ReminderUtilities.scheduleSwallowReminder(this);
            //setUpReminder();
        }if (selectedItem == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String string = parent.getItemAtPosition(position).toString();
        choosenMedType = string;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class startDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            EditText medStartDateEditText = (EditText)getActivity().findViewById(R.id.start_date_edit_text);

            medStartDateEditText.setText(day + "/" + (month+1) + "/" + year);
        }
    }
    public static class EndDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            EditText medEndDateEditText = (EditText)getActivity().findViewById(R.id.date_ends_edit_text);
            medEndDateEditText.setText(day + "/" + (month+1) + "/" + year);
        }
    }
    public void createMedication(){
        final String medName = medNameEditText.getText().toString();
        final String description = medDescriptionEditText.getText().toString();
        String startDateString = medStartDateEditText.getText().toString();
        String endDateString = medEndDateEditText.getText().toString();
        String startingTime = startinTime.getText().toString();
        int mCount;

        try {
            final Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDateString);
            final Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDateString);
            final Date startTime = new SimpleDateFormat("HH:mm").parse(startingTime);

            long startOn = DateConverter.toTimestamp(startDate);
            long endOn = DateConverter.toTimestamp(endDate);
            //final long timeStart = DateConverter.toTimestamp(startTime);

            if ((pickedValue * numberOfTimesPicked) > 8){
                Toast.makeText(this," Error: You cannot take more than 8 medicines in a day!",
                        Toast.LENGTH_LONG).show();
                return;
            }else if (medNameEditText.getText().toString().length() == 0){
                medNameEditText.setError("Enter Medicine name!");
                return;
            }else if (medDescriptionEditText.getText().toString().isEmpty()) {
                medDescriptionEditText.setError("Enter Description");
                return;
            }else if (endOn < startOn){
                Toast.makeText(this," Error: End Date must be greater than Start date!",
                        Toast.LENGTH_LONG).show();
                return;
            }else if (pickedValue * numberOfTimesPicked == 0){
                Toast.makeText(this," Error: Must Enter both the number of tablets and the times!",
                        Toast.LENGTH_LONG).show();
                return;
            }else{
                SharedPreferences sharedPfrefs = getApplicationContext().getSharedPreferences(COUNTER_PREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPfrefs.edit();
                int defaultValue = sharedPfrefs.getInt("counter", counterNewValue);
                ++defaultValue;
                editor.putInt("counter", defaultValue);

                //editor.putInt("counter", counterNewValue);
                editor.commit();
                counterNewValue = sharedPfrefs.getInt("counter", counterNewValue);
            }

            AppExecutors.getsInstance().diskIO().execute(new Runnable() {

                    final MedEntry medicine = new MedEntry(counterNewValue,medName, description,choosenMedType, pickedValue, numberOfTimesPicked,
                            startDate, endDate, totalMedicationTaken, startTime);

                    @Override
                    public void run() {
                        if (mMedId == DEFAULT_MED_ID) {
                            db.medDao().insertMedication(medicine);

                            //setUpTheReminder(counterNewValue);
                            setUpTheReminder(counterNewValue);
                           counterNewValue++;


                            Log.d(TAG, "Inserted " + medicine);
                            //ReminderUtilities.scheduleSwallowReminder(getApplicationContext());
                        } else {
                            //update Medication
                            medicine.setId(mMedId);
                            db.medDao().updateMedication(medicine);
                            //setUpTheReminder();

                            Log.d(TAG, "Updated " + medicine);
                            //ReminderUtilities.scheduleSwallowReminder(getApplicationContext());
                        }


                        finish();
                    }
                });

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    public void setTheDate(MedEntry entry){
        if (entry == null) return;
        Date endDate = entry.getEndDate();
        Date startDate = entry.getEndDate();
        beginReminderDate = startDate;
        stopReminderDate = endDate;

    }

    private void setUpTheReminder( int counter) {

        final String medName = medNameEditText.getText().toString();
        //int numberOfTimesValue = medDescriptionEditText.getText().toString();
        String startDateString = medStartDateEditText.getText().toString();
        String timePicked = startinTime.getText().toString();

        String temp = startDateString.substring(0, startDateString.lastIndexOf("/"));
        int year = Integer.parseInt(startDateString.substring(startDateString.lastIndexOf("/") + 1));
        int day = Integer.parseInt(temp.substring(0,temp.lastIndexOf("/")));
        int month = Integer.parseInt(temp.substring(temp.lastIndexOf("/") + 1));

        int hour = Integer.parseInt(timePicked.substring(0, timePicked.lastIndexOf(":")));
        int minute = Integer.parseInt(timePicked.substring(timePicked.lastIndexOf(":") + 1));

        mMonth = month;
        mDay = day;
        mYear = year;
        mHour = hour;
        mMinute = minute;

        long repeatNoOfTimes = hoursInAday / numberOfTimesPicked;
        mRepeatTime = repeatNoOfTimes * milHour;

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        //new AlarmReceiver().setRepeatAlarm(getApplicationContext(),mCalendar,counter,medName,mRepeatTime);
        new AlarmReceiver().scheduleAlarm(getApplicationContext(), counter, medName);


    }

    @Override
    public void onClick(View v) {
        if ( v == startinTime ){
            final TextView clickedView = (TextView) v;
            Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minutex = mCurrentTime.get(Calendar.MINUTE);

            final TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    clickedView.setText(String.format("%02d", hourOfDay) +":"+
                            String.format("%02d", minute));
                    hr = hourOfDay;
                    min = minute;

                }
            },hour, minutex, true
            );
            mTimePicker.setTitle("Select Time\n");
            mTimePicker.show();

        }else if(v == medStartDateEditText || v == medEndDateEditText ){
            final  TextView clickedView = (TextView) v;
            final  Calendar mCurrentDate = Calendar.getInstance();
            int mYear = mCurrentDate.get(Calendar.YEAR);
            int mMonth = mCurrentDate.get(Calendar.MONTH);
            int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int year, int monthOfYear, int dayOfMonth) {
                    clickedView.setText(String.format("%02d", dayOfMonth) + "/" +
                            String.format("%02d", monthOfYear + 1) + "/"
                            + String.format("%04d", year));
                }
            },mYear, mMonth, mDay);
            mDatePicker.setTitle("Select date");
            mDatePicker.show();

            }
    }

}
