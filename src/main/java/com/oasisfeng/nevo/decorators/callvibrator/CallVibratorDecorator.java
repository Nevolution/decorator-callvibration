/*
 * Copyright (C) 2015 The Nevolution Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oasisfeng.nevo.decorators.callvibrator;

import android.app.Notification;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import com.oasisfeng.nevo.sdk.MutableStatusBarNotification;
import com.oasisfeng.nevo.sdk.NevoDecoratorService;

/**
 * App-specific decorator - Vibrator when outgoing call is answered
 *
 * Created by Oasis on 2016/2/9.
 */
public class CallVibratorDecorator extends NevoDecoratorService {

	private static final long VIBRATOR_DURATION = 500;
	private static final long MAX_DELAY_TO_VIBRATE = 2000;

	@Override public void apply(final MutableStatusBarNotification evolving) {
		if (! evolving.isOngoing()) return;
		final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (tm == null) return;
		if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
			mIncomingCall = true;
			return;
		} else if (mIncomingCall) return;

		// The on-going notification is being updated in place, now check the chronometer.
		final Bundle extras = evolving.getNotification().extras;
		if (! extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER, false)) return;
		// Ensure the chronometer starting time is just now.
		if (evolving.getNotification().when < System.currentTimeMillis() - MAX_DELAY_TO_VIBRATE) return;

		final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		if (vibrator != null) vibrator.vibrate(VIBRATOR_DURATION);
	}

	@Override protected void onNotificationRemoved(final String key, final int reason) {
		mIncomingCall = false;
	}

	private boolean mIncomingCall;
}
