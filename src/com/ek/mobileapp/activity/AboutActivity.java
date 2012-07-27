package com.ek.mobileapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ek.mobileapp.R;

public class AboutActivity extends Activity implements OnClickListener {
    final static String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        PackageInfo pInfo;
        String version = "v1.x.x";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            version = pInfo.versionName;
        } catch (final NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView)findViewById(R.id.txtVersion)).setText(version);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        String tag = (String)v.getTag();
        Intent i = new Intent(Intent.ACTION_VIEW);

        if ("web".equals(tag)) {
            i.setData(Uri.parse("https://github.com/liato/android-bankdroid"));
        }
        else if ("donate".equals(tag)) {
            i.setData(Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=KWRCBB4PAA3LC"));
        }
        startActivity(i);
    }

}
