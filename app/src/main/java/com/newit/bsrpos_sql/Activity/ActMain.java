package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Menu;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpMenu;

import java.sql.ResultSet;

public class ActMain extends ActBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTitle(Global.getUser(getApplicationContext()).getName() + "@" + Global.getwh_grp_name(getApplicationContext()));

        final AdpMenu adap = new AdpMenu(getApplicationContext());
        ListView list = (ListView) findViewById(R.id.list_main);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Menu menu = adap.getItem(position);
                Intent intent = new Intent(ActMain.this.getApplicationContext(), menu.getActivity());
                ActMain.this.startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        backPressed(ActLogin.class, "ออกจากระบบ", "ท่านต้องการออกจากระบบหรือไม่?");
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.contextmenu, menu);
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

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) {

    }
}

