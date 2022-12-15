package com.example.todo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public class TaskCategoriesActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    // Lists screen
    private Button btnReturn, btnAddNewListCategory;
    private TextView tvUserFullName;
    private ListView lvTaskCategories;

    private ActivityResultLauncher<Intent> mGetContent;

    private ArrayList<TasksCategory> ArrayListTC;

    private Intent oldIntent;
    private long currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_categories);
        initViews(); //initialize views.
        initResultLauncher();
    }

    private void initResultLauncher() {
        // GetContent creates an ActivityResultLauncher<String> to allow you to pass
        // in the mime type you'd like to allow the user to select
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK)
                            importList(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void importList(ActivityResult result) throws IOException, NullPointerException {
        // Read file contents, convert into JSON string and then into ArrayList<TasksCategory>.
        // After converting the JSON into an Object successfully, add to user.
        //Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show();

        String json = ImportExportHelper.readHTMLFile(result, getContentResolver());
        /*Uri uri = result.getData().getData();
        ContentResolver cr = getContentResolver();
        InputStream is = cr.openInputStream(uri);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String json = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            json += br.lines().collect(Collectors.joining()).toString();
        }*/

        Type listType = new TypeToken<ArrayList<TasksCategory>>() {
        }.getType();
        ArrayList<TasksCategory> newLists = new Gson().fromJson(json, listType);
        Toast.makeText(this, newLists.toString(), Toast.LENGTH_SHORT).show();

        DBHelper db = new DBHelper(this);
        db.selectUserByID(currentUserID).addLists(newLists, this);
        Toast.makeText(this, newLists.toString(), Toast.LENGTH_LONG).show();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIntentExtras();
        initObjects(); //initialize objects.
        loadListView(); //load list view.
    }

    private ArrayList<String> toArrLstString() {
        ArrayList<String> arr = new ArrayList<String>();
        for (int i = 0; i < ArrayListTC.size(); i++) {
            arr.add(ArrayListTC.get(i).getTitle());
        }
        return arr;
    }

    private void getIntentExtras() {
        oldIntent = getIntent();
        currentUserID = oldIntent.getLongExtra(getString(R.string.ext_user_id), -1);
    }

    private void initObjects() {
        ArrayListTC = new ArrayList<TasksCategory>();
        initDefaultLists();
        DBHelper helper = new DBHelper(this);
        Cursor cursor = helper.selectAllListsOfUserID(currentUserID); //listsUsers cursor, list id column only
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") long _id = cursor.getLong(0);
                TasksCategory tc = helper.selectListByID(_id);
                ArrayListTC.add(tc);
                cursor.moveToNext();
            }
        }

        UserModel um = helper.selectUserByID(currentUserID);
        String text = getString(R.string.task_categories_logged_in_as) + " " + um.getFullName();
        tvUserFullName.setText(text);
    }

    private void loadListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toArrLstString());
        lvTaskCategories.setAdapter(adapter);
        lvTaskCategories.setOnItemClickListener(this);
    }

    private void initDefaultLists() {
        //Generate default lists.
        //todo check methods
        DBHelper db = new DBHelper(this);
        ArrayListTC.add(db.selectAllTasksDueTodayForUser(currentUserID));
        ArrayListTC.add(db.selectAllTasksPlannedForUser(currentUserID));
        ArrayListTC.add(db.selectAllTasksMarkedImportantForUser(currentUserID));
        ArrayListTC.add(db.selectAllTasksForUser(currentUserID));
    }

    private void initViews() {
        btnReturn = findViewById(R.id.btn_task_categories_return);
        btnAddNewListCategory = findViewById(R.id.btn_task_categories_add_new_list);
        lvTaskCategories = findViewById(R.id.lv_task_categories_task_categories);
        tvUserFullName = findViewById(R.id.tv_task_categories_name);

        btnReturn.setOnClickListener(this);
        btnAddNewListCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            {
                Intent intent = new Intent(this, LoginScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear screen stack.
                startActivity(intent);
            }
        } else if (v == btnAddNewListCategory) {
            //Pops a pop-up that lets user create a list.
            Intent intent = new Intent(this, CategoryDetailsActivity.class);
            intent.putExtra(getString(R.string.ext_user_id), currentUserID);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TasksScreenActivity.class);
        TasksCategory tc = ArrayListTC.get(position);

        intent.putExtra(getString(R.string.ext_list_id), tc.getId());
        intent.putExtra(getString(R.string.ext_user_id), currentUserID);
        //In case it is one of the default lists:
        intent.putExtra(getString(R.string.ext_list_type), position);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Attach menu to options resource.
        getMenuInflater().inflate(R.menu.menu_options_task_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //In the xml resource file we set the items type as <item>
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_tasks_categories_import:
                importLists();
                break;
            case R.id.menu_tasks_categories_export:
                exportLists();
                break;
            default:
                break;
        }
        return true;
    }

    private void importLists() {
        //todo imports lists into app. - will add new lists
        //Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*");
        Toast.makeText(this, "Please select a " + ImportExportHelper.fileData.type + " file.", Toast.LENGTH_SHORT).show();
        mGetContent.launch(Intent.createChooser(intent, "Select a file"));
    }

    private void exportLists() {
        // Exports the lists into a JSON format utf-8 based file with a special .TO-DO MIME
        // Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show();
        Calendar cal = Calendar.getInstance();
        String currentTime = cal.getTime().toString().replaceAll(" ", "").replaceAll(":", "_");
        ImportExportHelper.askForWritePerm("ExportedLists_" + currentTime, this, ArrayListTC);

    }
}