/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.homepage;

import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.settings.SettingsEnums;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.android.settings.Utils;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import com.android.settings.R;
import com.android.settings.accounts.AvatarViewMixin;
import com.android.settings.core.HideNonSystemOverlayMixin;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;

public class SettingsHomepageActivity extends FragmentActivity implements AppBarLayout.OnOffsetChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ForcedHomepageTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_homepage_container);
        final View root = findViewById(R.id.settings_homepage_container);
        root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // setHomepageContainerPaddingTop();


        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getResources().getText(R.string.settings_label));
        toolBarLayout.setCollapsedTitleTextColor(Utils.getColorAccentDefaultColor(this));
        toolBarLayout.setExpandedTitleColor(Utils.getColorAccentDefaultColor(this));
        toolBarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.UnExpanded);


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        final ImageView imageview = findViewById(R.id.search_action_bar);
        FeatureFactory.getFactory(this).getSearchFeatureProvider()
                .initSearchToolbar(this /* activity */, imageview, SettingsEnums.SETTINGS_HOMEPAGE);

        showFragment(new TopLevelSettings(), R.id.main_content);
        ((FrameLayout) findViewById(R.id.main_content))
                .getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        // homepageSpacer = findViewById(R.id.settings_homepage_spacer);
        // homepageMainLayout = findViewById(R.id.main_content_scrollable_container);

        // if (!isHomepageSpacerEnabled() && homepageSpacer != null && homepageMainLayout != null) {
        //     homepageSpacer.setVisibility(View.GONE);
        //     setMargins(homepageMainLayout, 0,0,0,0);
        // }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset)
    {
        ImageView ivSearch = (ImageView) findViewById(R.id.search_action_bar);
        float ivScale;
        if (offset == 0)
        {
            ivScale = (float) 1.35;
        }
        else
        {
            // Not fully expanded or collapsed
            float scrollPercentage = (float) Math.abs(offset)/appBarLayout.getTotalScrollRange();
            ivScale = (float) 1.35 - scrollPercentage;
        }
        if(ivScale < 1.0) ivScale = (float) 1.0;
        ivSearch.setScaleX(ivScale);
        ivSearch.setScaleY(ivScale);
    }

    private void showFragment(Fragment fragment, int id) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment showFragment = fragmentManager.findFragmentById(id);

        if (showFragment == null) {
            fragmentTransaction.add(id, fragment);
        } else {
            fragmentTransaction.show(showFragment);
        }
        fragmentTransaction.commit();
    }

    @VisibleForTesting
    void setHomepageContainerPaddingTop() {
        final View view = this.findViewById(R.id.homepage_container);

        final int searchBarHeight = getResources().getDimensionPixelSize(R.dimen.search_bar_height);
        final int searchBarMargin = getResources().getDimensionPixelSize(R.dimen.search_bar_margin);

        // The top padding is the height of action bar(48dp) + top/bottom margins(16dp)
        final int paddingTop = searchBarHeight + searchBarMargin * 2;
        view.setPadding(0 /* left */, paddingTop, 0 /* right */, 0 /* bottom */);

        // Prevent inner RecyclerView gets focus and invokes scrolling.
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }
}
