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
}
