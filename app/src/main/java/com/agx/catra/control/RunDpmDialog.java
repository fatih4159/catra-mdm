package com.agx.catra.control;

import static com.agx.catra.common.Constants.APPLICATION_ID;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.agx.catra.MainActivity;
import com.agx.catra.R;


@SuppressWarnings("WeakerAccess") // fragments should be public
public class RunDpmDialog extends DialogFragment {
	@NonNull
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		setCancelable(false);
		String name = AdminReceiver.class.getName();
		if(name.startsWith(APPLICATION_ID)) {
			name = name.substring(APPLICATION_ID.length());
		}
		final String command = "adb shell dpm set-device-owner " + APPLICATION_ID + '/' + name;
		String message = getString(R.string.run_dpm);
		message = message.replace("{COMMAND}", command);

		return new AlertDialog.Builder(getActivity())
				.setMessage(message)
				.setPositiveButton(R.string.dpm_success, new DialogInterface.OnClickListener() {
					@TargetApi(Build.VERSION_CODES.LOLLIPOP)
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						final DevicePolicyManager dpm =
								(DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
						if(!dpm.isDeviceOwnerApp(APPLICATION_ID)) {
							show(getActivity());
						}
					}
				})
				.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialogInterface, final int i) {
						MainActivity.getActivity().finishAndRemoveTask();

					}
				})
				.create();
	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	static void show(final FragmentActivity activity) {
		new RunDpmDialog().show(activity.getSupportFragmentManager(), null);
	}
}
