package ch.idoucha.todolist.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ch.idoucha.todolist.model.Item;
import ninja.sakib.pultusorm.callbacks.Callback;
import ninja.sakib.pultusorm.core.PultusORM;
import ninja.sakib.pultusorm.core.PultusORMCondition;
import ninja.sakib.pultusorm.core.PultusORMQuery;
import ninja.sakib.pultusorm.core.PultusORMUpdater;
import ninja.sakib.pultusorm.exceptions.PultusORMException;

/**
 * Created by Seïfane Idouchach on 1/31/2018.
 */

public class DbHelper {

    private PultusORM mOrm;

    public DbHelper(Context context) {
        String appPath = context.getFilesDir().getAbsolutePath();
        mOrm = new PultusORM("todos.db", appPath);
    }

    public Item getItemById(int id) {
        if (id <= 0)
            return null;
        PultusORMCondition condition = new PultusORMCondition.Builder()
                .eq("id", id)
                .build();
        List<Object> res = mOrm.find(new Item(), condition);
        if (res.size() == 1) {
            return (Item) res.get(0);
        }
        return null;
    }

    public List<Item> getAllItems() {
        PultusORMCondition condition = new PultusORMCondition.Builder()
                .sort("id", PultusORMQuery.Sort.DESCENDING)
                .build();
        List<Object> tmp = mOrm.find(new Item(), condition);
        List<Item> items = new ArrayList<>();
        for (Object o : tmp) {
            items.add((Item) o);
        }
        return items;
    }

    public boolean addItem(Context context, Item item) {
        boolean res = mOrm.save(item);
        if (res) {
            Item itemDb = getAllItems().get(0);
            if (itemDb != null) {
                itemDb.schedule(context);
            }
        }
        return res;
    }

    public void updateItem(final Context context, final Item item) {
        PultusORMCondition condition = new PultusORMCondition.Builder()
                .eq("id", item.id).build();
        PultusORMUpdater updater = new PultusORMUpdater.Builder()
                .set("title", item.getTitle())
                .set("content", item.getContent())
                .set("status", item.getStatus())
                .set("date", item.date).condition(condition).build();
        mOrm.update(new Item(), updater, new Callback() {
            @Override
            public void onSuccess(PultusORMQuery.Type type) {
                if (item.getStatus() == Item.STATE_ACTIVE)
                    item.schedule(context);
            }

            @Override
            public void onFailure(PultusORMQuery.Type type, PultusORMException e) {

            }
        });
    }

    public void deleteItem(Item item) {
        PultusORMCondition condition = new PultusORMCondition.Builder().eq("id", item.getId()).build();
        mOrm.delete(item, condition);
    }

    public void dropItem() {
        mOrm.drop(new Item());
    }

}
