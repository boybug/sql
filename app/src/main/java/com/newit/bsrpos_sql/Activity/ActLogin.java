package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActLogin extends ActBase {
    private SharedPreferences.Editor loginPrefsEditor;
    private EditText txt_username, txt_password;
    private String username, password;
    private CheckBox saveLoginCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        hideActionBar();

        txt_username = (EditText) findViewById(R.id.login_name);
        txt_password = (EditText) findViewById(R.id.login_password);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.login_remember);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);

        txt_username.setText("xclnc");
        txt_password.setText("xclnc");

        CheckBox login_local = (CheckBox) findViewById(R.id.login_local);
        Global.isLocal = login_local.isChecked();

        if (saveLogin) {
            txt_username.setText(loginPreferences.getString("username", null));
            txt_password.setText(loginPreferences.getString("password", null));
            saveLoginCheckBox.setChecked(true);
        }

        Button bt_cmd_save = (Button) findViewById(R.id.login_login);
        bt_cmd_save.setOnClickListener(v -> {

            username = txt_username.getText().toString();
            password = txt_password.getText().toString();
            if (Validate()) {
                try {
                    ResultSet rs = SqlServer.execute("{call POS.dbo.[getuser](?,?)}", new String[]{username, password});
                    if (rs != null && rs.next()) {
                        Global.usr_Id = rs.getInt("usr_Id");
                        Global.usr_name = rs.getString("usr_name");
                        loginPrefsEditor.apply();
                        Intent intent = new Intent(ActLogin.this, ActWarehouse.class);
                        startActivity(intent);
                        finish();
                    } else MessageBox("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง...กรุณาตรวจสอบ");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean Validate() {
        Boolean isValid = true;
        if (username.equals("")) {
            txt_username.setError("ต้องกรอก");
            isValid = false;
        }
        if (password.equals("")) {
            txt_password.setError("ต้องกรอก");
            isValid = false;
        }
        return isValid;
    }

    public void onBackPressed() {
        if (!saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        finishAffinity();
        moveTaskToBack(true);
    }
}
