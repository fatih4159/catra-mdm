package com.agx.catra.workers;


import static com.agx.catra.control.AdminControlService.isAdmin;
import static com.agx.catra.control.AdminControlService.isOwner;
import static com.agx.mqtt.ui.MainService.setRemoteActivity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agx.catra.MainActivity;
import com.agx.catra.control.AdminControlService;
import com.agx.mqtt.ui.MainService;

public class CommunicationsWorker extends Worker {

    public static void start(Context context){

        WorkManager
                .getInstance(context)
                .enqueue(new OneTimeWorkRequest.Builder(CommunicationsWorker.class).build());
    }
    private static final String TAG = "OwnPermissionsWorker";

    // Output Result Keys
    public static final String KEY_TOTAL_GRANTED = "OPW_TOTAL_GRANTED";
    public static final String KEY_TOTAL_PERMISSIONS = "OPW_TOTAL_PERMISSIONS";
    public static final String KEY_TOTAL_DENIED = "OPW_TOTAL_DENIED";


    public CommunicationsWorker(
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
        if(isAdmin() && isOwner()){
            Intent intent = new Intent(context, MainService.class);
            setRemoteActivity(MainActivity.getActivity());
            context.startService(intent);
            return Result.success();
        }else{
            return Result.retry();
        }


    }
}

