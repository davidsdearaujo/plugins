// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebasemessaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FlutterFirebaseMessagingService extends FirebaseMessagingService {

  static final String TAG = "FirebaseMessaging";

  public static final String ACTION_REMOTE_MESSAGE =
      "io.flutter.plugins.firebasemessaging.NOTIFICATION";
  public static final String EXTRA_REMOTE_MESSAGE = "notification";

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Map<String, String> data = remoteMessage.getData();

    Log.d(TAG, "Received onMessageReceived()");
    Log.d(TAG, "Bundle data: " + data);
    Log.d(TAG, "From: " + remoteMessage.getFrom());

    try {
      if (data.size() > 0 && data.containsKey("tipo") && data.get("tipo").equals("INTERFONE")) {

          SharedPreferences settings = getApplicationContext().getSharedPreferences("FlutterSharedPreferences", getApplicationContext().MODE_PRIVATE);
          SharedPreferences.Editor editor = settings.edit();
          editor.putString("flutter.INTERFONE_LIGACAO", "true");
          editor.apply();
          Log.d("Interfone", "SharedPreferences save");

          String ns = getApplicationContext().getPackageName();
        String cls = ns + ".MainActivity";
        Intent intentMainApp = new Intent(getApplicationContext(), Class.forName(cls));
        intentMainApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentMainApp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentMainApp.addCategory(Intent.CATEGORY_LAUNCHER);
        intentMainApp.putExtra("foreground", true);
        startActivity(intentMainApp);

        PowerManager powermanager=  ((PowerManager)getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE));
        PowerManager.WakeLock wakeLock=powermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        wakeLock.acquire();
        if(wakeLock.isHeld())
        {
           wakeLock.release();
        }
      }
    } catch (Exception e) {
        Log.w(TAG, "Failed to open application on received call", e);
    }

      Intent intent = new Intent(ACTION_REMOTE_MESSAGE);
      intent.putExtra(EXTRA_REMOTE_MESSAGE, remoteMessage);
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }
}
