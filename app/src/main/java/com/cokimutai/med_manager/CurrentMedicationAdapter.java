package com.cokimutai.med_manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cokimutai.med_manager.data.MedEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class CurrentMedicationAdapter extends
        RecyclerView.Adapter<CurrentMedicationAdapter.CurrentViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";

    final private ItemClickListener mItemClickListener;

    private List<MedEntry> mMedicineEntries;
    private Context mContext;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public CurrentMedicationAdapter(Context context, ItemClickListener listener ){
        mContext = context;
        mItemClickListener = listener;
    }


    @NonNull
    @Override
    public CurrentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.medicine_list_item, parent, false);
        return new CurrentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentViewHolder holder, int position) {
       final MedEntry medEntry = mMedicineEntries.get(position);
        String medName = medEntry.getMedName();
        String startDate = dateFormat.format(medEntry.getStartDate());
        int counter = medEntry.getCounter();

        holder.nameBox.setText(medName);

    }

    @Override
    public int getItemCount() {
        if (mMedicineEntries == null) {
            return 0;
        }
        return mMedicineEntries.size();
    }
    public List<MedEntry> getMedicines(){
        return mMedicineEntries;
    }
    public void setMedicines(List<MedEntry> medEntries){
        mMedicineEntries  = medEntries;
         notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    class CurrentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameBox;

        public CurrentViewHolder( View itemView) {
            super(itemView);
            nameBox = itemView.findViewById(R.id.medicine_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int medId = mMedicineEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(medId);

        }
    }
}
