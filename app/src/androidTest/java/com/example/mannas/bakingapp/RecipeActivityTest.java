package com.example.mannas.bakingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ScrollToAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityTestRule = new ActivityTestRule<>(RecipeListActivity.class);
    private int getRVcount(){
        RecyclerView recyclerView = (RecyclerView) mActivityTestRule.getActivity().findViewById(R.id.recipe_list);
        return recyclerView.getAdapter().getItemCount();
    }

    @Test
    public void recipeActivityTest() {

        Date d1 = new Date();
        Integer cnt =0;
        while (cnt <=0){
            cnt = getRVcount();
            if(cnt>0)
                break;
            Date d2 = new Date();
            if(d2.getTime() - d1.getTime() > 30000){
                ViewInteraction imageView = onView(
                        allOf(withId(R.id.img), withContentDescription("Recipe Image"),
                                childAtPosition(childAtPosition(withId(R.id.recipe_list),
                                        0),
                                        0),
                                isDisplayed()));
                imageView.check(matches(isDisplayed()));
                //fails when there is no Data in the Recycler

            }
        }





        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recipe_list),
                        withParent(withId(R.id.frameLayout)),
                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView = onView(
                allOf(withText("Ingredients"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_detail_container),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Ingredients")));



        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.ingredients_recycler),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_detail_container),
                                        0),
                                1),
                        isDisplayed()));
        recyclerView3.check(matches(isDisplayed()));

        ViewInteraction recyclerView4 = onView(
                allOf(withId(R.id.ingredients_recycler), isDisplayed()));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.quantity), withText("2.0"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.measure), withText("CUP"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.ingredient), withText("Graham Cracker crumbs"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                4),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.steps_title)).perform(scrollTo() , click());
        onView(withId(R.id.steps_view_pager)).perform(scrollTo());
//        onView(withId(R.id.recipe_list)).perform(swipeUp());
//        onView(withId(R.id.recipe_list)).perform(swipeUp());
//        onView(withId(R.id.recipe_list)).perform(swipeUp());


        ViewInteraction pager = onView( allOf(withId(R.id.steps_view_pager) , isCompletelyDisplayed() )   );
        pager = onView(withId(R.id.steps_view_pager) ).perform(swipeLeft());
        pager = onView(withId(R.id.steps_view_pager)).perform(swipeRight());

        ViewInteraction pager_item = onView( allOf(withId(R.id.pager_item) , isDisplayed() )   );
        pager_item.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        pressBack();

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
