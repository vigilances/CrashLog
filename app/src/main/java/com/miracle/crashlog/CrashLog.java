package com.miracle.crashlog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CrashLog implements Thread.UncaughtExceptionHandler {

    public static String TAG = "MyCrash";
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context mContext;
    private Map<String, String> infos = new HashMap<>();
    private String fileName;
    private String path;
    private boolean isDeleted;

    private CrashLog(Builder builder) {
        this.fileName = builder.fileName;
        this.path = builder.path;
        this.isDeleted = builder.isDeleted;
        this.mContext = builder.mContext;
        init();
    }

    public void init() {
        initPathandName();
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        try {
            deleteFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //use detault value if null
    private void initPathandName() throws NullPointerException {
        if (TextUtils.isEmpty(fileName))
            fileName = "crash_log.txt";
        if (TextUtils.isEmpty(path)) {
            if (null == mContext)
                throw new NullPointerException("Context could't be null,like this CrashLog.Builder().Context(context)");
            path = mContext.getExternalFilesDir(null).getAbsolutePath() + "/";
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        } else {
            SystemClock.sleep(3000);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null)
            return false;
        try {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "Sorry, something went wrong!",
                            Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            //collect the device's info
            collectDeviceInfo(mContext);
            // save log file
            saveCrashInfoFile(ex);
            SystemClock.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    private String saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            sb.append("\r\n" + date + "\n");
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + " = " + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            String fileName = writeFile(sb.toString());
            return fileName;
        } catch (Exception e) {
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
        }
        return null;
    }

    private String writeFile(String sb) throws IOException {
        File dir = new File(path);
        try {
            if (!dir.exists())
                dir.mkdirs();
            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new IOException("Write log failed !");
        }
        return fileName;
    }

    private void deleteFile() throws IOException {
        //max size 4MB
        long defaultsize = 1024 * 1024 * 4;
        try {
            if (getFileSize(path + fileName) > defaultsize && !isDeleted) {
                getFileByPath(path + fileName).delete();
                Log.i(TAG, "deleteFile: ------delete success");
            }
        } catch (IOException e) {
            throw new IOException("Delete file failed !");
        }
    }

    private File getFileByPath(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return new File(filePath);
        }
        return null;
    }

    private long getFileSize(String path) throws IOException {
        long size = 0;
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e(TAG, "file doesn't exist and create new file");
        }
        return size;
    }

    public static class Builder {
        private String fileName;
        private String path;
        private boolean isDeleted;
        private Context mContext;

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder ifDelete(boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public Builder Context(Context mContext) {
            this.mContext = mContext;
            return this;
        }

        public CrashLog create() {
            return new CrashLog(this);
        }
    }
}