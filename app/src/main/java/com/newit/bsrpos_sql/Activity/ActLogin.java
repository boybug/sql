package com.newit.bsrpos_sql.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newit.bsrpos_sql.Model.Database;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ActLogin extends ActBase {
    private SharedPreferences.Editor loginPrefsEditor;
    private EditText txt_username, txt_password;
    private String username, password;
    private CheckBox saveLoginCheckBox;
    private FirebaseAuth mAuth;
    private final int spLogin = 1;


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

        mAuth = FirebaseAuth.getInstance();

        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);

        CheckBox login_local = (CheckBox) findViewById(R.id.login_local);
        Global.isLocal = login_local.isChecked();

        if (saveLogin) {
            txt_username.setText(loginPreferences.getString("username", null));
            txt_password.setText(loginPreferences.getString("password", null));
            saveLoginCheckBox.setChecked(true);
        }

        Button bt_cmd_save = (Button) findViewById(R.id.login_login);
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();

                if (ActLogin.this.Validate()) {
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                mAuth.createUserWithEmailAndPassword(username, password)
                                        .addOnCompleteListener(ActLogin.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) processLogin();
                                                else hideProgressDialog();
                                            }
                                        });
                            } else {
                                processLogin();
                            }
                        }
                    });
                }
            }
        });
    }

    private void processLogin() {
        DatabaseReference refTB = FirebaseDatabase.getInstance().getReference().child("database");
        refTB.orderByChild("name").equalTo("dev").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot value = dataSnapshot.getChildren().iterator().next();
                    Global.database = value.getValue(Database.class);
                    String appversion = ActLogin.this.getVersion(getApplicationContext());
                    String latestversion = Global.database.getAppversion();
                    if (Objects.equals(latestversion, appversion)) {
                        new SqlQuery(ActLogin.this, spLogin, "{call POS.dbo.loginbyemail(?,?)}", new String[]{username, password});
                    } else {
                        hideProgressDialog();
                        MessageBox("เวอร์ชั่นของคุณเป็น " + appversion + " กรุณาอัพเกรดเป็นเวอร์ชั่นใหม่สุด " + latestversion);
                        mAuth.signOut();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean Validate() {
        Boolean isValid = true;
        if (username.equals("")) {
            txt_username.setError("ต้องกรอก");
            isValid = false;
        }
        if (password.equals("") || password.length() < 6) {
            txt_password.setError("ต้องกรอกมากกว่า 6 หลัก");
            isValid = false;
        }
        return isValid;
    }

    public void rememberlogin() {
        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", username);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }

    public void onBackPressed() {
        if (!saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        finishAffinity();
        moveTaskToBack(true);
    }

    @Override
    public void processFinish(ResultSet rs, int tag) {
        try {
            if (tag == this.spLogin) {
                if (rs != null && rs.next() && rs.getInt("usr_Id") > 0) {
                    Global.user = new User(rs.getInt("usr_Id"), rs.getString("login_name"), rs.getString("usr_name"), rs.getBoolean("admin"), rs.getBoolean("deleteorder"), rs.getString("password"), rs.getString("email"));
                    loginPrefsEditor.apply();
                    rememberlogin();
                    hideProgressDialog();
                    Intent intent = new Intent(ActLogin.this, ActWarehouse.class);
                    ActLogin.this.startActivity(intent);
                    ActLogin.this.finish();
                } else {
                    hideProgressDialog();
                    ActLogin.this.MessageBox("ชื่อผู้ใช้หรือรหัสผ่านไม่ปรากฎใน ERP");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
