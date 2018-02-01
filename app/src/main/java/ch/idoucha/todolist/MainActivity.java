package ch.idoucha.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.idoucha.todolist.helper.DbHelper;
import ch.idoucha.todolist.model.Item;
import ninja.sakib.pultusorm.core.PultusORM;
import ninja.sakib.pultusorm.core.PultusORMCondition;
import ninja.sakib.pultusorm.core.PultusORMQuery;

public class MainActivity extends AppCompatActivity {

    private DbHelper mHelper;

    private HomeListAdapter mAdapter;

    @BindView(R.id.list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private IconSwitch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mHelper = new DbHelper(getApplicationContext());

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
        mAdapter.setList(mHelper.getAllActiveItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_switch).setActionView(R.layout.toolbar_switch_main);
        mSwitch = menu.findItem(R.id.action_switch).getActionView().findViewById(R.id.switch_toolbar);
        mSwitch.setChecked(IconSwitch.Checked.LEFT);
        mSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                if (current == IconSwitch.Checked.RIGHT) {
                    mAdapter.setList(mHelper.getAllDoneItems());
                } else {
                    mAdapter.setList(mHelper.getAllActiveItems());
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_flush) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.action_flush))
                    .setMessage(getString(R.string.action_flush_prompt))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mHelper.dropItem();
                            mAdapter.setList(mHelper.getAllItems());
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
