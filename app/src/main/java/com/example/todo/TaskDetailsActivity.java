package com.example.todo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaskDetailsActivity extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    //Lets user change details about a task(due date, is important) and change its contents.
    //Is also the screen gui for creating a new task.

    private Button btnReturn, btnDelete, btnMarkAsImportant, btnAddToMyday, btnChangeDueDate;
    private EditText etTitle, etSideNote;

    private DatePickerDialog dialog;
    private Calendar calDueDate;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private Intent oldIntent;
    private long list_id, task_id;
    private Task t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        oldIntent = getIntent();
        getIntentExtras();
        initDatePicker();
        loadAlertDialog();

        //Check task.
        isMarkedInMyDay();
        isMarkedImportant();
        isDue();
    }

    private void initViews() {
        btnReturn = findViewById(R.id.btn_task_details_return);
        btnReturn.setOnClickListener(this);
        btnDelete = findViewById(R.id.btn_task_details_delete);
        btnMarkAsImportant = findViewById(R.id.btn_task_details_mark_important);
        btnAddToMyday = findViewById(R.id.btn_task_details_add_to_myday);
        btnChangeDueDate = findViewById(R.id.btn_task_details_add_due_date);
        etTitle = findViewById(R.id.et_task_details_name);
        etSideNote = findViewById(R.id.et_task_details_side_note);

        btnAddToMyday.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnChangeDueDate.setOnClickListener(this);
        btnMarkAsImportant.setOnClickListener(this);
    }

    private void getIntentExtras() {
        list_id = oldIntent.getLongExtra(getString(R.string.ext_list_id), -1);
        task_id = oldIntent.getLongExtra(getString(R.string.ext_task_id), -1);

        if (task_id != -1) {
            DBHelper dbHelper = new DBHelper(this);
            t = dbHelper.selectTaskByID(task_id);
        } else {
            //add to my day, set due date to today.
            t = new Task("New task", "", false, this);
            task_id = t.getId();
            DBHelper db = new DBHelper(this);
            db.insertIntoTblTasksLists(list_id, task_id);
        }

        etTitle.setText(t.getTitle());
        etSideNote.setText(t.getContent());
        calDueDate = t.getAssignedDate();
        btnChangeDueDate.setText(t.getStringFromDate()); //Update text field.
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener listener = this;
        Calendar cal;
        cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        dialog = new DatePickerDialog(this, style, listener, y, m, d);
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis()); //Forbids you from adding past dates.
    }

    private void loadAlertDialog() {
        builder = new AlertDialog.Builder(this);
        String title = getString(R.string.manage_accounts_alert_dialog_title);
        builder.setTitle(title);

        builder.setPositiveButton(R.string.manage_accounts_alert_dialog_yes, (dialog, which) ->
        {
            Toast.makeText(this, "Removing", Toast.LENGTH_SHORT).show();
            DeleteTask();
            Toast.makeText(this, "Removed successfully!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        builder.setNegativeButton(R.string.manage_accounts_alert_dialog_no, (dialog, which) ->
        {
            Toast.makeText(this, "Dismissed", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        alertDialog = builder.create();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Attach menu to options resource.
        getMenuInflater().inflate(R.menu.menu_options_task_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //In the xml resource file we set the items type as <item>
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_tasks_details_share:
                shareTask();
                break;
            default:
                break;
        }
        return true;
    }

    private void shareTask() {
        //Kick user to a screen that lets him select a list to share a task with.

    }
*/

    private void isMarkedImportant() {
        //Check if task is important, color text accordingly - red means no, blue means yes.
        if (t.isImportant()) {
            btnMarkAsImportant.setTextColor(getResources().getColor(R.color.blue));
            btnMarkAsImportant.setText(R.string.task_details_added_important);
        } else {
            btnMarkAsImportant.setTextColor(getResources().getColor(R.color.black));
            btnMarkAsImportant.setText(R.string.task_details_add_important);
        }
    }

    private void isMarkedInMyDay() {
        //Check if task is in my day, color text accordingly - red means no, blue means yes.
        if (t.isInMyDay()) {
            btnAddToMyday.setTextColor(getResources().getColor(R.color.blue));
            btnAddToMyday.setText(R.string.task_details_added_myday_text);
        } else {
            btnAddToMyday.setTextColor(getResources().getColor(R.color.black));
            btnAddToMyday.setText(R.string.task_details_add_myday_text);
        }
    }

    private void isDue() {
        //Check if task is still due, color text accordingly - red means overdue, blue means still due.
        //Change text color.
        btnChangeDueDate.setTextColor(getResources().getColor(R.color.light_red));
        if (t.isDue()) btnChangeDueDate.setTextColor(getResources().getColor(R.color.blue));
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            updateTask();
        } else if (v == btnAddToMyday) {
            AddToMyDay();
        } else if (v == btnChangeDueDate) {
            ChangeDueDate();
        } else if (v == btnMarkAsImportant) {
            MarkAsImportant();
        } else if (v == btnDelete) {
            alertDialog.show();
        }
    }

    private void MarkAsImportant() {
        t.setImportant(!t.isImportant());
        this.isMarkedImportant();
    }

    private void AddToMyDay() {
        t.setInMyDay(!t.isInMyDay());
        this.isMarkedInMyDay();
    }

    private void ChangeDueDate() {
        dialog.show();
    }

    private void updateTask() {
        DBHelper db = new DBHelper(this);
        t.setTitle(etTitle.getText().toString());
        t.setContent(etSideNote.getText().toString());
        db.updateTask(task_id, t);
        finish();
    }

    private void DeleteTask() {
        //todo deletes task off of user's list, and kicks user back to category(list).
        DBHelper db = new DBHelper(this);
        db.removeFromTblTasksLists(list_id, task_id);
        db.removeTask(task_id);
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //Months from date picker dialogs are represented from 0 to 11 - we must add one.
        calDueDate = new GregorianCalendar(year, month, dayOfMonth); //Create calendar object.
        t.setAssignedDate(calDueDate); //Update task.
        btnChangeDueDate.setText(t.getStringFromDate()); //Update text field.
        this.isDue(); //check if due.
    }
}