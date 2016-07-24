package com.expensedroid.expensedroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by S. Ameli on 09/07/16.
 */

public class CustomListViewAdapter extends BaseAdapter{
    private ArrayList<Transaction> listData;
    private LayoutInflater layoutInflater;

    public CustomListViewAdapter(Context aContext, ArrayList<Transaction> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_row_layout, null);
            holder = new ViewHolder();
            holder.dateView = (TextView) convertView.findViewById(R.id.textView_list_date);
            holder.titleView = (TextView) convertView.findViewById(R.id.textView_list_title);
            holder.amountView = (TextView) convertView.findViewById(R.id.textView_list_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateView.setText(listData.get(position).getDateString());
        holder.titleView.setText(listData.get(position).getTitle());
        showAmount(holder.amountView, listData.get(position).getAmount());
        return convertView;
    }

    private void showAmount(TextView textView_amount, double amount){

        String sign = (amount >= 0) ? "" : "-";
        double num = Math.abs(amount);
        textView_amount.setText(sign + "$" + String.format("%.2f", num));
        int color = (amount > 0) ? Color.parseColor("#008000") : Color.parseColor("#cc0000"); // first color is green, second is red
        textView_amount.setTextColor(color);
    }

    static class ViewHolder {
        TextView dateView;
        TextView titleView;
        TextView amountView;
    }
}
