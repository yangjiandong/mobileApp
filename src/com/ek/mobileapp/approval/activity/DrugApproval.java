package com.ek.mobileapp.approval.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.approval.action.DrugApprovalAction;
import com.ek.mobileapp.approval.adapter.DrugApprovalListAdapter;
import com.ek.mobileapp.model.DrugApprovalData;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.ToastUtils;

//合理用药审批
public class DrugApproval extends Activity {
    List<DrugApprovalData> list = new ArrayList<DrugApprovalData>();

    ListView infolist;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle fromMainBundle) {
        super.onCreate(fromMainBundle);
        GlobalCache.getCache().setModuleCode(null);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.drugapproval);

        infolist = (ListView) findViewById(R.id.drugapproval_list);
        infolist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        infolist.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent intent = new Intent(DrugApproval.this, DrugApproval2.class);
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });

    }

    private void refreshList() {
        DrugApprovalAction.getAll();
        list = GlobalCache.getCache().getDrugApprovalDatas();
        DrugApprovalListAdapter adapter = new DrugApprovalListAdapter(DrugApproval.this);
        adapter.setList(list);
        infolist.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onDestroy() {

        processCommitData();
        super.onDestroy();
    }

    //开始处理提交
    private void processCommitData() {

        try {
            CommitData commit = new CommitData(commitDataHandler);
            Thread thread = new Thread(commit);
            thread.sleep(100);
            thread.start();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //回调函数，显示结果
    Handler commitDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            String message = msg.getData().getString("msg");

            switch (type) {
            case 1: {
                ToastUtils.show(DrugApproval.this, message);
                break;
            }
            case 0: {
                ToastUtils.show(DrugApproval.this, message);
            }
            default: {

            }
            }
        }
    };

    //真正的提交过程
    class CommitData implements Runnable {
        Handler handler;

        public CommitData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                String ret = DrugApprovalAction.commitHis();
                if (!ret.equals("1")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", "提交失败");
                    message.setData(bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("msg", "提交成功");
                    message.setData(bundle);
                }

            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            }
            this.handler.sendMessage(message);
        }

    }

}
