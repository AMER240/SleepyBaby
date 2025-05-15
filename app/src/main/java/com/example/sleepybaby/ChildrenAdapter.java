package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ChildViewHolder> {

    private List<Child> childList;
    private OnChildDeleteListener deleteListener;

    // الواجهة التي تسمح بنقل حدث الحذف إلى Activity
    public interface OnChildDeleteListener {
        void onChildDelete(int childId);
    }

    public void setOnChildDeleteListener(OnChildDeleteListener listener) {
        this.deleteListener = listener;
    }

    public ChildrenAdapter(List<Child> childList) {
        this.childList = childList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);

        holder.textViewName.setText(child.getName());
        holder.textViewAge.setText("Yaş: " + child.getAge());
        holder.textViewSleep.setText(String.format("Uyku: %02d:%02d", child.getSleepHour(), child.getSleepMinute()));
        holder.textViewWake.setText(String.format("Uyanma: %02d:%02d", child.getWakeHour(), child.getWakeMinute()));

        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onChildDelete(child.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewAge, textViewSleep, textViewWake;
        ImageView buttonDelete;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            textViewSleep = itemView.findViewById(R.id.textViewSleep);
            textViewWake = itemView.findViewById(R.id.textViewWake);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
