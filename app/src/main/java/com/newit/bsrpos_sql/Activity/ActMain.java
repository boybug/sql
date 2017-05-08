package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
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
}

