package com.ek.mobileapp.approval.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.OperationApprovalData;

//手术
public class OperationApprovalListAdapter extends BaseAdapter {

    private class OperationApprovalHolder {
        public TextView patientId;
        public TextView patientName;
        public TextView doctorName;
    }

    private Context context;
    private List<OperationApprovalData> list;
    private LayoutInflater mInflater;

    public OperationApprovalListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setList(List<OperationApprovalData> list) {
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
        OperationApprovalHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.operationapproval_list, null);
            holder = new OperationApprovalHolder();
            holder.patientId = (TextView) convertView.findViewById(R.id.operationapproval_patientId);
            holder.patientName = (TextView) convertView.findViewById(R.id.operationapproval_patientName);
            holder.doctorName = (TextView) convertView.findViewById(R.id.operationapproval_doctorName);
            convertView.setTag(holder);
        } else {
            holder = (OperationApprovalHolder) convertView.getTag();
        }
        OperationApprovalData data = list.get(position);
        if (data != null) {
            holder.patientId.setText(data.getPatientId().toString());
            holder.patientName.setText(data.getPatientName());
            holder.doctorName.setText(data.getAppWho());

        }
        return convertView;
    }

}
