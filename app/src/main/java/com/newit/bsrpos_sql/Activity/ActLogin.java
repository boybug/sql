package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.newit.bsrpos_sql.Model.Database;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ActLogin extends ActBase {
    private SharedPreferences.Editor loginPrefsEditor;
    private EditText login_name, login_password;
    private String username, password;
    private CheckBox login_remember, login_local;
    private FirebaseAuth mAuth;
    private final int spLogin = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        hideActionBar();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TextView login_version = (TextView) findViewById(R.id.login_version);
        login_name = (EditText) findViewById(R.id.login_name);
        login_password = (EditText) findViewById(R.id.login_password);
        login_remember = (CheckBox) findViewById(R.id.login_remember);
        login_local = (CheckBox) findViewById(R.id.login_local);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        login_version.setText("เวอร์ชั่น " + Global.getVersion(this));
        mAuth = FirebaseAuth.getInstance();
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin) {
            login_name.setText(loginPreferences.getString("username", null));
            login_remember.setChecked(true);
            login_local.setChecked(loginPreferences.getBoolean("local", false));
            login_password.requestFocus();
        }

        Button bt_cmd_save = (Button) findViewById(R.id.login_login);
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = login_name.getText().toString();
                password = login_password.getText().toString();
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
                            switch (((FirebaseAuthInvalidCredentialsException) e).getErrorCode()) {
                                case "ERROR_INVALID_CREDENTIAL":
                                    hideProgressDialog();
                                    MessageBox("อีเมล์ไม่มีการใช้งานจริง");
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    hideProgressDialog();
                                    MessageBox("อีเมล์ไม่ถูกต้อง");
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    hideProgressDialog();
                                    MessageBox("รหัสผ่านผิด");
                                    break;
                                case "ERROR_USER_MISMATCH":
                                    hideProgressDialog();
                                    MessageBox("บัญชีถูกเปลี่ยนรหัสผ่านจากเครื่องอื่น โปรดล้อกอินใหม่อีกครั้ง");
                                    break;
                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    hideProgressDialog();
                                    MessageBox("บัญชีถูกเปลี่ยนรหัสผ่านจากเครื่องอื่น โปรดล้อกอินใหม่อีกครั้ง");
                                    break;
                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    break;
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    hideProgressDialog();
                                    MessageBox("มีการล็อกอินด้วยอีเมลนี้จากที่อื่นแล้ว");
                                    break;
                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    break;
                                case "ERROR_USER_DISABLED":
                                    hideProgressDialog();
                                    MessageBox("บัญชีถูกระงับการใช้งาน");
                                    break;
                                case "ERROR_USER_TOKEN_EXPIRED":
                                    hideProgressDialog();
                                    MessageBox("เซสชั่นหมดอายุ โปรดล้อกอินใหม่อีกครั้ง");
                                    break;
                                case "ERROR_USER_NOT_FOUND":
                                    hideProgressDialog();
                                    MessageBox("ไม่พบรายการผู้ใช้");
                                    break;
                                case "ERROR_INVALID_USER_TOKEN":
                                    hideProgressDialog();
                                    MessageBox("เซสชั่นหมดอายุ โปรดล้อกอินใหม่อีกครั้ง");
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    hideProgressDialog();
                                    MessageBox("รหัสผ่านง่ายเกินไป");
                                    break;
                                default:
                                    hideProgressDialog();
                                    MessageBox(((FirebaseAuthInvalidCredentialsException) e).getErrorCode());
                                    break;
                            }
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
                Database database = value.getValue(Database.class);
                Global.setDatabase(database, getApplicationContext());
                String appversion = Global.getVersion(getApplicationContext());
                String latestversion = database.getAppversion();

                if (!Objects.equals(latestversion, appversion)) {
                    hideProgressDialog();
                    MessageBox("เวอร์ชั่นของคุณเป็น " + appversion + " กรุณาอัพเกรดเป็นเวอร์ชั่นใหม่สุด " + latestversion);

//
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    StorageReference storageRef = storage.getReferenceFromUrl("gs://bsrpossqltest.appspot.com/1.2.10.apk");
//
//                    File localFile = null;
//                    try {
//                        localFile = File.createTempFile("1.2.10", "apk");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (localFile != null) {
//                        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
//                                String fileName = "1.2.10.apk";
//                                destination += fileName;
//                                final Uri uri = Uri.parse("file://" + destination);
//
//                                Intent install = new Intent(Intent.ACTION_VIEW);
//                                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                install.setDataAndType(uri, "application/vnd.android.package-archive");
//                                startActivity(install);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                MessageBox(exception.getMessage());
//                            }
//                        });
//                    }


                    mAuth.signOut();
                } else {
                    Global.setisLocal(login_local.isChecked(), getApplicationContext());
                    new SqlQuery(ActLogin.this, spLogin, "{call " + database.getPrefix() + "loginbyemail(?,?)}", new String[]{username, password});
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
            login_name.setError("ต้องกรอก");
            isValid = false;
        }
        if (password.equals("") || password.length() < 6) {
            login_password.setError("ต้องกรอก 6 หลักขึ้นไป");
            isValid = false;
        }
        return isValid;
    }

    public void rememberlogin() {
        if (login_remember.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putBoolean("local", login_local.isChecked());
            loginPrefsEditor.putString("username", username);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }

    public void onBackPressed() {
        if (!login_remember.isChecked()) {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        finishAffinity();
        moveTaskToBack(true);
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) {
        try {
            if (tag == this.spLogin) {
                if (rs != null && rs.next() && rs.getInt("usr_Id") > 0) {
                    User user = new User(rs.getInt("usr_Id"), rs.getString("login_name"), rs.getString("usr_name"), rs.getBoolean("admin"), rs.getBoolean("deleteorder"), rs.getString("password"), rs.getString("email"));
                    Global.setUser(user, getApplicationContext());
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
