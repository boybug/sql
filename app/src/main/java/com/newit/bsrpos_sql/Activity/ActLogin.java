package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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

        TextView ver = (TextView) findViewById(R.id.ver);
        txt_username = (EditText) findViewById(R.id.login_name);
        txt_password = (EditText) findViewById(R.id.login_password);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.login_remember);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        ver.setText("Ver. "+ Global.getVersion(this));
        mAuth = FirebaseAuth.getInstance();

        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);

        CheckBox login_local = (CheckBox) findViewById(R.id.login_local);
        Global.isLocal = login_local.isChecked();

        if (saveLogin) {
            txt_username.setText(loginPreferences.getString("username", null));
            saveLoginCheckBox.setChecked(true);
        }

        Button bt_cmd_save = (Button) findViewById(R.id.login_login);
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                if (!Global.isNetworkAvailable(getApplicationContext())) {
                    MessageBox("ไม่มีสัญญาณเน็ทเวิร์ค");
                } else if (ActLogin.this.Validate()) {
                    showProgressDialog();
                    signIn();
                }
            }
        });
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        processLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            switch (((FirebaseAuthInvalidUserException) e).getErrorCode()) {
                                case "ERROR_USER_DISABLED":
                                    hideProgressDialog();
                                    MessageBox("บัญชีถูกระงับการใช้งาน");
                                    break;
                                case "ERROR_USER_NOT_FOUND":
                                    signUp();
                                    break;
                                case "ERROR_USER_TOKEN_EXPIRED":
                                    hideProgressDialog();
                                    MessageBox("บัญชีถูกเปลี่ยนรหัสผ่านจากเครื่องอื่น โปรดล้อกอินใหม่อีกครั้ง");
                                    break;
                                default:
                                    hideProgressDialog();
                            }
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            hideProgressDialog();
                            String err = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            MessageBox(err);
                        }
                    }
                });
    }

    private void signUp() {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        processLogin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        if (e instanceof FirebaseAuthWeakPasswordException)
                            MessageBox("รหัสผ่านง่ายเกินไป");
                        else if (e instanceof FirebaseAuthInvalidCredentialsException)
                            MessageBox("อีเมล์ไม่ถูกต้อง");
                        else if (e instanceof FirebaseAuthUserCollisionException)
                            MessageBox("อีเมล์เคยลงทะเบียนแล้ว");
                    }
                });
    }

    private void processLogin() {
        DatabaseReference refTB = FirebaseDatabase.getInstance().getReference().child("database");
        refTB.orderByChild("name").equalTo("dev").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot value = dataSnapshot.getChildren().iterator().next();
                Global.database = value.getValue(Database.class);
                String appversion = Global.getVersion(getApplicationContext());
                String latestversion = Global.database.getAppversion();

                if (!Objects.equals(latestversion, appversion)) {
                    hideProgressDialog();
                    MessageBox("เวอร์ชั่นของคุณเป็น " + appversion + " กรุณาอัพเกรดเป็นเวอร์ชั่นใหม่สุด " + latestversion);
                    mAuth.signOut();
                } else {
                    new SqlQuery(ActLogin.this, spLogin, "{call " + Global.database.getPrefix() + "loginbyemail(?,?)}", new String[]{username, password});
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
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
            txt_password.setError("ต้องกรอก 6 หลักขึ้นไป");
            isValid = false;
        }
        return isValid;
    }

    public void rememberlogin() {
        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", username);
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
                    Intent intent = new Intent(ActLogin.this, ActWhGrp.class);
                    ActLogin.this.startActivity(intent);
                    ActLogin.this.finish();
                } else {
                    deleteUser();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        hideProgressDialog();
        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MessageBox("ไม่พบอีเมล์ใน ERP โปรดติดต่อไอที");
                    }
                });
    }
}
