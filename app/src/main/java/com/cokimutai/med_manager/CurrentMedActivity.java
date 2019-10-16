package com.cokimutai.med_manager;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.data.MedEntry;
import com.cokimutai.med_manager.data.QueryMedDateFactory;
import com.cokimutai.med_manager.data.QueryMedDateViewModel;
import com.cokimutai.med_manager.reminders.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentMedActivity extends Fragment
        implements CurrentMedicationAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    public static CurrentMedicationAdapter adapter;
    private List<MedEntry> currentMedication;
    private AlarmReceiver mAlarmReceiver;

    private TextView emptyViewTv;
    private ProgressBar progressBar;

    private static final String TAG = CurrentMedActivity.class.getSimpleName();

    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long SEVEN_DAYS_MILLIS = 7 * DAY_MILLIS;


    private MedDatabase db;

    View v;

    public CurrentMedActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentMedication = new ArrayList<>();
       // currentMedication.add(new MedEntry(adapter.))

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_current_medication, container, false);

        emptyViewTv = (TextView)v.findViewById(R.id.empty_view_tv);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.current_med_recyclerview);

        adapter = new CurrentMedicationAdapter(getContext(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);

       DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0 | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                      //  int counter = Integer.parseInt(startDateBox.getText().toString());
                        int i = viewHolder.getAdapterPosition();
                        List<MedEntry> meds = adapter.getMedicines();
                        db.medDao().deletemedication(meds.get(i));
                       MedEntry e  =  meds.get(i);
                       int cc = e.getCounter();
                        mAlarmReceiver.canacelAlarm(getContext(), cc);

                    }
                });

            }
        }).attachToRecyclerView(mRecyclerView);

        db = MedDatabase.getsInstance(getActivity());
        setUpViewModel();
        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();

        return v;
    }
    public void setUpViewModel(){
        Date sevenDaysTime = getTodayPlusDays( 7);
        QueryMedDateFactory factory = new QueryMedDateFactory(db, sevenDaysTime);
        final QueryMedDateViewModel qModel =
                ViewModelProviders.of(this, factory).get(QueryMedDateViewModel.class);
        qModel.getMedications().observe(this, new Observer<List<MedEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedEntry> medEntries) {
                adapter.setMedicines(medEntries);

                if (medEntries.isEmpty()){
                    emptyViewTv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }else {
                    emptyViewTv.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_current_med_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
    private static Date getTodayPlusDays(int daysAhead){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysAhead);
        return calendar.getTime();
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(getActivity(), MedDetailsActivity.class);
        intent.putExtra(MedDetailsActivity.EXTRA_MED_ID, itemId);
        startActivity(intent);
    }
}
