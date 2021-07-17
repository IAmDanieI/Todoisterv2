package com.bawp.todoister.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.todoister.R;
import com.bawp.todoister.adapter.util.Utils;
import com.bawp.todoister.model.Task;
import com.google.android.material.chip.Chip;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<Task> taskList;
    private final onTodoClickListener onTodoClickListener;

    public RecyclerViewAdapter(List<Task> taskList, onTodoClickListener onTodoClickListener) {
        this.taskList = taskList;
        this.onTodoClickListener = onTodoClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        String formatted = Utils.formatDate(task.getDueDate());

        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        },
                new int[]{
                        Color.LTGRAY, // disabled
                        Utils.priorityColor(task)
                });

        holder.task.setText(task.getTask());
//        Paints calendar icon on task in the color of priority
//        holder.todayChip.setText(formatted);
//        holder.todayChip.setTextColor(Utils.priorityColor(task));
//        holder.todayChip.setChipIconTint(colorStateList);
        holder.priorityImage.setColorFilter(Utils.priorityColor(task));
        holder.radioButton.setButtonTintList(colorStateList);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AppCompatRadioButton radioButton;
        public AppCompatTextView task;
        public Chip todayChip;
        public ImageView priorityImage;
        public ImageView deleteButton;

        onTodoClickListener viewOnTodoClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.todo_radio_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            task = itemView.findViewById(R.id.todo_row_todo);
            todayChip = itemView.findViewById(R.id.todo_row_chip);
            priorityImage = itemView.findViewById(R.id.todo_row_priority);

            this.viewOnTodoClickListener = onTodoClickListener;

            // attach onClickListener to each row
            itemView.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Task currentTask = taskList.get(getAdapterPosition());
            int id = v.getId();
            if (id == R.id.todo_row_layout) {
                onTodoClickListener.onTodoClick(currentTask);
            } else if (id == R.id.delete_button) {
                onTodoClickListener.onTodoRadioButtonClick(currentTask);
            }
        }
    }
}

