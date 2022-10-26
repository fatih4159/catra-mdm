package com.agx.catra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import com.agx.catra.control.AdminControlService;
import com.agx.catra.workers.OwnPermissionsWorker;
import com.agx.catra.workers.StartUpWorker;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AdminControlService";
    private CheckBox checkBoxAdmin;
    private Button btn_grantPermission;

    private static Context context;
    public static AppCompatActivity activity;
    private static DevicePolicyManager devicePolicyManager ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivity(this);
        setContext(this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);




        super.onCreate(savedInstanceState);

        StartUpWorker.start(getContext());

        setContentView(R.layout.activity_main);
        btn_grantPermission = (Button) findViewById(R.id.btn_grandPermission);
        btn_grantPermission.setOnClickListener(v -> {
            OwnPermissionsWorker.start(getContext());
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);

    }

    public static AppCompatActivity getActivity() {

        return activity;
    }

    public static void setActivity(AppCompatActivity activity) {
        MainActivity.activity = activity;
    }

    public static Context getContext() {
        return context;
    }

    public static void restart(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    public static void setContext(Context context) {
        MainActivity.context = context;
    }

    public static DevicePolicyManager getDevicePolicyManager() {
        return devicePolicyManager;
    }

    public static void setDevicePolicyManager(DevicePolicyManager devicePolicyManager) {
        MainActivity.devicePolicyManager = devicePolicyManager;
    }
}