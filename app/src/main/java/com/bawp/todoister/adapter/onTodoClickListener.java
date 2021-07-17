package com.bawp.todoister.adapter;

import com.bawp.todoister.model.Task;
// interface to set position and task corresponding to the onClick action
public interface onTodoClickListener {
    void onTodoClick(Task task);
    void onTodoRadioButtonClick(Task task);
}
