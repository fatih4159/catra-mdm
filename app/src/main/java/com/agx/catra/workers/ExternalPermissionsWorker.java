package com.agx.catra.workers;


import static com.agx.catra.MainActivity.getDevicePolicyManager;
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

import com.agx.catra.common.Constants;

import java.util.concurrent.atomic.AtomicInteger;

public class ExternalPermissionsWorker extends Worker {

    public static void start(Context context){

        WorkManager
                .getInstance(context)
                .enqueue(new OneTimeWorkRequest.Builder(ExternalPermissionsWorker.class).build());
    }
    private static final String TAG = "OwnPermissionsWorker";

    // Output Result Keys
    public static final String KEY_TOTAL_GRANTED = "OPW_TOTAL_GRANTED";
    public static final String KEY_TOTAL_PERMISSIONS = "OPW_TOTAL_PERMISSIONS";
    public static final String KEY_TOTAL_DENIED = "OPW_TOTAL_DENIED";


    public ExternalPermissionsWorker(
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
        Log.d(TAG, "SelfGrantPermissions has been launched");

        AtomicInteger totalGranted = new AtomicInteger();
        AtomicInteger totalDenied = new AtomicInteger();
        int totalPermissions = Constants.USED_PERMISSIONS.size();

        Constants.USED_PERMISSIONS.forEach(v ->{
            boolean isGranted = false;
//            Log.d(TAG, "SelfGrantPermissions: granting permission ="+v);
            try{
                isGranted = getDevicePolicyManager().setPermissionGrantState(getComponentName(context), context.getPackageName(), v, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
            }catch (Exception e){
//                e.printStackTrace();
            }

            if(isGranted) {
                //Log.d(TAG, "SelfGrantPermissions: permission granted ="+v);
                totalGranted.getAndIncrement();
            }
            else {
                //Log.e(TAG, "SelfGrantPermissions: permission denied ="+v);
                totalDenied.getAndIncrement();
            }



        });


        Log.e(TAG, "SelfGrantPermissions: Total/Granted/Denied="+totalPermissions+"/"+totalGranted+"/"+totalDenied);

        //...set the output, and we're done!
        Data output = new Data.Builder()
                .putInt(KEY_TOTAL_PERMISSIONS, totalPermissions)
                .putInt(KEY_TOTAL_GRANTED, totalGranted.get())
                .putInt(KEY_TOTAL_DENIED, totalDenied.get())
                .build();
        // Indicate whether the work finished successfully with the Result
        return Result.success(output);
    }
}

