package ch.idoucha.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.idoucha.todolist.model.Item;
import ninja.sakib.pultusorm.core.PultusORM;
import ninja.sakib.pultusorm.core.PultusORMCondition;
import ninja.sakib.pultusorm.core.PultusORMQuery;
import ninja.sakib.pultusorm.core.PultusORMUpdater;

public class AddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private PultusORM orm;

    @BindView(R.id.title_edit)
    EditText title;
    @BindView(R.id.content_edit)
    EditText content;
    @BindView(R.id.date_edit)
    EditText date;
    @BindView(R.id.time_edit)
    EditText time;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_note_layout)
    ConstraintLayout mLayout;

    int year, month, day, hour, minute, second;
    boolean isDateSet = false, isTimeSet = false, isEditing = false;
    int currentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        orm = new PultusORM("todos.db", appPath);


        detectAndSetupEditMode();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item;
                long epochDate = 0;

                if (isDateSet) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    if (isTimeSet) {
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                    }
                    Date date = cal.getTime();
                    epochDate = date.getTime();
                }

                if (isEditing) {
                    PultusORMCondition condition = new PultusORMCondition.Builder().eq("id", currentId).build();
                    PultusORMUpdater.Builder builder = new PultusORMUpdater.Builder()
                            .set("title", title.getText().toString())
                            .set("content", content.getText().toString());
                    if (epochDate > 0) {
                        builder.set("date", epochDate);
                    }
                    PultusORMUpdater updater = builder.condition(condition).build();
                    orm.update(new Item(), updater);
                } else {
                    if (epochDate > 0) {
                        item = new Item(title.getText().toString(), content.getText().toString(), epochDate);
                    } else {
                        item = new Item(title.getText().toString(), content.getText().toString());
                    }
                    boolean id = orm.save(item);
                }
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            AddActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                    mLayout.requestFocus();
                }
            }
        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog dpd = TimePickerDialog.newInstance(
                            AddActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                    mLayout.requestFocus();
                }
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isDateSet = true;
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.year);
        cal.set(Calendar.MONTH, this.month);
        cal.set(Calendar.DAY_OF_MONTH, this.day);
        Date date = cal.getTime();
        String dateString = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG);
        this.date.setText(dateString);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        isTimeSet = true;
        this.hour = hourOfDay;
        this.minute = minute;
        this.second = second;
        this.time.setText(this.hour + ":" + this.minute);
    }

    private void detectAndSetupEditMode() {
        if (getIntent().hasExtra("ID")) {
            this.currentId = getIntent().getIntExtra("ID", -1);
            if (this.currentId >= 0) {
                PultusORMCondition condition = new PultusORMCondition.Builder()
                        .eq("id", this.currentId)
                        .build();
                List<Object> res = orm.find(new Item(), condition);
                if (res.size() == 1) {
                    Item item = (Item) res.get(0);
                    Date date;
                    if ((date = item.getDateAsDate()) != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        this.year = cal.get(Calendar.YEAR);
                        this.month = cal.get(Calendar.MONTH);
                        this.day = cal.get(Calendar.DAY_OF_MONTH);
                        this.hour = cal.get(Calendar.HOUR_OF_DAY);
                        this.minute = cal.get(Calendar.MINUTE);
                        this.second = cal.get(Calendar.SECOND);
                        this.isDateSet = true;
                        this.isTimeSet = true;

                        this.date.setText(item.getDateString());
                        this.time.setText(item.getTimeString());
                    }
                    this.title.setText(item.getTitle());
                    this.content.setText(item.getContent());

                    this.isEditing = true;
                    getSupportActionBar().setTitle("Edit a note");

                }
            }
        }
    }
}
