package com.cokimutai.med_manager.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class SearchMedViewModel extends ViewModel {

    private LiveData<MedEntry> searchMedication;

    public SearchMedViewModel( MedDatabase db, String searchText) {

        searchMedication = db.medDao().loadMedicationByName(searchText);
    }

    public LiveData<MedEntry> getSearchMedication() {
        return searchMedication;
    }
}
