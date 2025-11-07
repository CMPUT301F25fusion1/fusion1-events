package com.example.fusion1_events;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.allOf;



import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


//Tests UI and sign-up validity for SignUpActivity
@RunWith(AndroidJUnit4.class)
public class SignUpActivityUITest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> rule = new ActivityScenarioRule<>(SignUpActivity.class);

    // Tests if edittext inputs text.
    @Test
    public void EditTextsTest() {
        onView(withId(R.id.signupName)).perform(replaceText("TestName"));
        onView(withId(R.id.signupEmail)).perform(replaceText("test@email.com"));
        onView(withId(R.id.signupNumber)).perform(replaceText("1234567890"));

        onView(withId(R.id.signupName)).check(matches(withText("TestName")));
        onView(withId(R.id.signupEmail)).check(matches(withText("test@email.com")));
        onView(withId(R.id.signupNumber)).check(matches(withText("1234567890")));
    }

    // checks if spinner selects.
    @Test
    public void SpinnerTest() {

        onView(withId(R.id.signupRole)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ENTRANT"))).perform(click());

        onView(withId(R.id.signupRole)).check(matches(withSpinnerText("ENTRANT")));
    }

    // checks if sign up fails without valid role.
    @Test
    public void signupWithoutRole() {
        onView(withId(R.id.signupName)).perform(replaceText("TestName"));
        onView(withId(R.id.signupEmail)).perform(replaceText("test@email.com"));


        onView(withId(R.id.buttonSignUP)).perform(click());

        //screen does not change
        onView(withId(R.id.buttonSignUP)).check(matches(isDisplayed()));
    }

    // checks if sign up fails without name.
    @Test
    public void signupWithoutName() {

        onView(withId(R.id.signupName)).perform(replaceText(""));
        onView(withId(R.id.signupEmail)).perform(replaceText("alice@example.com"));

        onView(withId(R.id.signupRole)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ENTRANT"))).perform(click());

        onView(withId(R.id.buttonSignUP)).perform(click());

        //screen does not change
        onView(withId(R.id.buttonSignUP)).check(matches(isDisplayed()));
    }

    // checks if sign up fails without email.
    @Test
    public void signupWithoutEmail() {

        onView(withId(R.id.signupName)).perform(replaceText("TestName"));
        onView(withId(R.id.signupEmail)).perform(replaceText(""));

        onView(withId(R.id.signupRole)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ENTRANT"))).perform(click());

        onView(withId(R.id.buttonSignUP)).perform(click());

        //screen does not change
        onView(withId(R.id.buttonSignUP)).check(matches(isDisplayed()));
    }

}
