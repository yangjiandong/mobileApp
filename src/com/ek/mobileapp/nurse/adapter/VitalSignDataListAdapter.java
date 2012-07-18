package com.ek.mobileapp.nurse.adapter;

import java.util.List;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.VitalSignData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VitalSignDataListAdapter extends BaseAdapter {
    private class InfoHolder {
        public TextView name;
        public TextView value;
    }

    private Context context;
    private List<VitalSignData> list;
    private LayoutInflater mInflater;

    public void setList(List<VitalSignData> list) {
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public VitalSignDataListAdapter(Context context) {
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

        VitalSignData total = list.get(position);
        if (total != null) {
            if (total.getUnit() != null && total.getUnit().length() > 0) {
                holder.name.setText(total.getItemName() + "(" + total.getUnit() + ")");
            } else {
                holder.name.setText(total.getItemName());
            }
            String value = "";
            if (total.getValue1() != null && total.getValue1().length() > 0) {
                value = total.getValue1();
            } else {
                value = total.getValue2();
            }
            holder.value.setText(value);
        }
        return convertView;
    }
}
