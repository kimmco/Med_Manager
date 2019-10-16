package com.cokimutai.med_manager.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MedCounterViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MedDatabase db;
    private final int medCounter;

    public MedCounterViewModelFactory(MedDatabase mDb, int medCount){
        db = mDb;
        medCounter = medCount;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MedCounterViewModel(db, medCounter);
    }
}
