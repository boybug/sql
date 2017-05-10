package com.newit.bsrpos_sql.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
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

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;

import java.util.Objects;

public class ActUserInput extends ActBase {

    private User user;
    private CheckBox userinput_deleteorder;
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
        //todo : bug... เทียบแล้วไม่เคยเท่ากัน
        boolean isself = user == Global.user ? true : false;

        Button userinput_chngpwd = (Button) findViewById(R.id.userinput_chngpwd);
        Button userinput_resetpwd = (Button) findViewById(R.id.userinput_resetpwd);
        userinput_deleteorder = (CheckBox) findViewById(R.id.userinput_deleteorder);
        CheckBox userinput_admin = (CheckBox) findViewById(R.id.userinput_admin);

        TextView userinput_login = (TextView) findViewById(R.id.userinput_login);
        TextView userinput_name = (TextView) findViewById(R.id.userinput_name);
        userinput_login.setText(user.getLogin());
        userinput_name.setText(user.getName());

        userinput_admin.setEnabled(false);
        userinput_chngpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPassword();
            }
        });

        if (Global.user.isAdmin() && !isself) {

            userinput_resetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActUserInput.this);
                    dialog.setTitle("รีเซ็ตรหัสผ่าน");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("คุณต้องการรีเซ็ตรหัสผ่านใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SqlResult result = user.change(user.getPassword(), user.getLogin());
                            ActUserInput.this.MessageBox(result.getMsg() == null ? "รีเซ็ตรหัสผ่านสำเร็จ" : result.getMsg());
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
            userinput_deleteorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //todo: เปลี่ยนสิทธิการลบ order ของคนอื่น ยืนยันแล้ว update deleteorder
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActUserInput.this);
                    dialog.setTitle("อนุญาต");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("คุณต้องการเปลี่ยนระดับการลบบิลใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            user.setDeleteorder(true);
                            SqlResult result = user.setuser();
                            ActUserInput.this.MessageBox(result.getMsg() == null ? "เปลี่ยนระดับการลบบิลสำเร็จ" : result.getMsg());
                        }
                    });
                    dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (userinput_deleteorder.isChecked()) userinput_deleteorder.setChecked(false);
                            else userinput_deleteorder.setChecked(true);
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            userinput_resetpwd.setVisibility(View.GONE);
            userinput_deleteorder.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
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
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ActUserInput.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        final EditText old_password = (EditText) view.findViewById(R.id.old_password);
        final EditText new_password = (EditText) view.findViewById(R.id.new_password);
        final EditText confirm_password = (EditText) view.findViewById(R.id.confirm_password);
        builder.setView(view);

        builder.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldpass = old_password.getText().toString();
                        String newpass = new_password.getText().toString();
                        String confirmpass = confirm_password.getText().toString();
                        if (!TextUtils.isEmpty(oldpass) && !TextUtils.isEmpty(newpass) && !TextUtils.isEmpty(confirmpass)) {
                            if (Objects.equals(newpass, confirmpass)) {
                                SqlResult result = user.change(oldpass, confirmpass);
                                ActUserInput.this.MessageBox(result.getMsg() == null ? "บันทึกรหัสผ่านสำเร็จ" : result.getMsg());
                            }

                        }
                    }
                });
        builder.setNegativeButton(getString(android.R.string.cancel), null);
        builder.show();
    }
}
