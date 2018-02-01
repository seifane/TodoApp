package ch.idoucha.todolist.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import ch.idoucha.todolist.helper.DbHelper;
import ch.idoucha.todolist.model.Item;

/**
 * Created by Seïfane Idouchach on 1/31/2018.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DbHelper helper = new DbHelper(context);
        List<Item> list = helper.getAllItems();

        for (Item item : list) {
            item.schedule(context);
        }
    }
}
