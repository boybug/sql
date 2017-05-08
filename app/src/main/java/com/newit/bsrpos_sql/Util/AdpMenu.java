package com.newit.bsrpos_sql.Util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newit.bsrpos_sql.Activity.ActCustomer;
import com.newit.bsrpos_sql.Activity.ActOrder;
import com.newit.bsrpos_sql.Activity.ActProduct;
import com.newit.bsrpos_sql.Model.Menu;
import com.newit.bsrpos_sql.R;

import java.util.ArrayList;
import java.util.List;

public class AdpMenu extends BaseAdapter {

    private List<Menu> menus;
    private LayoutInflater inflater;

    public AdpMenu(Context context) {
        menus = new ArrayList<>();
        menus.add(new Menu(1, "ขาย", R.drawable.order, R.color.colorPrimaryNavy, ActOrder.class));
        menus.add(new Menu(2, "ลูกค้า", R.drawable.customer, R.color.colorPrimaryGreen, ActCustomer.class));
        menus.add(new Menu(3, "สินค้า", R.drawable.product, R.color.colorSquid, ActProduct.class));
//        menus.add(new Menu(4, "เครื่องมือ", R.drawable.utility, R.color.colorBeef, Tool.class));

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Menu getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menus.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.listing_grid_menu, parent, false);

        Menu menu = this.getItem(position);

        LinearLayout menu_layout = (LinearLayout) view.findViewById(R.id.menu_layout);
        menu_layout.setBackgroundResource(menu.getBgColor());

        ImageView menu_icon = (ImageView) view.findViewById(R.id.menu_icon);
        menu_icon.setImageResource(menu.getIcon());

        TextView menu_text = (TextView) view.findViewById(R.id.menu_text);
        menu_text.setText(menu.getName());

        return view;
    }
}