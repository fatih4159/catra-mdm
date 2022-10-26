package com.agx.catra.workers;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agx.catra.control.AdminControlService;

public class AdminControlServiceWorker extends Worker {

    public static void start(Context context){

        WorkManager
                .getInstance(context)
                .enqueue(new OneTimeWorkRequest.Builder(AdminControlServiceWorker.class).build());
    }
    private static final String TAG = "OwnPermissionsWorker";

    // Output Result Keys
    public static final String KEY_TOTAL_GRANTED = "OPW_TOTAL_GRANTED";
    public static final String KEY_TOTAL_PERMISSIONS = "OPW_TOTAL_PERMISSIONS";
    public static final String KEY_TOTAL_DENIED = "OPW_TOTAL_DENIED";


    public AdminControlServiceWorker(
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

        Intent intent = new Intent(context, AdminControlService.class);
        context.startService(intent);

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

