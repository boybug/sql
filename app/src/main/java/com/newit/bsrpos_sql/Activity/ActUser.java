package com.newit.bsrpos_sql.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.Model.User;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActUser extends ActBase {

    private List<User> users = new ArrayList<>();
    private AdpCustom<User> adap;
    private final int spQuery = 1;
    private final int spDelete = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายชื่อผู้ใช้");
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<User>(R.layout.listing_grid_user, getLayoutInflater(), users) {
            @Override
            protected void populateView(View v, User user) {
                TextView user_login = (TextView) v.findViewById(R.id.user_login);
                user_login.setText(user.getLogin());

                TextView user_name = (TextView) v.findViewById(R.id.user_name);
                user_name.setText(user.getName());

                TextView user_deleteorder = (TextView) v.findViewById(R.id.user_deleteorder);
                if (user.isDeleteorder()) user_deleteorder.setText("ลบใบสั่งได้");
                else user_deleteorder.setVisibility(View.GONE);

                TextView user_admin = (TextView) v.findViewById(R.id.user_admin);
                if (user.isDeleteorder()) user_admin.setText("แอดมิน");
                else user_admin.setText("พนักงาน");

                if (searchString != null) SetTextSpan(searchString, user.getName(), user_name);

                if (!Global.user.isAdmin()) {
                    user_deleteorder.setEnabled(false);
                    user_admin.setEnabled(false);
                }
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", adap.getModels().get(position));
                Intent intent = new Intent(ActUser.this, ActUserInput.class);
                intent.putExtras(bundle);
                ActUser.this.startActivity(intent);
            }
        });
        if (Global.user.isAdmin()) {
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final User user = adap.getModels().get(position);
                    if (user.getId() == Global.user.getId())
                        MessageBox("ลบตัวเองไม่ได้");
                    else {
                        AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new android.support.v7.app.AlertDialog.Builder(ActUser.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new android.support.v7.app.AlertDialog.Builder(ActUser.this);
                        dialog.setTitle("ยืนยันการลบ");
                        dialog.setIcon(R.mipmap.ic_launcher);
                        dialog.setCancelable(true);
                        dialog.setMessage("ท่านต้องการลบรายการนี้หรือไม่?");
                        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog12, int which) {
                                new SqlQuery(ActUser.this, spDelete, "{call " + Global.database.getPrefix() + "deleteuser(?,?)}", new String[]{user.getLogin(), String.valueOf(user.getId())});
                            }
                        });
                        dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.cancel();
                            }
                        });
                        dialog.show();
                    }
                    return true;
                }
            });
        }
        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, users, adap);
    }

    @Override
    public void refresh() {
        new SqlQuery(this, spQuery, "{call " + Global.database.getPrefix() + "getuser(?,?)}", new String[]{Global.user.getLogin(), String.valueOf(Global.user.isAdmin())});
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

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
        if (tag == spQuery) {
            users.clear();
            while (rs != null && rs.next()) {
                User user = new User(rs.getInt("usr_Id"), rs.getString("login_name"), rs.getString("usr_name"), rs.getBoolean("admin"), rs.getBoolean("deleteorder"), rs.getString("password"), rs.getString("email"));
                users.add(user);
            }
            if (adap != null) adap.notifyDataSetChanged();
        } else if (tag == spDelete) {
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                MessageBox(result.getMsg() == null ? "ลบผู้ใช้แล้ว" : result.getMsg());
                refresh();
            }
        }
    }
}
