package com.cokimutai.med_manager.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class AddMedViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MedDatabase db;
    private final int medId;

    public AddMedViewModelFactory(MedDatabase mDb, int id){
        db = mDb;
        medId = id;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMedicationViewModel(db, medId);
    }
}
