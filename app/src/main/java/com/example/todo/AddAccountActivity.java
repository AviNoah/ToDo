package com.example.todo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddAccountActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Lets the user either add an existing account, or register a new account.
    private Button btnSignIn, btnRegister, btnReturn;
    private EditText etEmail, etPassword;

    private long user_id;
    private String device_id;
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        oldIntent = getIntent();
        device_id = oldIntent.getStringExtra(getString(R.string.ext_device_id));
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btn_sign_in_screen_register);
        btnRegister.setOnClickListener(this);
        btnReturn = findViewById(R.id.btn_sign_in_screen_return);
        btnReturn.setOnClickListener(this);
        btnSignIn = findViewById(R.id.btn_sign_in_screen_sign_in);
        btnSignIn.setOnClickListener(this);
        etEmail = findViewById(R.id.et_sign_in_screen_email);
        etPassword = findViewById(R.id.et_sign_in_screen_password);
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            Intent intent = new Intent(this, RegisterAccountActivity.class);
            intent.putExtra(getString(R.string.ext_device_id), device_id);
            startActivity(intent);
        } else if (v == btnReturn) {
            finish();
        } else if (v == btnSignIn) {
            // Validate account login and log in as account.
            if (validateLogIn()) {
                signIn();
            }
        }
    }

    private boolean validateLogIn() {
        // Checks if the email matches password.

        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError(CredentialsChecker.ERROR_CODES.EmptyField);
            return false;
        }

        DBHelper db = new DBHelper(this);
        Cursor c = db.selectByEmailTheMostUpdated(email); //User cursor returned

        // Check if record exists for email.
        if (c.getCount() == 0) {
            etEmail.setError(CredentialsChecker.ERROR_CODES.EMAIL.NoSuchEmail);
            return false;
        }

        // Fetch user_id.
        int ci = c.getColumnIndex(DBHelper.USERS.ID);
        user_id = c.getLong(ci);

        // Check if user is already linked to this device.
        Cursor cursor = db.selectAllActiveUsersForDevice(device_id);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") long _id = cursor.getLong(cursor.getColumnIndex(DBHelper.DEVICES_USERS.USER_ID));
                if (user_id == _id) {
                    etEmail.setError(CredentialsChecker.ERROR_CODES.EMAIL.EmailIsUsed);
                    return false;
                }
                cursor.moveToNext();
            }
        }

        // Compare passwords.
        if (!db.checkPasswordForEmail(email, password)) {
            // Passwords are different.
            etPassword.setError(CredentialsChecker.ERROR_CODES.PASSWORD.WrongPass);
            return false;
        }
        return true;
    }

    private void signIn() {
        //todo check for data duplicates, for example can you sign into the same user multiple times?
        DBHelper db = new DBHelper(this);
        db.insertIntoTblUsersDevices(user_id, device_id);
        Intent intent = new Intent(this, TaskCategoriesActivity.class);
        intent.putExtra(getString(R.string.ext_user_id), user_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear screen stack.
        startActivity(intent);
    }
}