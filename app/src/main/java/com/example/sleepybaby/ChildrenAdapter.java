package com.example.sleepybaby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ChildViewHolder> {
    private List<Child> childList;
    private OnChildClickListener clickListener;
    private OnChildDeleteListener deleteListener;

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    public interface OnChildDeleteListener {
        void onChildDelete(Child child, int position);
    }

    public ChildrenAdapter(List<Child> childList) {
        this.childList = childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
        notifyDataSetChanged();
    }

    public void setOnChildClickListener(OnChildClickListener listener) {
        this.clickListener = listener;
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
        holder.textViewAge.setText(child.getAge() + " yaşında");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onChildClick(child);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onChildDelete(child, position);
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