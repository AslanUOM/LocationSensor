package com.aslan.locationsensor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigActivity extends Activity {
    private final String KEY_NAME = "name";
    private final String KEY_USERNAME = "username";
    private final String KEY_PWD = "password";
    private DatabaseHelper dbHelper;
    private String VALUE_NAME;
    private String VALUE_USERNAME;
    private String VALUE_PWD;
    private EditText etxtName, etxtUname, etxtPassword;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        dbHelper = new DatabaseHelper(this);

        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtUname = (EditText) findViewById(R.id.etxtUname);
        etxtPassword = (EditText) findViewById(R.id.etxtPassword);

        etxtName.setSelectAllOnFocus(true);
        etxtUname.setSelectAllOnFocus(true);
        etxtPassword.setSelectAllOnFocus(true);

        Intent intent = getIntent();
        boolean isFreshTable = intent.getBooleanExtra("isFresh", false);
        if (!isFreshTable) {
            etxtName.setText(dbHelper.getRegData(KEY_NAME));
            etxtUname.setText(dbHelper.getRegData(KEY_USERNAME));
            etxtPassword.setText(dbHelper.getRegData(KEY_PWD));
        }

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VALUE_NAME = etxtName.getText().toString();
                VALUE_USERNAME = etxtUname.getText().toString();
                VALUE_PWD = etxtPassword.getText().toString();
                dbHelper.insertRegData(KEY_NAME, VALUE_NAME);
                dbHelper.insertRegData(KEY_USERNAME, VALUE_USERNAME);
                dbHelper.insertRegData(KEY_PWD, VALUE_PWD);
                ConfigActivity.this.finish();
            }
        });
    }
}
