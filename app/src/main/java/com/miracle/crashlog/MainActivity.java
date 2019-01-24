package com.miracle.crashlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init the crashlog
        new CrashLog.Builder().Context(this).create();
        test();
    }

    private void test() {
        TextView tv = (TextView) findViewById(R.id.tv);
        final int i = 0, j = 2;
        tv.setText("int i = 0, j = 2;" + "\nint result = j / i" + "\n\nClick !");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("!!!", "onCreate: " + (j / i));
            }
        });
    }
    
     /**
     * 年龄计算
     * 0--传入时间戳   1--传入日期格式字符串("2012-12-2")
     *
     * @param time
     * @return
     */
    public static String calculateAge(int src, String time) {
        if (TextUtils.isEmpty(time)) {
            return "0";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        try {
            if (src == 0) {
                birthday = sdf.parse(stampToDate(time));
            } else {
                birthday = sdf.parse(time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        b.setTime(birthday);
        int year = now.get(Calendar.YEAR) - b.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) - b.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH) - b.get(Calendar.DAY_OF_MONTH);
        if (month < 0) {
            month = 12 - b.get(Calendar.MONTH) + now.get(Calendar.MONTH);
            year -= 1;
        }
        if (day < 0) {
            day = b.getMaximum(Calendar.DAY_OF_MONTH) - b.get(Calendar.DAY_OF_MONTH) + now.get(Calendar.DAY_OF_MONTH);
            month -= 1;
        }
        if (year >= 1) {
            return year + "岁";
        } else {
            if (month < 1) {
                return day + "天";
            }
            return month + "个月";
        }
    }
}
