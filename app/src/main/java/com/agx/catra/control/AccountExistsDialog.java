package com.agx.catra.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.agx.catra.R;


@SuppressWarnings("WeakerAccess") // fragments should be public
public class AccountExistsDialog extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		setCancelable(false);
		return new AlertDialog.Builder(getActivity())
				.setMessage(R.string.error_account_exists)
				.setNeutralButton(R.string.quit, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialogInterface, final int i) {
						getActivity().finish();
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
		new AccountExistsDialog().show(activity.getSupportFragmentManager(), null);
	}
}
