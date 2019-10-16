package com.cokimutai.med_manager.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity( /*
        primaryKeys = {
                "id", "medName"
        },*/
        tableName = "medicines")
public class MedEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int counter;
    @NonNull
    private String medName;
    @NonNull
    private String description;
    @NonNull
    private String medicationType;
    @NonNull
    private int tabletsValue;
    @NonNull
    private int timesValue;
   // private String startingTime;
    @NonNull
    @ColumnInfo(name = "StartDate")
    private Date startDate;
    @NonNull
    @ColumnInfo(name = "EndDate")
    private Date endDate;
    @NonNull
    private int medicinesTaken;
    @Nullable
    @ColumnInfo(name = "taken_At")
    private Date timeTaken;


    @Ignore
    public MedEntry(int counter,String medName,String description,String medicationType,int tabletsValue,int timesValue,
                    Date startDate,Date endDate, int medicinesTaken, Date timeTaken){
        this.counter = counter;
        this.medName = medName;
        this.description = description;
        this.medicationType = medicationType;
        this.tabletsValue = tabletsValue;
        this.timesValue = timesValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.medicinesTaken = medicinesTaken;
        this.timeTaken = timeTaken;


    }
    public MedEntry(int id, int counter, String medName,String description,String medicationType,int tabletsValue ,int timesValue,
                    Date startDate,Date endDate, int medicinesTaken, Date timeTaken){
        this.id = id;
        this.counter = counter;
        this.medName = medName;
        this.description = description;
        this.medicationType = medicationType;
        this.tabletsValue = tabletsValue;
        this.timesValue = timesValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.medicinesTaken = medicinesTaken;
        this.timeTaken = timeTaken;

    }

    @NonNull
    public int getMedicinesTaken() {
        return medicinesTaken;
    }

    public void setMedicinesTaken(@NonNull int medicinesTaken) {
        this.medicinesTaken = medicinesTaken;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getMedicationType() {
        return medicationType;
    }

    public void setMedicationType(@NonNull String medicationType) {
        this.medicationType = medicationType;
    }

    public void setTabletsValue(@NonNull int tabletsValue) {
        this.tabletsValue = tabletsValue;
    }

    @NonNull
    public int getTabletsValue() {
        return tabletsValue;
    }

    @NonNull
    public int getTimesValue() {
        return timesValue;
    }

    public void setTimesValue(@NonNull int timesValue) {
        this.timesValue = timesValue;
    }

    public void setMedName(@NonNull String medName) {
        this.medName = medName;
    }

    @NonNull
    public String getMedName() {
        return medName;
    }

    public void setStartDate(@NonNull Date startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public Date getStartDate() {
        return startDate;
    }

    @NonNull
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(@NonNull Date endDate) {
        this.endDate = endDate;
    }

    @NonNull
    public Date getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(@NonNull Date timeTaken) {
        this.timeTaken = timeTaken;
    }


    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    /* public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) */
}
