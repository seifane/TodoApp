package ch.idoucha.todolist.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ch.idoucha.todolist.helper.DbHelper;
import ch.idoucha.todolist.model.Item;

/**
 * Created by Seïfane Idouchach on 2/1/2018.
 */

public class SetDoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("ID")) {
            int id = intent.getIntExtra("ID", 0);
            DbHelper helper = new DbHelper(context);

            Item item = helper.getItemById(id);
            if (item != null) {
                item.setStatus(Item.STATE_DONE);
                helper.updateItem(context, item);

                if (intent.hasExtra("notifID")) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.cancel(intent.getIntExtra("notifID", 0));
                }
            }
        }
    }
}
