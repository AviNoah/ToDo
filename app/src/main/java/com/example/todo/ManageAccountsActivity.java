package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ManageAccountsActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Lets the user remove saved accounts.
    private ListView lvSavedUserProfiles;
    private Button btnReturn;

    private ArrayList<UserModel> ArrayListUP;
    private AdapterUserProfileList adapterUserProfileList;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private Intent oldIntent;
    private String device_id;

    private int pos = -1; //Whether the user confirmed or not.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);
        initViews();
        getIntentExtras();
        initObjects();
        loadListView();
        loadAlertDialog();
    }

    private void loadAlertDialog() {
        builder = new AlertDialog.Builder(this);
        String title = getString(R.string.manage_accounts_alert_dialog_title);
        builder.setTitle(title);

        builder.setPositiveButton(R.string.manage_accounts_alert_dialog_yes, (dialog, which) ->
        {
            Toast.makeText(this, "Removing", Toast.LENGTH_SHORT).show();
            UserModel um = ArrayListUP.get(pos);
            ArrayListUP.remove(pos);
            DBHelper helper = new DBHelper(this);
            helper.removeFromTblUsersDevices(um.getId(), device_id);
            loadListView(); //load updated list view.
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
        adapterUserProfileList = new AdapterUserProfileList(this, ArrayListUP);
        lvSavedUserProfiles.setAdapter(adapterUserProfileList);
        lvSavedUserProfiles.setOnItemClickListener(this);
    }

    private void getIntentExtras() {
        oldIntent = getIntent();
        device_id = oldIntent.getStringExtra(getString(R.string.ext_device_id));
    }

    private void initObjects() {
        ArrayListUP = new ArrayList<UserModel>();
        DBHelper helper = new DBHelper(this);

        Cursor cursor = helper.selectAllActiveUsersForDevice(device_id); //user devices cursors, users id column only
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") long _id = cursor.getLong(0);
                UserModel um = helper.selectUserByID(_id);
                ArrayListUP.add(um);
                cursor.moveToNext();
            }
        }
    }

    private void initViews() {
        lvSavedUserProfiles = findViewById(R.id.lv_manage_accounts_screen_user_profiles);
        btnReturn = findViewById(R.id.btn_manage_accounts_screen_return);
        btnReturn.setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //todo remove selected account from saved accounts
        pos = position;
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            finish();
        }
    }
}