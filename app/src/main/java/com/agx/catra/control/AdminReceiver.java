package com.agx.catra.control;

import static com.agx.catra.MainActivity.getContext;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agx.catra.MainActivity;
import com.agx.catra.workers.OwnPermissionsWorker;


public class AdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = "AdminReceiver";

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "onEnabled: ");
        OwnPermissionsWorker.start(getContext());
        MainActivity.restart(getContext());


    }

    @Override
    public void onTransferOwnershipComplete(@NonNull Context context, @Nullable PersistableBundle bundle) {
        Log.d(TAG, "onTransferOwnershipComplete: ");
        super.onTransferOwnershipComplete(context, bundle);
    }

    @Override
    public void onProfileProvisioningComplete(@NonNull Context context, @NonNull Intent intent) {
        Log.d(TAG, "onProfileProvisioningComplete: ");
        super.onProfileProvisioningComplete(context, intent);
    }
}
