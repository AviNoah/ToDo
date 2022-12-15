package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginScreenActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //First activity - user logs into one of the saved accounts or creates a new one.
    private ListView lvUserProfiles;
    private Button btnAddAccount, btnManageAccounts;

    private ArrayList<UserModel> ArrayListUP;
    private AdapterUserProfileList adapterUserProfileList;

    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceID();
        initObjects();
        loadListView();
    }

    private void getDeviceID() {
        //todo get device mac address.
        device_id = "-3";
    }

    private void loadListView() {
        if (ArrayListUP.isEmpty()) {
            // Kick user to add account screen, since he has no saved accounts.
            Intent intent = new Intent(this, AddAccountActivity.class);
            intent.putExtra(getString(R.string.ext_device_id), device_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        adapterUserProfileList = new AdapterUserProfileList(this, ArrayListUP);
        lvUserProfiles.setAdapter(adapterUserProfileList);
        lvUserProfiles.setOnItemClickListener(this);
    }

    private void initObjects() {
        ArrayListUP = new ArrayList<UserModel>();

        DBHelper helper = new DBHelper(this);
        // todo fix device id
        Cursor cursor = helper.selectAllActiveUsersForDevice(device_id);
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
        lvUserProfiles = findViewById(R.id.lv_login_page_user_profiles);
        btnAddAccount = findViewById(R.id.btn_log_in_screen_add_account);
        btnManageAccounts = findViewById(R.id.btn_login_page_manage_accounts);
        btnAddAccount.setOnClickListener(this);
        btnManageAccounts.setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long _id = ArrayListUP.get(position).getId();
        Intent intent = new Intent(this, TaskCategoriesActivity.class);
        intent.putExtra(getString(R.string.ext_user_id), _id);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddAccount) {
            Intent intent = new Intent(this, AddAccountActivity.class);
            intent.putExtra(getString(R.string.ext_device_id), device_id);
            startActivity(intent);
        } else if (v == btnManageAccounts) {
            Intent intent = new Intent(this, ManageAccountsActivity.class);
            intent.putExtra(getString(R.string.ext_device_id), device_id);
            startActivity(intent);
        }
    }
}