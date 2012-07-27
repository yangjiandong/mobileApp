package com.ek.mobileapp.nurse.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.DrugCheckData;

//用药
public class DrugCheckDataListAdapter extends BaseAdapter {

    private class DrugCheckHolder {
        public TextView orderText;
        public TextView dosage;
    }

    private Context context;
    private List<DrugCheckData> list;
    private LayoutInflater mInflater;

    public DrugCheckDataListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setList(List<DrugCheckData> list) {
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        DrugCheckHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drugcheck_list, null);
            holder = new DrugCheckHolder();
            holder.orderText = (TextView) convertView.findViewById(R.id.drugcheck_order_text);
            holder.dosage = (TextView) convertView.findViewById(R.id.drugcheck_dosage);
            convertView.setTag(holder);
        } else {
            holder = (DrugCheckHolder) convertView.getTag();
        }
        DrugCheckData data = list.get(position);
        if (data != null) {
            holder.orderText.setText(data.getOrderText());
            holder.dosage.setText(data.getDosage());

        }
        return convertView;
    }

}
