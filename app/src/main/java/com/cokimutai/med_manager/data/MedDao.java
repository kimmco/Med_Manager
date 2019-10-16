package com.cokimutai.med_manager.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface MedDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertMedication(MedEntry medEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMedication(MedEntry medEntry);

    @Query(" UPDATE medicines SET medicinesTaken = :medicines, taken_At = :date  " +
    " WHERE id  = :id ")
    void insertMecinesTaken(int medicines, int id, Date date);

    @Query("SELECT * FROM medicines ")
    MedEntry loadMedicationTaken();

    @Query("SELECT * FROM medicines ORDER BY StartDate ASC")
    LiveData<List<MedEntry>> loadMedication();

    @Query("SELECT * FROM medicines WHERE counter = :id")
    LiveData<MedEntry> loadMedicationById(int id);

    @Query("SELECT * FROM medicines WHERE medName = :name")
    LiveData<MedEntry> loadMedicationByName(String name);

    @Delete
    public void deletemedication(MedEntry medEntry);

    @Query("SELECT * FROM medicines WHERE StartDate < :date ")
    LiveData<List<MedEntry>> loadMedicationByDate(Date date);

}
