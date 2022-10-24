package com.agx.catra;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.agx.catra.common.Constants.RC_GET_DEVICE_ADMIN;
import static com.agx.catra.common.Constants.SET_PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import com.agx.catra.control.GainControlService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GainControlService";
    private CheckBox checkBoxAdmin;
    private Button btn_grantPermission;

    private static Context context;
    public static AppCompatActivity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivity(this);
        setContext(this);




        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GainControlService.class);
        startService(intent);

        setContentView(R.layout.activity_main);
        btn_grantPermission = (Button) findViewById(R.id.btn_grandPermission);
        btn_grantPermission.setOnClickListener(v -> {
            GainControlService.SelfGrantPermissions(getContext());
        });


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
}