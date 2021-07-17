package com.bawp.todoister;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.adapter.util.Utils;
import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private EditText enterTodo;
    private ImageButton calendarButton, priorityButton, saveButton;
    private RadioGroup priorityRadioGroup;
    private Group calendarGroup;
    private RadioButton selectedRadioButton;
    private int selectedRadioButtonId;
    private CalendarView calendarView;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    public BottomSheetFragment(){

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip),
                tomorrowChip = view.findViewById(R.id.tomorrow_chip),
                nextWeekChip = view.findViewById(R.id.next_week_chip);
        todayChip.setOnClickListener(this);
        tomorrowChip.setOnClickListener(this);
        nextWeekChip.setOnClickListener(this);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(sharedViewModel.getSelectedItem().getValue() != null){
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                Utils.hideSoftKeyboard(v);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                dueDate = calendar.getTime();
            }
        });
        priorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(v);
                priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                priorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                       if(priorityRadioGroup.getVisibility() == View.VISIBLE){
                           selectedRadioButtonId = checkedId;
                           selectedRadioButton = view.findViewById(selectedRadioButtonId);

                           if(selectedRadioButton.getId() == R.id.radioButton_high){
                               priority = Priority.HIGH;
                           } else if(selectedRadioButton.getId() == R.id.radioButton_med){
                               priority = Priority.MEDIUM;
                           }else if(selectedRadioButton.getId() == R.id.radioButton_med){
                               priority = Priority.LOW;
                           }else{
                               priority = Priority.LOW;
                           }
                       }else{
                           priority = Priority.LOW;
                       }
                    }
                });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = enterTodo.getText().toString().trim();
                if(!TextUtils.isEmpty(task) && dueDate != null && priority != null){
                    Task myTask = new Task(
                            task,
                            priority,
                            dueDate,
                            Calendar.getInstance().getTime(),
                            false);
                    if(isEdit){
                        Task updatedTask = sharedViewModel.getSelectedItem().getValue();
                        if (updatedTask != null) {
                            updatedTask.setTask(task);
                            updatedTask.setDateCreated(Calendar.getInstance().getTime());
                            updatedTask.setPriority(priority);
                            updatedTask.setDueDate(dueDate);
                            TaskViewModel.update(updatedTask);
                            sharedViewModel.setIsEdit(false);
                        }
                    }else {
                        TaskViewModel.insert(myTask);
                    }

                }else{
                    Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.today_chip){
            calendar.add(Calendar.DAY_OF_YEAR,0);
            dueDate = calendar.getTime();
        }else if(id == R.id.tomorrow_chip){
            calendar.add(Calendar.DAY_OF_YEAR,1);
            dueDate = calendar.getTime();
        }else if(id == R.id.next_week_chip){
            calendar.add(Calendar.DAY_OF_YEAR,7);
            dueDate = calendar.getTime();
        }
    }
}