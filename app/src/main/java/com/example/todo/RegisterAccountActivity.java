package com.example.todo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAccountActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Lets the user register a new account.
    private Button btnReturn, btnRegister;
    private EditText etEmail, etName, etSurname, etPassword1, etPassword2;

    private Intent oldIntent;
    private String device_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras()
    {
        oldIntent = getIntent();
        //todo fix this, it gets null instead of the device_id.
        device_id = oldIntent.getStringExtra(getString(R.string.ext_device_id));
    }

    private void initViews() {
        btnReturn = findViewById(R.id.btn_register_screen_return);
        btnReturn.setOnClickListener(this);
        btnRegister = findViewById(R.id.btn_register_screen_register);
        btnRegister.setOnClickListener(this);
        etEmail = findViewById(R.id.et_register_screen_email);
        etName = findViewById(R.id.et_register_screen_name);
        etSurname = findViewById(R.id.et_register_screen_surname);
        etPassword1 = findViewById(R.id.et_register_screen_password1);
        etPassword2 = findViewById(R.id.et_register_screen_password2);
    }

    @Override
    public void onClick(View v) {
        if (v == btnReturn) {
            finish();
        }
        if (v == btnRegister) {
            // todo add to saved accounts and if it is a new account add it to database, and use it as current account.
            if (CheckFields()) {
                Register();
            }
        }
    }

    private void Register() {
        //Registers new account into database.
        UserModel um = new UserModel(etName.getText().toString(), etSurname.getText().toString(),
                etEmail.getText().toString(), etPassword1.getText().toString(), this);
        long id = um.getId();
        Toast.makeText(this, getString(R.string.register_account_creation_successful), Toast.LENGTH_SHORT).show();


        //Insert into UsersDevices table
        DBHelper db = new DBHelper(this);
        db.insertIntoTblUsersDevices(id, device_id);

        Intent intent = new Intent(this, TaskCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clear top, flush all data of other screens.
        intent.putExtra(getString(R.string.ext_user_id), id);
        startActivity(intent); //Log user into lists activity.
    }


    private boolean CheckFields() {
        // Checks the fields using "And" will return false on the first invalid field, preventing
        // overload of warning texts on screen.
        boolean isValid = (CredentialsChecker.validateEmail(etEmail) &&
                CredentialsChecker.validateName(etName, etSurname) &&
                CredentialsChecker.validatePassword(etPassword1) &&
                CredentialsChecker.matchPasswords(etPassword1, etPassword2));

        if(!isValid)
            return false; // if invalid, don't bother checking if email already exists.

        DBHelper db = new DBHelper(this);
        Cursor c = db.selectByEmailTheMostUpdated(etEmail.getText().toString()); //User cursor returned

        // Check if a record exists for email.
        if (c.getCount() != 0) {
            // No users with this email were found.
            etEmail.setError(CredentialsChecker.ERROR_CODES.EMAIL.EmailIsUnavailable);
            isValid = false;
        }

        return isValid;
        /*
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
        boolean isValid = CredentialsChecker.checkEmail(this, email);
        if (!CredentialsChecker.checkPassword(this, pass1))
            isValid = false;
        if (!CredentialsChecker.matchPasswords(this, pass1, pass2))
            isValid = false;
        if (!CredentialsChecker.checkName(this, name, surname))
            isValid = false;
        if (!CredentialsChecker.checkIfEmailAlreadyExists(this, email))
            isValid = false;
        return isValid; */
    }
}