package com.udacity.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkData() {
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(1));
        onView(withText("Brownies")).check(matches(isDisplayed()));
    }

    @Test
    public void testIntent() throws Exception {
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(1));
        onView(withText("Brownies")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(3));
        onView(withText("Cheesecake")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(2));
        onView(withText("Yellow Cake")).check(matches(isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0,ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.pressBack();

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1,ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.pressBack();

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(3,ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.pressBack();

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(2,ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.linearLayout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.pressBack();
        Espresso.pressBack();
    }
}