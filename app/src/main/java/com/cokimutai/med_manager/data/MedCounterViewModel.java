package com.cokimutai.med_manager.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class MedCounterViewModel extends ViewModel {
    private LiveData<MedEntry> medCounter;


    public MedCounterViewModel(MedDatabase db, int counter){

    }

    public LiveData<MedEntry> getMedCounter() {
        return medCounter;
    }
}
