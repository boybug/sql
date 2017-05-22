package com.newit.bsrpos_sql.Activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.ModelBase;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.AdpPrint;
import com.newit.bsrpos_sql.Util.SqlConnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@SuppressLint("Registered")
public abstract class ActBase<T> extends AppCompatActivity {

    protected EditText txt_search;
    private ProgressDialog mProgressDialog;
    protected String searchString;
    private List<T> backup;
    private ActionBar bar;


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideSearchBar(@IdRes int searchId) {
        try {
            LinearLayout searchbar = (LinearLayout) findViewById(searchId);
            searchbar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideActionBar() {
        bar = getSupportActionBar();
        if (bar != null) bar.hide();

    }

    public void hideFloatButton(@IdRes int fabId) {
        try {
            FloatingActionButton fab = (FloatingActionButton) findViewById(fabId);
            fab.setVisibility((View.GONE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        bar = getSupportActionBar();
        if (bar != null) bar.setTitle(title);
    }

    public void addVoiceSearch(@IdRes int txtId, @IdRes int speakBtnId, @IdRes int clearBtnId, final List<T> items, final AdpCustom<T> adap) {
        txt_search = (EditText) findViewById(txtId);
        ImageButton btn_search = (ImageButton) findViewById(speakBtnId);
        ImageButton btn_clear = (ImageButton) findViewById(clearBtnId);

        if (txt_search != null && btn_search != null) {
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "พูดคำที่ต้องการค้นหา...");
                    try {
                        ActBase.this.startActivityForResult(intent, Global.speechCode);
                    } catch (ActivityNotFoundException a) {
                        ActBase.this.MessageBox("เครื่องนี้ไม่รองรับระบบวิเคราะห์เสียง.");
                    }
                }
            });

            txt_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    searchString = s.toString().toLowerCase(Locale.getDefault());
                    List<T> filtered = new ArrayList<>();
                    for (T i : items) {
                        ModelBase item = (ModelBase) i;
                        if (item.getSearchString().toLowerCase(Locale.getDefault()).contains(searchString))
                            filtered.add(i);
                    }
                    if (backup == null)
                        backup = new ArrayList<>(items);
                    adap.setModels(filtered);
                    adap.notifyDataSetChanged();
                }
            });
        }
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_search.setText("");
            }
        });
    }

    public void SetTextSpan(String search, String name, TextView lab_name) {
        int firstIndex = name.toLowerCase(Locale.getDefault()).indexOf(search, 0);
        Spannable span = new SpannableString(name);
        for (int i = 0; i < name.length() && firstIndex != -1; i = firstIndex + 1) {
            firstIndex = name.toLowerCase(Locale.getDefault()).indexOf(search, i);
            if (firstIndex == -1)
                break;
            else {
                span.setSpan(new BackgroundColorSpan(0xFFFFFF00), firstIndex, firstIndex + search.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                lab_name.setText(span, TextView.BufferType.SPANNABLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Global.speechCode: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txt_search.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contextmenu, menu);
        return true;
    }

    public void MessageBox(String message) {
        Toast.makeText(ActBase.this, message, Toast.LENGTH_LONG).show();
    }

    public void backPressed(Class nextActivity) {
        if (nextActivity == ActLogin.class) SqlConnect.disconnect(this);
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void backPressed(final Class nextActivity, String title, String message) {
        AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setCancelable(true);
        dialog.setMessage(message);
        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog12, int which) {
                if (nextActivity == ActLogin.class) SqlConnect.disconnect(ActBase.this);
                Intent intent = new Intent(ActBase.this.getApplicationContext(), nextActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActBase.this.startActivity(intent);
                ActBase.this.finish();
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

    public void setSwipeRefresh(@IdRes int swipeid, @IdRes int listviewId) {
        final SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(swipeid);
        if (swiper != null) {
            final Handler[] handle = new Handler[1];
            final Runnable[] runable = new Runnable[1];
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    handle[0] = new Handler();
                    runable[0] = new Runnable() {
                        @Override
                        public void run() {
                            swiper.setRefreshing(false);
                            ActBase.this.refresh();
                            swiper.removeCallbacks(runable[0]);
                        }
                    };
                    handle[0].postDelayed(runable[0], 1000);
                }
            });
        }

        final ListView listView = (ListView) findViewById(listviewId);
        if (listView != null) {
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    boolean enable = false;
                    if (listView != null && listView.getChildCount() > 0) {
                        boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                        boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    swiper.setEnabled(enable);

                }
            });
        }
    }

    public void refresh() {
        MessageBox("no implementation.");
    }

    public void printPDF(String filename, @IdRes int rootViewId) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        AdpPrint printer = new AdpPrint(this, findViewById(rootViewId), filename);
        printManager.print("BSR POS " + filename, printer, null);
    }

    public abstract void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException;
}
