package com.cokimutai.med_manager.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

public class AddMedicationViewModel extends ViewModel {

    private LiveData<MedEntry> med;
    private MedEntry medx ;

    public AddMedicationViewModel(MedDatabase db, int id){
        med = db.medDao().loadMedicationById(id);
        //medx = db.medDao().loadMedicationTaken(medx.getId());
    }

    public LiveData<MedEntry> getMed() {
            return med;
    }

  /*  public MedEntry getMedx() {
        return medx;
    }*/
}

