package meme.app.dmcx.chatapp.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import meme.app.dmcx.chatapp.Activities.MainActivity;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Chat;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Profile.NotificationProfileActivity;
import meme.app.dmcx.chatapp.LocalDatabase.AppLocalDatabaseVariables;
import meme.app.dmcx.chatapp.Manifest;
import meme.app.dmcx.chatapp.R;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        int notificationId = (int) System.currentTimeMillis();
        String channelId = "default";
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String fromUser = remoteMessage.getData().get("from_user");
        String notificationType = remoteMessage.getData().get("type");

        SuperVariables._AppFirebase.setProfileUserId(fromUser);

        if (notificationType.equals(AppFirebaseVariables.notification_type_request)) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SuperVariables._MainActivity, channelId)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            Intent intent = new Intent(this, NotificationProfileActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(881, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);

            assert notificationManager != null;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel("Default", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.notify(notificationId, builder.build());

        }

    }
}
