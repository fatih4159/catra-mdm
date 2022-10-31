package com.agx.mqtt.helper;

import static com.agx.mqtt.BuildConfig.*;
import static com.agx.mqtt.helper.Notification.notificate;
import static com.agx.mqtt.ui.MainService.getContext;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.agx.mqtt.BuildConfig;
import com.agx.mqtt.R;
import com.agx.mqtt.ui.MainService;
import com.agx.mqtt.worker.WorkStarter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class Installer {
    public static void installPackage(final Context context, final String url)
            throws Exception {

        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);

        // set params
        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream out = session.openWrite("COSU", 0, -1);
        //get the input stream from the url
        HttpsURLConnection apkConn = (HttpsURLConnection) new URL(url).openConnection();
        InputStream in = apkConn.getInputStream();
        byte[] buffer = new byte[65536];
        int c;
        while ((c = in.read(buffer)) != -1) {
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        in.close();
        out.close();
//                    //you can replace this intent with whatever intent you want to be run when the applicaiton is finished installing
//                    //I assume you have an activity called InstallComplete
                    Intent intent = new Intent(context, MainService.class);
//                    intent.putExtra("info", "somedata");  // for extra data if needed..
                    Random generator = new Random();
                    PendingIntent i = PendingIntent.getActivity(context, generator.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    session.commit(i.getIntentSender());
        WorkStarter ws = new WorkStarter(getContext());
        ws.showNotification("APK Installer","Installation Done");

    }
}
