package com.itwings.dataVerification.Adepters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itwings.dataVerification.R;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> list_category;
    private View rowView;

    public CustomSpinnerAdapter(Context context, ArrayList<String> list_category) {
        this.context = context;
        this.list_category = list_category;
    }

    @Override
    public int getCount() {
        return list_category == null ? 0 : list_category.size();
    }

    @Override
    public Object getItem(int position) {
        return list_category.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_category.indexOf(list_category.get(position));
    }

    public void addAll(List<String> list){
        list_category.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll(){
        list_category.clear();
        notifyDataSetChanged();
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.spinner_item_white, parent, false);
        TextView txt = (TextView) rowView.findViewById(R.id.txt);
        ImageView img = (ImageView) rowView.findViewById(R.id.arrow);
        if (position != 0){
            img.setVisibility(View.GONE);
        }
        txt.setText(list_category.get(position));
        return rowView;
    }
}
