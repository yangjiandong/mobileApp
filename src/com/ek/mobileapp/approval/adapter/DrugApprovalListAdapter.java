package com.ek.mobileapp.approval.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.DrugApprovalData;

//用药
public class DrugApprovalListAdapter extends BaseAdapter {

    private class DrugApprovalHolder {
        public TextView patientId;
        public TextView patientName;
        public TextView doctorName;
    }

    private Context context;
    private List<DrugApprovalData> list;
    private LayoutInflater mInflater;

    public DrugApprovalListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setList(List<DrugApprovalData> list) {
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
        DrugApprovalHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drugapproval_list, null);
            holder = new DrugApprovalHolder();
            holder.patientId = (TextView) convertView.findViewById(R.id.drugapproval_patientId);
            holder.patientName = (TextView) convertView.findViewById(R.id.drugapproval_patientName);
            holder.doctorName = (TextView) convertView.findViewById(R.id.drugapproval_doctorName);
            convertView.setTag(holder);
        } else {
            holder = (DrugApprovalHolder) convertView.getTag();
        }
        DrugApprovalData data = list.get(position);
        if (data != null) {
            holder.patientId.setText(data.getPatientId().toString());
            holder.patientName.setText(data.getPatientName());
            holder.doctorName.setText(data.getAppWho());

        }
        return convertView;
    }

}
