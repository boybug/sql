package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Bank;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActBank extends ActBase {

    private List<Bank> banks = new ArrayList<>();
    private AdpCustom<Bank> adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("โปรดเลือกบัญชีธนาคารที่โอนเข้ามา");
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Bank>(R.layout.listing_grid_bank, getLayoutInflater(), banks) {
            @Override
            protected void populateView(View v, Bank bank) {
                TextView bank_name = (TextView) v.findViewById(R.id.bank_name);
                bank_name.setText(bank.getName());

                if (searchString != null) SetTextSpan(searchString, bank.getName(), bank_name);
            }
        };

        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bank bank = banks.get(position);
                Intent intent = new Intent();
                intent.putExtra("bank_id", bank.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, banks, adap);
    }

    @Override
    public void onBackPressed() {
        MessageBox("ยังไม่ได้เลือกธนาคาร");
        super.onBackPressed();
    }

    @Override
    public void refresh() {
        new SqlQuery(ActBank.this, 1, "{call " + Global.getDatabase(getApplicationContext()).getPrefix() + "getbank(?)}", new String[]{String.valueOf(Global.getwh_Grp_Id(getApplicationContext()))});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == 1) {
            banks.clear();
            while (rs != null && rs.next()) {
                Bank bank = new Bank(rs.getInt("bank_Id"), rs.getString("bank_name"));
                banks.add(bank);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
