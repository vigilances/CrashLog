# CrashLog

能将未捕获处理的异常，写入文件并保存在您的设备中，便于后期调试和上线的错误分析

CrashLog can catch the crash info , and write to file

# 使用
# Usage

使用下面方法来构造和初始化CrashLog，可以自定义生成日志的路径(默认在Context.getExternalFilesDir(null).getAbsolutePath())和名字(crash_log.txt)。

You can construct it  as follow

  Default:
            ```
            new CrashLog.Builder().Context(this).create();
            ```
  Custom:
            ```
            new CrashLog.Builder().Context(this).path("/sdcard/").fileName("CrashLog")create();
            ```
# 实际效果
# Actual effect

Note: 示例是使用一个分母为0的错误,以一个点击事件来触发
```
   final int i = 0, j = 2;
        tv.setText("int i = 0, j = 2;" + "\nint result = j / i" + "\n\nClick !");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("!!!", "onCreate: " + (j / i));
            }
        });
    }
```
![](https://github.com/vigilances/CrashLog/tree/master/image/use.png)

弹出Toast,程序停止运行,捕获并写入相关信息
![](https://github.com/vigilances/CrashLog/tree/master/image/use1.png)

日志中的详细信息，(前面是用2/0，所以异常信息对应的上)
![](https://github.com/vigilances/CrashLog/tree/master/image/log.png)