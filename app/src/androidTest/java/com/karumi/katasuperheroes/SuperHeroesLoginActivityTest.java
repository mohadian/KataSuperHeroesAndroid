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

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.SuperHeroesLoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.VerificationModes.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SuperHeroesLoginActivityTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
            new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
                    new DaggerMockRule.ComponentSetter<MainComponent>() {
                        @Override
                        public void setComponent(MainComponent component) {
                            SuperHeroesApplication app =
                                    (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                                            .getTargetContext()
                                            .getApplicationContext();
                            app.setComponent(component);
                        }
                    });

    @Rule
    public IntentsTestRule<SuperHeroesLoginActivity> activityRule =
            new IntentsTestRule<>(SuperHeroesLoginActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;

    @Test
    public void showsErrorMessageIfWrongCredentials() {
        activityRule.launchActivity(null);
        onView(withId(R.id.login_email))
                .perform(typeText("notok@google.com"), closeSoftKeyboard());
        onView(withId(R.id.action_login))
                .perform(click());
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(isDisplayed()));
    }

    @Test
    public void showsErrorMessageIfWrongCredentialsIsIntent() {
        activityRule.launchActivity(null);
        onView(withId(R.id.login_email))
                .perform(typeText("notok@google.com"), closeSoftKeyboard());
        onView(withId(R.id.action_login))
                .perform(click());
        intended(toPackage("com.karumi.katasuperheroes"), times(0));
    }

    @Test
    public void openSuperHeroesIfValidCredentials() {
        activityRule.launchActivity(null);
        onView(withId(R.id.login_email))
                .perform(typeText("ok@google.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(typeText("supersecure"), closeSoftKeyboard());

        Mockito.when(repository.login("ok@google.com", "supersecure")).thenReturn(true);

        onView(withId(R.id.action_login))
                .perform(click());
        intended(toPackage( "com.karumi.katasuperheroes"), times(1));
        //or intended(hasComponent(SuperHeroesLoginActivity.class.getCanonicalName()));

    }

}