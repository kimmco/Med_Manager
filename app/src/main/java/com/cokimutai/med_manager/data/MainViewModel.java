package com.cokimutai.med_manager.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<MedEntry>> medications;

    private MedEntry medInfo;

    public MainViewModel(@NonNull Application application){
        super(application);
        MedDatabase db = MedDatabase.getsInstance(this.getApplication());
        medications = db.medDao().loadMedication();
        medInfo = db.medDao().loadMedicationTaken();

    }

    public LiveData<List<MedEntry>> getMedications() {
        return medications;
    }

}
