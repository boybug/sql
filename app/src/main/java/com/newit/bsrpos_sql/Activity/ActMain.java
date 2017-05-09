package com.newit.bsrpos_sql.Activity;

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
        backPressed(ActLogin.class, "ออกจากระบบ", "ท่านต้องการออกจากระบบหรือไม่?");
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

