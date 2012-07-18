package com.ek.mobileapp.nurse.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ek.mobileapp.R;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    List<String> numList;

    public ImageAdapter(Context context, List<String> numList) {
        this.context = context;
        this.numList = numList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(context);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.vitalsign_item1, null);

            // set value into textview
            TextView textLabel = (TextView) gridView.findViewById(R.id.grid_item_label);
            TextView textValue = (TextView) gridView.findViewById(R.id.grid_item_value);
            ImageView image = (ImageView) gridView.findViewById(R.id.grid_item_image);

            //switch (position) {
            //case 0:
            textLabel.setText(numList.get(position));
            textValue.setText(numList.get(position));
            image.setImageResource(R.drawable.ic_action_bar_info);
            //break;

        } else {
            gridView = (View) convertView;
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