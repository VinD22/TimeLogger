package project.timer.tracker.logger.time.v.app.timelogger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/*
 * Used for Push Notifications!
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //message will contain the Push Message
        String message = remoteMessage.getData().get("message");
        String url = remoteMessage.getData().get("url");

        if(message == null) {
            if(message.isEmpty()) {
                message = remoteMessage.getNotification().getBody().trim();
                url = "";
            }
        }

        sendNotification(message, url);

        Log.i(TAG, "onMessageReceived: ");
    }

    private void sendNotification(String messageBody, String url) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(messageBody);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(2)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(defaultSoundUri)
                .setStyle(inboxStyle)
                .setContentIntent(pendingIntent);
        // .setContentTitle("Inventory Management")


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        // http://www.startingandroid.com/android-push-notification-using-firebase-cloud-messaging/
        // http://engineering.letsnurture.com/firebase-cloud-messaging/

    }
}