package com.agx.catra.control;

import static com.agx.catra.MainActivity.getActivity;
import static com.agx.catra.MainActivity.getContext;
import static com.agx.catra.common.Constants.APPLICATION_ID;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.agx.catra.R;
import com.agx.catra.common.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GainControlService extends Service {
	private static final String TAG = "GainControlService";
	private final List<PackageInfo> mInstalledPackages = new ArrayList<>();
	// we have to maintain this set because DPM has no methods to add or remove lockable packages individually
	private final Set<String> mLockablePackages = new HashSet<>();
	private static DevicePolicyManager mDpm;
	private static ComponentName mAdminComponent;
	private ArrayAdapter<PackageInfo> mListAdapter;

	@Override
	public void onCreate() {
		super.onCreate();
		//noinspection ConstantConditions - we have an action bar
		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminComponent = new ComponentName(this, ApplicationAdminReceiver.class);
		checkDeviceOwnership();
	}



	private static final Comparator<PackageInfo> gPackageComparator = new Comparator<PackageInfo>() {
		@Override
		public int compare(final PackageInfo p0, final PackageInfo p1) {
			return p0.packageName.compareTo(p1.packageName);
		}
	};

	private void populatePackageLists() {
		mInstalledPackages.clear();
		mLockablePackages.clear();
		for(PackageInfo pkg : getPackageManager().getInstalledPackages(0)) {
			if((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				mInstalledPackages.add(pkg);
			}
			if(mDpm.isLockTaskPermitted(pkg.packageName)) {
				mLockablePackages.add(pkg.packageName);
			}
		}
		Collections.sort(mInstalledPackages, gPackageComparator);
	}

	public static void checkDeviceOwnership() {
		// find out if we are the device owner
		if(!mDpm.isDeviceOwnerApp(APPLICATION_ID)) {
			Log.d(TAG, "checkDeviceOwnership: we are not a device owner");
			// find out if we can become the device owner
			final Account[] accounts = AccountManager.get(getContext()).getAccounts();
			if(accounts.length == 0) {
				Log.d(TAG, "checkDeviceOwnership: we are device admin so we can become device owner");
				// find out if we are a device administrator
				if(mDpm.isAdminActive(mAdminComponent)) {
					// become the device owner
					RunDpmDialog.show(getActivity());
				}
				else {
					Log.d(TAG, "checkDeviceOwnership: we are not a device admin so we need to gain admin rights");
					// become a device administrator
					final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponent);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getContext().getString(R.string.grant_admin));
					getActivity().startActivity(intent);
				}
			}
			else {
				Log.d(TAG, "checkDeviceOwnership: we can not become device admin or device owner");

				// inform the user that we cannot become the device owner
				AccountExistsDialog.show(getActivity());
			}
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void SelfGrantPermissions(Context context) {
		Log.d(TAG, "SelfGrantPermissions has been launched");
		final DevicePolicyManager devicePolicyManager =
				(DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);

		AtomicInteger totalGranted = new AtomicInteger();
		AtomicInteger totalDenied = new AtomicInteger();
		int totalPermissions = Constants.USED_PERMISSIONS.size();

		Constants.USED_PERMISSIONS.forEach(v ->{
			boolean isGranted = false;
			Log.d(TAG, "SelfGrantPermissions: granting permission ="+v);
			try{
				isGranted = devicePolicyManager.setPermissionGrantState(getComponentName(context), context.getPackageName(), v, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
			}catch (Exception e){
				e.printStackTrace();
			}

			if(isGranted) {
				Log.d(TAG, "SelfGrantPermissions: permission granted ="+v);
				totalGranted.getAndIncrement();
			}
			else {
				Log.e(TAG, "SelfGrantPermissions: permission denied ="+v);
				totalDenied.getAndIncrement();
			}



		});
		Log.e(TAG, "SelfGrantPermissions: Total/Granted/Denied="+totalPermissions+"/"+totalGranted+"/"+totalDenied);

//        devicePolicyManager.setPermissionGrantState(getComponentName(context), context.getPackageName(), "android.permission.WRITE_EXTERNAL_STORAGE", DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
//        devicePolicyManager.setPermissionGrantState(getComponentName(context), context.getPackageName(), "android.permission.READ_EXTERNAL_STORAGE", DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
//        devicePolicyManager.setPermissionGrantState(getComponentName(context), context.getPackageName(), "android.permission.ACCESS_FINE_LOCATION", DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
	}
	public static ComponentName getComponentName(Context context) {
		return new ComponentName(context.getApplicationContext(), ApplicationAdminReceiver.class);
	}
}
