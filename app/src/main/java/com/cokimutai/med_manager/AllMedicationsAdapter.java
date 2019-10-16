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

public class AllMedicationsAdapter extends RecyclerView.Adapter<AllMedicationsAdapter.MedicationViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    final private ItemClickHandler itemClickHandler;

    private List<MedEntry> medEntries;

    private Context mContext;

    private SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public AllMedicationsAdapter(ItemClickHandler itemClickHandler, Context mContext) {
        this.itemClickHandler = itemClickHandler;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.medicine_list_item, viewGroup, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        MedEntry entry = medEntries.get(position);
        String medName = entry.getMedName();

        holder.nameBox.setText(medName);
    }

    @Override
    public int getItemCount() {
        if (medEntries == null) {
            return 0;
        }
        return medEntries.size();
    }

    public List<MedEntry> getMedEntries() {
        return medEntries;
    }

    public void setMedEntries(List<MedEntry> medEntries) {
        this.medEntries = medEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickHandler{
        void onItemClickHandler(int itemId);
    }

    class MedicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameBox;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameBox = (TextView)itemView.findViewById(R.id.medicine_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int medId = medEntries.get(getAdapterPosition()).getId();
            itemClickHandler.onItemClickHandler(medId);

        }
    }
}
