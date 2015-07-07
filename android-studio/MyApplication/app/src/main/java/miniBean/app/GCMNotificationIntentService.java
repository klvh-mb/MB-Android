package miniBean.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;

import miniBean.R;
import miniBean.activity.MainActivity;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification(""
						+ extras.get(Config.MESSAGE_KEY));
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.mascot_orange_left, msg, System.currentTimeMillis());

        String title = getApplicationContext().getString(R.string.app_name);//The notification title

        Date now = new Date();
        long uniqueId = now.getTime();//use date to generate an unique id to differentiate the notifications.

        /** Put the information you should pass in your notification to the intent. **/
        /** Replace the HomeActivity with the class name you wish to start when user click on the notificaiton. **/
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("IS_NOTIFICATION", true);
        notificationIntent.putExtra("NOTIFICATION_MSG", msg);

        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        /** This is the part to let your application recognise difference notification when the user click on the notification.  You could use any unique string to represent your notification. But be sure each notification have difference action name.
         **/
        notificationIntent.setAction("com.sample.myapp" + uniqueId);

        PendingIntent intent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(getApplicationContext(), title, msg, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        /** Set the unique id to let Notification Manager knows this is a another notification instead of same notification. If you use the same uniqueId for each notification, the Notification Manager will assume that is same notification and would replace the previous notification. **/
        notificationManager.notify((int) uniqueId, notification);
    }
}
