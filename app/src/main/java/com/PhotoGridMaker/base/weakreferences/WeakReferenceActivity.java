package com.PhotoGridMaker.base.weakreferences;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Activity
 * Availability detector
 * 
 * @author Deepak
 * 
 */
public class 	WeakReferenceActivity {
	private WeakReference<Activity> mReference;

	public WeakReferenceActivity(Activity activity) {
		mReference = new WeakReference<Activity>(activity);
	}

	public Activity getActivity() {
		Activity activity = null;
		if (mReference == null) {
			return activity;
		}
		return mReference.get();
	}

	public boolean checkActivityAvailable() {
		Activity activity = getActivity();
		if (activity == null || activity.isFinishing()) {
			return false;
		}
		return true;
	}
}
