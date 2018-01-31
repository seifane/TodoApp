package ch.idoucha.todolist;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Created by Seïfane Idouchach on 1/31/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void stage1_testLaunch() {
       /* try {
            Thread.sleep(250);
            onView(withText(R.string.action_flush)).perform(click());
            onView(withId(android.R.id.button1)).perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        onView(withId(R.id.fab)).perform(click());
        try {
            onView(withId(R.id.title_edit)).perform(typeText("Hello test title")).perform(closeSoftKeyboard());
            Thread.sleep(250);

            onView(withId(R.id.content_edit)).perform(typeText("Hello test")).perform(closeSoftKeyboard());
            Thread.sleep(250);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.list_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.title_edit)).check(matches(withText("Hello test title")));
        onView(withId(R.id.content_edit)).check(matches(withText("Hello test")));
    }

    @Test
    public void stage2_testLaunch() {
        onView(withId(R.id.fab)).perform(click());
        try {
            onView(withId(R.id.title_edit)).perform(typeText("Hello DELETE")).perform(closeSoftKeyboard());
            Thread.sleep(250);

            onView(withId(R.id.content_edit)).perform(typeText("DELETE DELETE")).perform(closeSoftKeyboard());
            Thread.sleep(250);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.list_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.title_edit)).check(matches(withText("Hello DELETE")));
        onView(withId(R.id.content_edit)).check(matches(withText("DELETE DELETE")));
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.list_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.title_edit)).check(matches(withText("Hello test title")));
        onView(withId(R.id.content_edit)).check(matches(withText("Hello test")));
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
    }
}
