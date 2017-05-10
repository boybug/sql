package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;

public class ActUserInput extends ActBase {

    private User user;

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
        CheckBox userinput_deleteorder = (CheckBox) findViewById(R.id.userinput_deleteorder);
        CheckBox userinput_admin = (CheckBox) findViewById(R.id.userinput_admin);

        userinput_admin.setEnabled(false);
        userinput_chngpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: change password ตัวเอง ให้เด้งถาม old + new + new แล้ว update password = new
            }
        });

        if (Global.user.isAdmin() && !isself) {

            userinput_resetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: reset password คนอื่น ให้ถาม dialog ยืนยัน แล้ว update password = login_name
                }
            });
            userinput_deleteorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //todo: เปลี่ยนสิทธิการลบ order ของคนอื่น ยืนยันแล้ว update deleteorder
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
}
