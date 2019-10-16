package com.cokimutai.med_manager.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class SearchMedFactory extends ViewModelProvider.NewInstanceFactory {
    private final MedDatabase db;
    private final String searchText;

    public SearchMedFactory(MedDatabase db, String searchText) {
        this.db = db;
        this.searchText = searchText;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SearchMedViewModel(db, searchText);
    }

}
