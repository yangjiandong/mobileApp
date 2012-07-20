package com.ek.mobileapp.nurse.adapter;

import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ek.mobileapp.R;

//显示全部生命体征gridview
public class VitalSignDataGridViewAdapter extends BaseAdapter {
    private Context context;
    List<String> numList;

    public VitalSignDataGridViewAdapter(Context context, List<String> numList) {
        this.context = context;
        this.numList = numList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        gridView = new View(context);
        gridView = inflater.inflate(R.layout.vitalsign_gridview_item, null);

        TextView textCode = (TextView) gridView.findViewById(R.id.grid_item_code);
        TextView textLabel = (TextView) gridView.findViewById(R.id.grid_item_label);
        TextView textValue = (TextView) gridView.findViewById(R.id.grid_item_value);

        StringTokenizer s = new StringTokenizer(numList.get(position), "|");
        String code = s.nextToken();
        textCode.setText(code);
        textLabel.setText(s.nextToken());
        textValue.setText(s.nextToken());//s.nextToken());
        if (code.equals("01")) {
            //convertView.setBackgroundColor(Color.GREEN);
        }

        return gridView;
    }

    public int getCount() {
        return numList.size();
    }

    public Object getItem(int position) {
        return numList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

}