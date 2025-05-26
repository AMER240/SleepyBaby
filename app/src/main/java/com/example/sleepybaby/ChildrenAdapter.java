package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ChildViewHolder> {
    private List<Child> childList;
    private OnChildDeleteListener deleteListener;

    public interface OnChildDeleteListener {
        void onChildDelete(int childId);
    }

    public ChildrenAdapter(List<Child> childList) {
        this.childList = childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
        notifyDataSetChanged();
    }

    public void setOnChildDeleteListener(OnChildDeleteListener listener) {
        this.deleteListener = listener;
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
        holder.textViewAge.setText(String.valueOf(child.getAge()));
        
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onChildDelete(child.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList != null ? childList.size() : 0;
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewAge;
        ImageButton btnDelete;

        ChildViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
