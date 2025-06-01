package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {
    private List<SleepRecord> sleepRecords;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    public SleepHistoryAdapter(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        this.dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("tr"));
        this.timeFormat = new SimpleDateFormat("HH:mm", new Locale("tr"));
    }

    public void setSleepRecords(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_sleep_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepRecord record = sleepRecords.get(position);
        
        // Tarih ve süre
        holder.textViewDate.setText(dateFormat.format(record.getStartTime()));
        holder.textViewDuration.setText(String.format(Locale.getDefault(), 
            "%.1f saat", record.getDurationMinutes() / 60.0));

        // Başlangıç ve bitiş zamanları
        holder.textViewStartTime.setText(timeFormat.format(record.getStartTime()));
        holder.textViewEndTime.setText(timeFormat.format(record.getEndTime()));

        // Uyku kalitesi
        holder.ratingBarQuality.setRating(record.getQuality());
        String qualityText;
        switch (record.getQuality()) {
            case 5:
                qualityText = "Çok İyi";
                break;
            case 4:
                qualityText = "İyi";
                break;
            case 3:
                qualityText = "Orta";
                break;
            case 2:
                qualityText = "Kötü";
                break;
            default:
                qualityText = "Çok Kötü";
        }
        holder.textViewQuality.setText(qualityText);

        // Notlar
        if (record.getNotes() != null && !record.getNotes().isEmpty()) {
            holder.textViewNotes.setVisibility(View.VISIBLE);
            holder.textViewNotes.setText(record.getNotes());
        } else {
            holder.textViewNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sleepRecords.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewDuration;
        TextView textViewStartTime;
        TextView textViewEndTime;
        RatingBar ratingBarQuality;
        TextView textViewQuality;
        TextView textViewNotes;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            ratingBarQuality = itemView.findViewById(R.id.ratingBarQuality);
            textViewQuality = itemView.findViewById(R.id.textViewQuality);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
        }
    }
} 