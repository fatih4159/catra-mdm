package com.agx.catra.control.deviceadmin.common;

import android.os.Environment;



import java.io.File;
import java.nio.file.FileSystem;

/* loaded from: classes.dex */
public final class Constants {
    public static final String BARCODE_REGEX = "^(~[0-9][0-9])(63s2d9t)([0-9]{2,4})(a|s)([A-Za-z0-9+\\/=]*)$";
    public static final String BARCODE_REGEX_LEGACY = "^(~..)?(..)([A-Za-z0-9+\\/=]*)$";
    private static final String DATA_HEADER = "-----BEGIN ENCODED DATA-----";
    private static final String DATA_TRAILER = "-----END ENCODED DATA-----";
    public static final String DEVICE_ADMINS_PATH = "Settings -> Apps & notifications -> Advanced ->  Special app access -> Device admin apps";
    public static final String DL_LAUNCHER_PACKAGE_NAME = "com.datalogic.launcher";
    public static final int device_owner_profile_name = 0x7f0f005d;
    public static final double PACKAGE_INSTALLER_DL_SDK_VER = 1.24d;
    public static final int SDK_1_24 = 65560;
    public static final String SURELOCK_PACKAGE_NAME = "com.gears42.surelock";
    public static final String TAG = "Scan2Deploy";

    /* loaded from: classes.dex */
    public static final class Actions {
        public static final String CLOSE = "close";
        public static final String ENTERPRISE_RESET = "enterprise-reset";
        public static final String FACTORY_RESET = "factory-reset";
        public static final String NONE = "none";
        public static final String REBOOT = "reboot";
        public static final String RESET = "reset";
    }

    /* loaded from: classes.dex */
    public static final class Files {
        private static final String ENTERPRISE_ARCHIVE_FILE = "scan2deploy.archive";
        public static final File WORKING_ARCHIVE = new File(Environment.getExternalStorageDirectory(), ENTERPRISE_ARCHIVE_FILE);
    }

    /* loaded from: classes.dex */
    public static final class PadlockStates {
        public static final String LOCKED = "locked";
        public static final String UNDEFINED = "undefined";
        public static final String UNLOCKED = "unlocked";
    }

    public static String getDataHeader() {
        return DATA_HEADER;
    }

    public static String getDataTrailer() {
        return DATA_TRAILER;
    }

}
