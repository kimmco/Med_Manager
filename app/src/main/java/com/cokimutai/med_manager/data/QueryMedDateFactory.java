package com.cokimutai.med_manager.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.Date;

public class QueryMedDateFactory extends ViewModelProvider.NewInstanceFactory {

    private final MedDatabase db;
    private final Date medDate;

    public QueryMedDateFactory(MedDatabase db, Date medDate) {
        this.db = db;
        this.medDate = medDate;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new QueryMedDateViewModel(db, medDate);
    }
}
