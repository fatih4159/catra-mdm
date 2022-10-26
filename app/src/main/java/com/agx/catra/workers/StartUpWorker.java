package com.agx.catra.workers;


import static com.agx.catra.control.AdminControlService.getComponentName;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agx.catra.MainActivity;
import com.agx.catra.common.Constants;

import java.util.concurrent.atomic.AtomicInteger;

public class StartUpWorker extends Worker {

    public static void start(Context context){

        WorkManager
                .getInstance(context)
                .enqueue(new OneTimeWorkRequest.Builder(StartUpWorker.class).build());
    }
    private static final String TAG = "OwnPermissionsWorker";

    // Output Result Keys
    public static final String KEY_TOTAL_GRANTED = "OPW_TOTAL_GRANTED";
    public static final String KEY_TOTAL_PERMISSIONS = "OPW_TOTAL_PERMISSIONS";
    public static final String KEY_TOTAL_DENIED = "OPW_TOTAL_DENIED";


    public StartUpWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        this.params = params;
    }
    Context context;
    WorkerParameters params;

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.

        WorkManager
                .getInstance(context)
                .beginWith(new OneTimeWorkRequest.Builder(AdminControlServiceWorker.class).build())
                .enqueue();

        //...set the output, and we're done!
//        Data output = new Data.Builder()
//                .putInt(KEY_TOTAL_PERMISSIONS, totalPermissions)
//                .putInt(KEY_TOTAL_GRANTED, totalGranted.get())
//                .putInt(KEY_TOTAL_DENIED, totalDenied.get())
//                .build();
        // Indicate whether the work finished successfully with the Result
//        return Result.success(output);
        return Result.success();

    }
}

