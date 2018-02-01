package ch.idoucha.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.idoucha.todolist.helper.DbHelper;
import ch.idoucha.todolist.model.Item;

public class AddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private DbHelper mHelper;

    @BindView(R.id.title_edit)
    EditText mEditTitle;
    @BindView(R.id.content_edit)
    EditText mEditContent;
    @BindView(R.id.date_edit)
    EditText mEditDate;
    @BindView(R.id.time_edit)
    EditText mEditTime;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_note_layout)
    ConstraintLayout mLayout;

    int year, month, day, hour, minute, second;
    boolean isDateSet = false, isTimeSet = false, isEditing = false;
    int currentId = -1;
    Item currentItem = null;
    private SwitchCompat mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        mHelper = new DbHelper(getApplicationContext());

        detectAndSetupEditMode();

        setupOnFocusChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        if (!isEditing) {
            menu.findItem(R.id.action_switch).setVisible(false);
            return true;
        }
        menu.findItem(R.id.action_switch).setActionView(R.layout.toolbar_switch);
        mSwitch = menu.findItem(R.id.action_switch).getActionView().findViewById(R.id.switch_toolbar);
        if (currentItem.getStatus() == Item.STATE_ACTIVE)
            mSwitch.setChecked(false);
        else
            mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (currentItem == null)
                    return;
                if (isChecked) {
                    currentItem.setStatus(Item.STATE_DONE);
                } else {
                    currentItem.setStatus(Item.STATE_ACTIVE);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            doRemove();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doRemove() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_delete))
                .setMessage(getString(R.string.action_delete_prompt))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isEditing)
                            removeExisting();
                        else
                            removeDraft();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeExisting() {
        if (currentItem != null)
            mHelper.deleteItem(currentItem);
        exit();
    }

    private void removeDraft() {
        exit();
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
        String dateString = DateTimeUtils.formatWithStyle(date, DateTimeStyle.MEDIUM);
        this.mEditDate.setText(dateString);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        isTimeSet = true;
        this.hour = hourOfDay;
        this.minute = minute;
        this.second = second;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, this.hour + TimeZone.getDefault().getRawOffset()/(60 * 60 * 1000));
        cal.set(Calendar.MINUTE, this.minute);
        cal.set(Calendar.SECOND, this.second);

        Date date = cal.getTime();

        this.mEditTime.setText(DateTimeUtils.formatWithPattern(date, "HH:mm"));
    }

    private void detectAndSetupEditMode() {
        if (getIntent().hasExtra("ID")) {
            this.currentId = getIntent().getIntExtra("ID", -1);
            currentItem = mHelper.getItemById(this.currentId);
            if (currentItem != null) {
                Date date;
                if ((date = currentItem.getDateAsDate()) != null) {
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

                    this.mEditDate.setText(currentItem.getDateString());
                    this.mEditTime.setText(currentItem.getTimeString());
                }
                this.mEditTitle.setText(currentItem.getTitle());
                this.mEditContent.setText(currentItem.getContent());

                this.isEditing = true;
                getSupportActionBar().setTitle("Edit a note");

            }
        }
    }

    @OnClick(R.id.fab)
    public void fabOnClick(View view) {
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
                cal.set(Calendar.SECOND, 0);
            }
            Date date = cal.getTime();
            epochDate = date.getTime();
        }

        if (currentItem != null) {
            currentItem.setTitle(mEditTitle.getText().toString());
            currentItem.setContent(mEditContent.getText().toString());
            if (epochDate > 0) {
                currentItem.setDate(epochDate);
            }
            mHelper.updateItem(getApplicationContext(), currentItem);
        } else {
            if (epochDate > 0) {
                item = new Item(mEditTitle.getText().toString(), mEditContent.getText().toString(), epochDate);
            } else {
                item = new Item(mEditTitle.getText().toString(), mEditContent.getText().toString());
            }
            mHelper.addItem(getApplicationContext(), item);
        }
        exit();
    }

    private void setupOnFocusChange() {
        mEditDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        mEditTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog dpd = TimePickerDialog.newInstance(
                            AddActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                    mLayout.requestFocus();
                }
            }
        });
    }

    private void exit() {
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
