package com.cokimutai.med_manager;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cokimutai.med_manager.data.MainViewModel;
import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.data.MedEntry;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ALLMedication extends Fragment implements
        AllMedicationsAdapter.ItemClickHandler {

    private RecyclerView mRecyclerView;
    private static AllMedicationsAdapter adapter;

    private static final String TAG = ALLMedication.class.getSimpleName();

    private MedDatabase db;

    View v;



    public ALLMedication() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_due_medication, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.current_med_recyclerview);

        adapter = new AllMedicationsAdapter(this, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);


        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0| ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int i = viewHolder.getAdapterPosition();
                        List<MedEntry> medicines = adapter.getMedEntries();
                        db.medDao().deletemedication(medicines.get(i));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        db = MedDatabase.getsInstance(getActivity());

        setUpViewModel();

        return v;
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new AllMedicationsAdapter(this, getContext());

      //  mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mRecyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        //mRecyclerView.addItemDecoration(decoration);


        setUpViewModel();


    }*/

    private void setUpViewModel() {
        MainViewModel model = ViewModelProviders.of(this).get(MainViewModel.class);
        model.getMedications().observe(this,
                new Observer<List<MedEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<MedEntry> medEntries) {
                        Log.d(TAG, "Updatinhg list of medication from Livedata in viewModel");
                        adapter.setMedEntries(medEntries);

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.menu_all_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                medicationSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter as you type
                medicationSearchQuery(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClickHandler(int itemId) {
        Intent intent = new Intent(getActivity(), MedDetailsActivity.class);
        intent.putExtra(MedDetailsActivity.EXTRA_MED_ID, itemId);
        startActivity(intent);
    }
    private void medicationSearchQuery(String searchText) {

    }
}
