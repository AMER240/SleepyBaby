package com.example.sleepybaby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private List<Child> children;
    private OnChildClickListener listener;
    private DatabaseHelper databaseHelper;
    private Context context;

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    public ChildAdapter(Context context, List<Child> children, OnChildClickListener listener) {
        this.context = context;
        this.children = children;
        this.listener = listener;
        this.databaseHelper = new DatabaseHelper(context);
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
        Child child = children.get(position);
        holder.bind(child);
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public void removeChild(int position) {
        if (position >= 0 && position < children.size()) {
            Child child = children.get(position);
            databaseHelper.deleteChild(child.getId());
            children.remove(position);
            notifyItemRemoved(position);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewAge;
        private ImageButton buttonDelete;

        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAge = itemView.findViewById(R.id.textViewAge);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onChildClick(children.get(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeChild(position);
                }
            });
        }

        void bind(Child child) {
            textViewName.setText(child.getName());
            textViewAge.setText(child.getAge() + " yaşında");
        }
    }
}
