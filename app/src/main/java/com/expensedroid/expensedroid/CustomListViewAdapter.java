package com.expensedroid.expensedroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by S. Ameli on 09/07/16.
 */
public class CustomListViewAdapter extends BaseAdapter {

    private ArrayList<Transaction> listData;
    private LayoutInflater layoutInflater;

    public CustomListViewAdapter(Context aContext, ArrayList<Transaction> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
