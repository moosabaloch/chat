package pana.com.chat.gcm;

/**
 * Created by Moosa on 12/29/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import pana.com.chat.HomeActivity;
import pana.com.chat.MainActivity;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    private Bitmap bitmap;

    /**
     * Called when message is received.
     * Called By Operating System
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String picture = data.getString("pic");
        String title = data.getString("title");
        String fragment = data.getString("frag");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Picture: " + picture);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Fragment:" + fragment);

        SharedPreferences prefs = getSharedPreferences(HomeActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt(title, 0);
        if (notificationNumber == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            notificationNumber = generateRandom();
            editor.putInt(title, notificationNumber);
            editor.commit();
        }


        //    if (from.startsWith("/topics/")) {
        // message received from some topic.
        //    } else {
        // normal downstream message.
        //    }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        if (!prefs.getBoolean(Utils.MY_APP_IS_RUNNING, false)) {
            sendNotification(message, picture, title, fragment, notificationNumber);
        }// [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String picUrl, String title, String frag, int notifyID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frag", frag);
        intent.putExtra("sender", title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //////////////////////////////////////////////////////////////////////
        bitmap = getBitmapFromURL(picUrl);
       /* AppController.getInstance().getImageLoader().get(picUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
               bitmap= response.getBitmap();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });*/
        ////////////////////////////////////////////////////////
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.friend)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(2)
                .setVibrate(new long[]{100, 100, 100, 100, 100, 100})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notifyID, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int generateRandom() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}