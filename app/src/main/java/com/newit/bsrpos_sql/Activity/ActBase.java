package com.newit.bsrpos_sql.Activity;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;

import java.util.ArrayList;
import java.util.Locale;

public class ActBase extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ImageButton btn_search;
    protected EditText txt_search;
    private ImageButton btn_clear;


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
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
            LinearLayout searchbar = (LinearLayout) findViewById(searchId/*R.id.list_search*/);
            searchbar.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void hideActionBar() {
        getSupportActionBar().hide();
    }

    public void hideFloatButton(@IdRes int fabId) {
        try {
            FloatingActionButton fab = (FloatingActionButton) findViewById(fabId/*R.id.fab*/);
            fab.setVisibility((View.GONE));
        } catch (Exception e) {
        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void MessageBox(String message) {
        Toast.makeText(ActBase.this, message, Toast.LENGTH_LONG).show();
    }

    public void AddVoiceSearch(@IdRes int txtId,@IdRes int btnId) {
        txt_search = (EditText) findViewById(txtId/*R.id.txt_search*/);
        btn_search = (ImageButton) findViewById(btnId/*R.id.btn_search*/);
        if (txt_search != null && btn_search != null) {
            btn_search.setOnClickListener(v -> {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "พูดคำที่ต้องการค้นหา...");
                try {
                    startActivityForResult(intent, Global.speechCode);
                } catch (ActivityNotFoundException a) {
                    MessageBox("เครื่องนี้ไม่รองรับระบบวิเคราะห์เสียง.");
                }
            });
        }
    }

    public void SetTextSpan(String search, String name, TextView lab_name) {
        int firstIndex = name.toLowerCase(Locale.getDefault()).indexOf(search,0);
        Spannable span = new SpannableString(name);
        for (int i = 0; i < name.length() && firstIndex != -1; i = firstIndex + 1) {
            firstIndex = name.toLowerCase(Locale.getDefault()).indexOf(search,i);
            if (firstIndex == -1)
                break;
            else {
                span.setSpan(new BackgroundColorSpan(0xFFFFFF00), firstIndex, firstIndex + search.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                lab_name.setText(span, TextView.BufferType.SPANNABLE);
            }
        }
    }

    public void ClearSearch(@IdRes int txtId,@IdRes int btnId) {
        txt_search = (EditText) findViewById(txtId);
        btn_clear = (ImageButton) findViewById(btnId);
        if (txt_search != null && btn_clear != null) {
            btn_clear.setOnClickListener(v -> {
                txt_search.setText("");
            });
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
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_sign_out) {
//            Logout();
//        }
        return true;
    }
}
