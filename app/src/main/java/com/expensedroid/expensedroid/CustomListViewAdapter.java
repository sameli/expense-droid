package com.expensedroid.expensedroid;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by S. Ameli on 09/07/16.
 *
 * This class defines a custom ListView for the main activity. List of transactions will be viewed using this class
 */
public class CustomListViewAdapter extends BaseAdapter{
    private ArrayList<Transaction> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListViewAdapter(Context aContext, ArrayList<Transaction> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        this.context = aContext;
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
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        textView_amount.setText(sign + "$" + formatter.format(num));
        int color = ContextCompat.getColor(context, R.color.Black);
        if(amount > 0){
            color = ContextCompat.getColor(context, R.color.Green);
        }else if(amount < 0){
            color = ContextCompat.getColor(context, R.color.Red);
        }
        textView_amount.setTextColor(color);
    }

    static class ViewHolder {
        TextView dateView;
        TextView titleView;
        TextView amountView;
    }
}
