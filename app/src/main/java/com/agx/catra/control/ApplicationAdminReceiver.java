package com.agx.catra.control;

import static com.agx.catra.MainActivity.getActivity;
import static com.agx.catra.MainActivity.getContext;
import static com.agx.catra.control.GainControlService.checkDeviceOwnership;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.agx.catra.MainActivity;


public class ApplicationAdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = "ApplicationAdminReceiver";

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "onEnabled: ");
        MainActivity.restart(getContext());


    }
}
