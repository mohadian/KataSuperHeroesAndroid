/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.idlingresource.RecyclerViewWithContentIdlingResource;
import com.karumi.katasuperheroes.matchers.ToolbarMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.SuperHeroesActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SuperHeroesActivityTest {

    //    @Rule
    //    public DaggerMockRule<MainComponent> daggerRule = new DaggerMockRule<>(MainComponent.class, new MainModule()).set(new DaggerMockRule.ComponentSetter<MainComponent>() {
    //        @Override
    //        public void setComponent(MainComponent component) {
    //            SuperHeroesApplication app = (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    //            app.setComponent(component);
    //        }
    //    });

    @Rule
    public IntentsTestRule<SuperHeroesActivity> activityRule = new IntentsTestRule<>(SuperHeroesActivity.class, true,
            true);

    @Mock
    SuperHeroesRepository repository;


    @Before
    public void registerIntentServiceIdlingResource() {
        IdlingResource idlingResource = new RecyclerViewWithContentIdlingResource(activityRule.getActivity(), R.id
                .recycler_view, 12);
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIntentServiceIdlingResource() {
        Espresso.unregisterIdlingResources();
    }

    @Test
    public void showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes();

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
    }

    @Test
    public void showsTheToolbar() {
        givenThereAreNoSuperHeroes();

        startActivity();

        //check if the toolbar is displayed and it has a childview with the mentioned text
        onView(allOf(withId(R.id.toolbar), hasDescendant(withText("Kata Super Heroes")))).check(matches(isDisplayed()));
    }

    @Test
    public void showsTheToolbarWithToolbarMatcher() {
        startActivity();
        ToolbarMatcher.onToolbarWithTitle("Kata Super Heroes").check(matches(isDisplayed()));
    }

    @Test
    public void showsTheFirstItemWithCorrectData() {
        onView(withText("Iron Man")).check(matches(isDisplayed()));
    }

    @Test
    public void showsTheMultipleItemWithCorrectData() {
        startActivity();
        onView(withText("Mostafa")).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view), hasDescendant(withId(R.id.tv_super_hero_name)), hasDescendant(withText("Mostafa")))).check(matches(isDisplayed()));
    }

    @Test
    public void showsTheMultipleItemsWithCorrectData() {
        startActivity();
        RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view)).withItems(repository.getAll()).check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
            @Override
            public void check(SuperHero item, View view, NoMatchingViewException e) {
                matches(hasDescendant(withText(item.getName()))).check(view, e);
            }
        });
    }

    @Test
    public void checkIfAvengersItemsHasBadges() {
        givenThereIsMultipleSuperHeroes();
        startActivity();
        RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view)).withItems(repository.getAll()).check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
            @Override
            public void check(SuperHero item, View view, NoMatchingViewException e) {
                matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge), withEffectiveVisibility((item.isAvenger() ? ViewMatchers.Visibility.VISIBLE : ViewMatchers.Visibility.GONE))))).check(view, e);
            }
        });
    }

    @Test
    public void givenThereIsMultipleSuperHeroes() {
        ArrayList<SuperHero> superHeros = new ArrayList<>();
        superHeros.add(new SuperHero("Mostafa", null, false, "Has kick-ass coding skills"));
        superHeros.add(new SuperHero("Coder 1", null, false, "Has kick-ass coding skills"));
        superHeros.add(new SuperHero("Coder 2", null, true, "Has kick-ass coding skills"));
        superHeros.add(new SuperHero("Coder 3", null, false, "Has kick-ass coding skills"));
        when(repository.getAll()).thenReturn(superHeros);
    }

    @Test
    public void givenThereIsASuperHeroes() {
        when(repository.getAll()).thenReturn(Arrays.asList(new SuperHero("Mostafa", null, false, "Has kick-ass coding " + "skills")));
    }

    private void givenThereAreNoSuperHeroes() {
        when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
    }

    private SuperHeroesActivity startActivity() {
        return activityRule.launchActivity(null);
    }
}