package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CategoryDetailsActivity extends AppCompatActivity
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    //Lets the user edit or create a new category(list).

    private RadioGroup rgColors;
    private Button btnReturn, btnCreate;
    private EditText etListName;
    private TextView tvTitle;

    private int listColorRID = R.color.light_gray;
    private int[] colors;
    private long userId;
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRadioGroup();
        getIntentExtras();
    }

    private void getIntentExtras() {
        Intent oldIntent = getIntent();
        userId = oldIntent.getLongExtra("user_id", -1);
        listId = oldIntent.getLongExtra("list_id", -1);

        btnCreate.setText(getString(R.string.category_details_btn_create_create));
        tvTitle.setText(getString(R.string.category_details_title_text_create_list));
        if (listId != -1) {
            DBHelper db = new DBHelper(this);
            TasksCategory list = db.selectListByID(listId);
            etListName.setText(list.getTitle());
            listColorRID = list.getColorRID();
            int i = 0;
            while (colors[i] != listColorRID) i++;
            rgColors.check(i+1);
            btnCreate.setText(getString(R.string.category_details_btn_create_update));
            tvTitle.setText("Customize " + list.getTitle());
        }
    }

    private void loadRadioGroup() {
        //Populates the group with colors.
        rgColors.removeAllViews();
        colors = getResources().getIntArray(R.array.colors);
        for (int color : colors) {
            //Choose which colors will appear when marked or unmarked.
            ColorStateList csl = new ColorStateList(
                    new int[][]
                            {
                                    new int[]{-android.R.attr.state_enabled}, // Disabled
                                    new int[]{android.R.attr.state_enabled}   // Enabled
                            },
                    new int[]
                            {
                                    color, //disabled
                                    color //enabled
                            }
            );
            RadioButton rb = new RadioButton(this);
            rb.setButtonTintList(csl);
            rgColors.addView(rb);
        }
        rgColors.setOnCheckedChangeListener(this);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_category_details_title);
        rgColors = findViewById(R.id.rg_category_details_colors);
        etListName = findViewById(R.id.et_category_details_list_name);
        btnReturn = findViewById(R.id.btn_category_details_return);
        btnCreate = findViewById(R.id.btn_category_details_create_list);
        btnReturn.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            finish();
        } else if (v == btnCreate) {
            if (listId == -1) {
                createList();
                kickToListScreen();
            } else {
                updateList();
                kickToListScreen();
            }
        }
    }

    private void updateList() {
        DBHelper db = new DBHelper(this);
        TasksCategory newList = new TasksCategory(etListName.getText().toString(), listColorRID);
        db.updateList(listId, newList);
    }

    private void createList() {
        //todo creates list and adds it to categories, moves user to its screen
        String title, color;
        color = listColorRID + "";
        title = etListName.getText().toString();

        DBHelper db = new DBHelper(this);
        listId = db.insertIntoTblLists(title, color);
        db.insertIntoTblListsUsers(userId, listId);
    }

    private void kickToListScreen() {
        Intent intent = new Intent(this, TasksScreenActivity.class);
        intent.putExtra(getString(R.string.ext_list_id), listId);
        intent.putExtra(getString(R.string.ext_user_id), userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //Changes the selected color RID for the list.
        checkedId -= 1;
        checkedId %= colors.length;
        listColorRID = colors[checkedId];

    }
}