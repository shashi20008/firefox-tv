/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.components.locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Locale;

public abstract class LocaleAwareAppCompatActivity
        extends AppCompatActivity {

    private volatile Locale mLastLocale;

    /**
     * Is called whenever the application locale has changed. Your Activity must either update
     * all localised Strings, or replace itself with an updated version.
     */
    public abstract void applyLocale();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locales.initializeLocale(this);

        mLastLocale = LocaleManager.getInstance().getCurrentLocale(getApplicationContext());

        LocaleManager.getInstance().updateConfiguration(this, mLastLocale);

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final LocaleManager localeManager = LocaleManager.getInstance();

        localeManager.correctLocale(this, getResources(), getResources().getConfiguration());

        final Locale changed = localeManager.onSystemConfigurationChanged(this, getResources(), newConfig, mLastLocale);

        if (changed != null) {
            LocaleManager.getInstance().updateConfiguration(this, changed);
            applyLocale();
            setLayoutDirection(getWindow().getDecorView(), changed);
        }

        super.onConfigurationChanged(newConfig);
    }

    /**
     * Force set layout direction to RTL or LTR by Locale.
     *
     * @param view
     * @param locale
     */
    public static void setLayoutDirection(View view, Locale locale) {
        switch (TextUtilsCompat.getLayoutDirectionFromLocale(locale)) {
            case ViewCompat.LAYOUT_DIRECTION_RTL:
                ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL);
                break;
            case ViewCompat.LAYOUT_DIRECTION_LTR:
            default:
                ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_LTR);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        onConfigurationChanged(getResources().getConfiguration());

//        if (resultCode == SettingsActivity.ACTIVITY_RESULT_LOCALE_CHANGED) {
//            applyLocale();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((LocaleAwareApplication) getApplicationContext()).onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((LocaleAwareApplication) getApplicationContext()).onActivityPause();
    }
}
