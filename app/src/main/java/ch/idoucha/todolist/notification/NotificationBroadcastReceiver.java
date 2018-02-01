package ch.idoucha.todolist.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Random;

import ch.idoucha.todolist.AddActivity;
import ch.idoucha.todolist.MainActivity;
import ch.idoucha.todolist.R;
import ch.idoucha.todolist.helper.DbHelper;
import ch.idoucha.todolist.model.Item;

/**
 * Created by Seïfane Idouchach on 1/31/2018.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "NotificationIDCHANNEL";
    public static final String NOTIFICATION_CHANNEL_NAME = "NotificationCHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = new Random().nextInt();

        if (intent.hasExtra("ID")) {
            int id = intent.getIntExtra("ID", 0);
            DbHelper helper = new DbHelper(context);

            Item item = helper.getItemById(id);
            setNotificationChannel(context);
            if (item != null) {
                if (item.getDate() > System.currentTimeMillis())
                    return;
                if (item.getStatus() != Item.STATE_ACTIVE)
                    return;

                Intent intentWithData = new Intent(context, SetDoneReceiver.class);
                intentWithData.putExtra("ID", id);
                intentWithData.putExtra("notifID", notificationId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, new Random().nextInt(), intentWithData, 0);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_alarm_on_white_18dp)
                                .addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.notification_set_done), pendingIntent) // #0
                                .setContentTitle("Reminder : " + item.getTitle())
                                .setContentText(item.getContent());

                Intent resultIntent = new Intent(context, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(notificationId, mBuilder.build());
            }
        }
    }

    private void setNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = NOTIFICATION_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //notificationChannel.enableVibration(true);
            //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
