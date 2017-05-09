package com.newit.bsrpos_sql.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Menu;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpMenu;

public class ActMain extends ActBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTitle(Global.usr_name + "@" + Global.wh_name);
        AdpMenu adap = new AdpMenu(getApplicationContext());
        ListView list = (ListView) findViewById(R.id.list_main);
        list.setAdapter(adap);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Menu menu = adap.getItem(position);
            Intent intent = new Intent(getApplicationContext(), menu.getActivity());
            startActivity(intent);
        });
    }

    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากระบบ");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการออกจากระบบ?");
        dialog.setPositiveButton("ใช่", (dialog12, which) -> {

            Intent intent = new Intent(ActMain.this, ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        dialog.setNegativeButton("ไม่", (dialog1, which) -> dialog1.cancel());

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Intent intent = new Intent(ActMain.this, ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }
}

