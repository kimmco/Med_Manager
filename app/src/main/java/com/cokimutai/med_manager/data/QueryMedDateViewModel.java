package com.cokimutai.med_manager.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class QueryMedDateViewModel extends ViewModel {
    private LiveData<List<MedEntry>> medications;

    public QueryMedDateViewModel(MedDatabase db, Date date) {
       // super(application);
       // MedDatabase db = MedDatabase.getsInstance(this.getApplication());
        medications = db.medDao().loadMedicationByDate(date);
    }

    public LiveData<List<MedEntry>> getMedications() {
        return medications;
    }
}
