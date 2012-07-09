package com.ek.mobileapp.activity;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ViewAnimator;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.QueryAction;
import com.ek.mobileapp.model.QueryTotalInfo;
import com.ek.mobileapp.utils.TimeTool;
import com.ek.mobileapp.utils.ViewUtils;
import com.ek.mobileapp.utils.WebUtils;

public class QueryActivity extends ActivityGroup {
    EditText startTime;
    EditText endTime;
    Button queryButton;
    Button backButton;
    RadioButton today;
    RadioButton week;
    RadioButton month;
    ListView grid;

    String busdate = "";

    String startDate = "", endDate = "";

    protected PopupWindow selectDateView;
    protected LayoutInflater mLayoutInflater;
    protected DatePicker date_picker;
    private ProgressDialog proDialog;

    private boolean date_changed = false;

    private boolean isStart = true;

    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    @Override
    protected void onCreate(Bundle fromMainBundle) {
        super.onCreate(fromMainBundle);
        mLayoutInflater = (LayoutInflater) QueryActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.activity_query);
        startTime = (EditText) findViewById(R.id.query_startdate);
        endTime = (EditText) findViewById(R.id.query_enddate);
        queryButton = (Button) findViewById(R.id.query_button_query);
        backButton = (Button) findViewById(R.id.query_back);
        today = (RadioButton) findViewById(R.id.query_button_today);
        week = (RadioButton) findViewById(R.id.query_button_week);
        month = (RadioButton) findViewById(R.id.query_button_month);
        grid = (ListView) findViewById(R.id.query_list);

        startDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());
        endDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());

        startTime.setText(startDate);
        endTime.setText(endDate);

        startTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                isStart = true;
                if (selectDateView == null) {
                    initSelectDate();
                }
                if (!selectDateView.isShowing()) {
                    View view = mLayoutInflater.inflate(R.layout.activity_query, null);
                    selectDateView.showAtLocation(view, BIND_AUTO_CREATE, 0, 0);
                } else {
                    selectDateView.dismiss();
                }
            }

        });

        endTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                isStart = false;
                if (selectDateView == null) {
                    initSelectDate();
                }
                if (!selectDateView.isShowing()) {
                    View view = mLayoutInflater.inflate(R.layout.activity_query, null);
                    selectDateView.showAtLocation(view, BIND_AUTO_CREATE, 0, 0);
                } else {
                    selectDateView.dismiss();
                }
            }

        });

        queryButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                QueryAction.getTotalData(startDate, endDate);
            }

        });

        today.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                startDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());
                endDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());
                startTime.setText(startDate);
                endTime.setText(endDate);
            }

        });

        week.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                startTime.setText(startDate);
                endTime.setText(endDate);
            }

        });

        month.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String m = "";
                if (TimeTool.getMonth() < 10) {
                    m = "0" + String.valueOf(TimeTool.getMonth());
                } else {
                    m = String.valueOf(TimeTool.getMonth());
                }

                startDate = String.valueOf(TimeTool.getYear()) + "." + m + ".01";
                endDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());
                startTime.setText(startDate);
                endTime.setText(endDate);
            }

        });

        int res = QueryAction.getTotalData(startDate, endDate);

        if (res == WebUtils.SUCCESS) {

            // 开始显示内容
            switchActivity(new Intent(QueryActivity.this, QueryTotalInfo.class), ViewUtils.MENU_HOME, this.RIGHT);

        }

    }

    public void switchActivity(Intent intent, int type, int direction) {
        ViewAnimator mViewAnimator = new ViewAnimator(this);

        grid.removeAllViews();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity = getLocalActivityManager().startActivity("subActivity", intent);
        View view = subActivity.getDecorView();
        mViewAnimator.removeAllViews();
        mViewAnimator.addView(view);
        grid.addView(mViewAnimator, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mViewAnimator.showNext();
    }

    private void initSelectDate() {
        View view = mLayoutInflater.inflate(R.layout.activity_date, null);
        date_picker = (DatePicker) view.findViewById(R.id.query_seldate);
        date_picker.init(TimeTool.getYear(), TimeTool.getMonth(), TimeTool.getDay(), new OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date_changed = true;
                busdate = TimeTool.getDateFormatedFromDataPicker(year, monthOfYear, dayOfMonth);
                if (isStart) {
                    startDate = busdate;
                } else {
                    endDate = busdate;
                }

            }

        });

        selectDateView = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        selectDateView.setFocusable(true);
        selectDateView.setAnimationStyle(-1);
        selectDateView.update();
        Button okbtn = (Button) view.findViewById(R.id.query_date_ok);
        okbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                    if (isStart) {
                        startTime.setText(busdate);
                    } else {
                        endTime.setText(busdate);
                    }

                }
            }
        });

        Button canelbtn = (Button) view.findViewById(R.id.query_date_cancle);
        canelbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                }
            }
        });
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case 0: {
                AlertDialog.Builder builder = new AlertDialog.Builder(QueryActivity.this);
                builder.setMessage(msg.getData().getString("msg")).setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            }
            default: {

            }
            }
        }
    };

    private void queryUnSuccess(String msg) {
        proDialog.dismiss();
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("msg", msg);
        message.setData(bundle);
        UIHandler.sendMessage(message);
    }
}
