package ch.idoucha.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.idoucha.todolist.model.Item;
import ninja.sakib.pultusorm.core.PultusORM;
import ninja.sakib.pultusorm.core.PultusORMCondition;
import ninja.sakib.pultusorm.core.PultusORMQuery;

public class MainActivity extends AppCompatActivity {

    private PultusORM orm;

    private HomeListAdapter mAdapter;

    @BindView(R.id.list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        Log.d("DEBUG", appPath);
        orm = new PultusORM("todos.db", appPath);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddActivity.class);
                startActivity(i);
            }
        });

        mAdapter = new HomeListAdapter(getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PultusORMCondition condition = new PultusORMCondition.Builder()
                .sort("id", PultusORMQuery.Sort.DESCENDING)
                .build();
        List<Object> tmp = orm.find(new Item(), condition);
        List<Item> items = new ArrayList<>();
        for (Object o : tmp) {
            items.add((Item) o);
        }
        mAdapter.setList(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
