package com.example.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TasksScreenActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Lets user click on tasks in a category(list), and modify the category(edit name, delete it, change background color)
    private LinearLayout llMain;
    private Button btnReturn, btnAddTask;
    private TextView tvListTitle;
    private ListView lvTasks;

    private Intent oldIntent;
    private ArrayList<String> arrayListTasks;

    private long userId, listId;
    private int type;
    private TasksCategory currentList;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_screen);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIntentExtras();
        initObjects();
        loadListView();
        loadAlertDialog();
    }

    @SuppressLint("NewApi")
    private void getIntentExtras() {
        //Change the background color depending on user's preference.
        oldIntent = getIntent();
        listId = oldIntent.getLongExtra(getString(R.string.ext_list_id), -1);
        userId = oldIntent.getLongExtra(getString(R.string.ext_user_id), -1);
        type = oldIntent.getIntExtra(getString(R.string.ext_list_type), -1);

        DBHelper db = new DBHelper(this);
        TasksCategory list;

        btnAddTask.setClickable(false); //If it is a default list, cannot add tasks to it.
        btnAddTask.setText("");

        if (listId == -1) {
            //A default list.
            if (type == 0) {
                //MyDay list
                list = db.selectAllTasksDueTodayForUser(userId);
                list.setColorRID(getColor(list.getColorRID()));
            } else if (type == 1) {
                //Planned list
                list = db.selectAllTasksPlannedForUser(userId);
                list.setColorRID(getColor(list.getColorRID()));
            } else if (type == 2) {
                //Important list
                list = db.selectAllTasksMarkedImportantForUser(userId);
                list.setColorRID(getColor(list.getColorRID()));
            } else {
                //All tasks list
                list = db.selectAllTasksForUser(userId);
                list.setColorRID(getColor(list.getColorRID()));
            }
        } else {
            //Not a default list.
            list = db.selectListByID(listId);
            btnAddTask.setClickable(true);
            btnAddTask.setText(R.string.tasks_screen_add_task_button_text);
        }

        int color = list.getColorRID();
        llMain.setBackgroundColor(color);

        //Change the title for the category.
        String name = list.getTitle();
        if (name.isEmpty())
            name = "New list";
        tvListTitle.setText(name);
        currentList = list;
    }

    private void loadAlertDialog() {
        builder = new AlertDialog.Builder(this);
        String title = getString(R.string.manage_accounts_alert_dialog_title);
        builder.setTitle(title);

        builder.setPositiveButton(R.string.manage_accounts_alert_dialog_yes, (dialog, which) ->
        {
            Toast.makeText(this, "Removing", Toast.LENGTH_SHORT).show();
            deleteList();
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

    private void loadListView() {
        // loads tasks onto list screen
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListTasks);
        lvTasks.setAdapter(adapter);
        lvTasks.setOnItemClickListener(this);
    }

    private void initObjects() {
        arrayListTasks = new ArrayList<String>();
        ArrayList<Task> arr = currentList.getTasks();
        for (Task t : arr) {
            arrayListTasks.add(t.getTitle());
        }

    }

    private void initViews() {
        llMain = findViewById(R.id.ll_tasks_screen_main);
        lvTasks = findViewById(R.id.lv_tasks_screen_tasks);
        btnReturn = findViewById(R.id.btn_tasks_screen_return);
        btnReturn.setOnClickListener(this);
        btnAddTask = findViewById(R.id.btn_task_screen_add_task);
        btnAddTask.setOnClickListener(this);
        tvListTitle = findViewById(R.id.tv_tasks_screen_title);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            {
                Intent intent = new Intent(this, TaskCategoriesActivity.class);
                intent.putExtra(getString(R.string.ext_user_id), userId);
                startActivity(intent);
            }
        } else if (v == btnAddTask) {
            Intent intent = new Intent(this, TaskDetailsActivity.class);
            //todo add an extra that specifies this is a NEW task.
            String name = "New task";
            intent.putExtra(getString(R.string.ext_list_id), currentList.getId());
            intent.putExtra(getString(R.string.ext_list_type), type);
            intent.putExtra(getString(R.string.ext_task_id), -1);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //todo fetch data for task and put into extras.
        Task task = currentList.getTasks().get(position);

        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(getString(R.string.ext_task_id), task.getId());
        intent.putExtra(getString(R.string.ext_list_id), currentList.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Attach menu to options resource.
        if (listId != -1) getMenuInflater().inflate(R.menu.menu_options_tasks_screen, menu);
        else getMenuInflater().inflate(R.menu.menu_options_tasks_screen_print_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //In the xml resource file we set the items type as <item>
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_tasks_screen_edit:
                editList();
                break;
            case R.id.menu_tasks_screen_print:
                printList();
                break;
            case R.id.menu_tasks_screen_delete:
                alertDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void printList() {
        // Saves to pdf in downloads folder, and opens the document with the default pdf reader.

        String fileName = "test_pdf2";
        PrintHelper.createPDFFileFromName(fileName, this, currentList, userId);
    }

    private void editList() {
        //todo kicks the user into the activity that creates/edits new lists.
        Intent intent = new Intent(this, CategoryDetailsActivity.class);
        intent.putExtra(getString(R.string.ext_user_id), userId);
        intent.putExtra(getString(R.string.ext_list_id), currentList.getId());
        startActivity(intent);
    }

    private void deleteList() {
        // Confirm deletion; deletes list if confirmed.
        DBHelper db = new DBHelper(this);
        db.removeFromTblListsUsers(userId, currentList.getId());
        Intent intent = new Intent(this, TaskCategoriesActivity.class);
        intent.putExtra(getString(R.string.ext_user_id), userId);
        startActivity(intent);
    }
}