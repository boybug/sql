package com.newit.bsrpos_sql.Util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class AdpCustom<T> extends BaseAdapter {

    private final LayoutInflater inflater;
    private final int layout;
    private List<T> models;

    public AdpCustom(int layout, LayoutInflater inflater, List<T> models) {
        this.layout = layout;
        this.inflater = inflater;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public T getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null && layout > 0) {
            //todo: ถ้าปกติ ให้เรียกแบบนี้ ถ้า ซ่อน ให้เรียกอีกแบบ
            view = inflater.inflate(layout, viewGroup, false);
        }
        T model = models.get(i);
        if (layout > 0)
            populateView(view, model);
        return view;
    }

    protected abstract void populateView(View v, T model);

    public void setModels(List<T> models) {
        this.models = models;
    }
}
