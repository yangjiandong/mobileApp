package com.ek.mobileapp.nurse.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.VitalSignItem;

public class VitalSignItemListAdapter extends BaseAdapter {
    private class InfoHolder {
        public TextView name;
        public TextView value;
    }

    private Context context;
    private List<VitalSignItem> list;
    private LayoutInflater mInflater;

    public void setList(List<VitalSignItem> list) {
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public VitalSignItemListAdapter(Context context) {
        super();
        this.context = context;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int index) {
        return list.get(index);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        InfoHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vitalsigndata, null);
            holder = new InfoHolder();
            holder.name = (TextView) convertView.findViewById(R.id.vital_sign_name);
            holder.value = (TextView) convertView.findViewById(R.id.vital_sign_value);
            convertView.setTag(holder);
        } else {
            holder = (InfoHolder) convertView.getTag();
        }

        VitalSignItem total = list.get(position);
        if (total != null) {
            if (total.getUnit() != null && total.getUnit().length() > 0) {
                holder.name.setText(total.getName() + "(" + total.getUnit() + ")");
            } else {
                holder.name.setText(total.getName());
            }
            String value = "";

            holder.value.setText(value);
        }
        return convertView;
    }
}
