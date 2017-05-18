package com.newit.bsrpos_sql.Activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ActUserInput extends ActBase {

    private User user;
    private CheckBox userinput_deleteorder;
    private final int spChngPwd = 1;
    private final int spResetPwd = 2;
    private final int spUpdateDeleteOrder = 3;
    private String newpass;
    private String oldpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinput);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            MessageBox("error");
            finish();
        }

        user = (User) (bundle != null ? bundle.getSerializable("user") : null);

        Button userinput_chngpwd = (Button) findViewById(R.id.userinput_chngpwd);
        Button userinput_resetpwd = (Button) findViewById(R.id.userinput_resetpwd);
        userinput_deleteorder = (CheckBox) findViewById(R.id.userinput_deleteorder);
        CheckBox userinput_admin = (CheckBox) findViewById(R.id.userinput_admin);

        TextView userinput_login = (TextView) findViewById(R.id.userinput_login);
        TextView userinput_name = (TextView) findViewById(R.id.userinput_name);
        userinput_login.setText(user.getLogin());
        userinput_name.setText(user.getName());
        userinput_deleteorder.setChecked(user.isDeleteorder());

        userinput_admin.setEnabled(false);
        userinput_chngpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPassword();
            }
        });

        if (Global.user.isAdmin()) {
            if (user.getId() != Global.user.getId()) userinput_chngpwd.setVisibility(View.GONE);
            userinput_resetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActUserInput.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActUserInput.this);
                    dialog.setTitle("รีเซ็ตรหัสผ่าน");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("คุณต้องการรีเซ็ตรหัสผ่านใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showProgressDialog();
                            new SqlQuery(ActUserInput.this, spResetPwd, "{call " + Global.database.getPrefix() + "chngpasword(?,?,?)}", new String[]{user.getLogin(), user.getPassword(), "123456"});
                        }
                    });
                    dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            });
            final boolean[] update = {true};
            userinput_deleteorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActUserInput.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActUserInput.this);
                    dialog.setTitle("อนุญาต");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("คุณต้องการเปลี่ยนระดับการลบบิลใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showProgressDialog();
                            new SqlQuery(ActUserInput.this, spUpdateDeleteOrder, "{call " + Global.database.getPrefix() + "chnguserdetail(?,?)}", new String[]{user.getLogin(), userinput_deleteorder.isChecked() ? "1" : "0"});
                        }
                    });
                    dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            update[0] = false;
                            if (userinput_deleteorder.isChecked()) {
                                userinput_deleteorder.setChecked(false);
                            } else {
                                userinput_deleteorder.setChecked(true);
                                dialog.cancel();
                            }
                        }
                    });
                    if (update[0]) dialog.show();
                    update[0] = true;
                }
            });
        } else {
            userinput_resetpwd.setVisibility(View.GONE);
            userinput_deleteorder.setEnabled(false);
            userinput_chngpwd.setVisibility(View.VISIBLE);
        }
    }

    private void PassResetViaEmail(final String login, final String oldpassword, final String newpassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(login, oldpassword);
        if (user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    MessageBox("รีเซทรหัสผ่านแล้ว");
                                } else {
                                    MessageBox("ผิดพลาด ไม่สามารถรีเซทรหัสผ่าน");
                                }
                            }
                        });
                    } else {
                        MessageBox("ผิดพลาด รหัสผ่านไม่ตรงกัน");
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contextmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            super.backPressed(ActLogin.class);
        }
        return true;
    }

    private void showDialogPassword() {
        AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActUserInput.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActUserInput.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        final EditText old_password = (EditText) view.findViewById(R.id.old_password);
        final EditText new_password = (EditText) view.findViewById(R.id.new_password);
        final EditText confirm_password = (EditText) view.findViewById(R.id.confirm_password);
        dialog.setView(view);

        dialog.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        oldpass = old_password.getText().toString();
                        newpass = new_password.getText().toString();
                        String confirmpass = confirm_password.getText().toString();
                        if (newpass.length() < 6) {
                            MessageBox("รหัสผ่าน 6 หลักขึ้นไป");
                        } else if (!TextUtils.isEmpty(oldpass) && !TextUtils.isEmpty(newpass) && !TextUtils.isEmpty(confirmpass) && Objects.equals(newpass, confirmpass)) {
                            showProgressDialog();
                            new SqlQuery(ActUserInput.this, spChngPwd, "{call " + Global.database.getPrefix() + "chngpasword(?,?,?)}", new String[]{user.getLogin(), String.valueOf(oldpass), String.valueOf(newpass)});

                        }
                    }
                });
        dialog.setNegativeButton(getString(android.R.string.cancel), null);
        dialog.show();
    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
        if (tag == spChngPwd) {
            hideProgressDialog();
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                if (result.getMsg() == null) {
                    MessageBox("เปลี่ยนรหัสผ่านสำเร็จ");
                    PassResetViaEmail(user.getEmail(), oldpass, newpass);
                } else MessageBox(result.getMsg());
            }
        } else if (tag == spResetPwd) {
            hideProgressDialog();
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                if (result.getMsg() == null) {
                    MessageBox("รีเซทรหัสผ่านสำเร็จ");
                    PassResetViaEmail(user.getEmail(), user.getPassword(), "123456");
                } else MessageBox(result.getMsg());
            }
        } else if (tag == spUpdateDeleteOrder) {
            hideProgressDialog();
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                MessageBox(result.getMsg() == null ? "เปลี่ยนระดับการลบบิลสำเร็จ" : result.getMsg());
            }
        }
    }
}
