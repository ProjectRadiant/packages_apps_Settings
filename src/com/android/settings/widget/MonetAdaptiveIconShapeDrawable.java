/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.settings.widget;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Path;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.PathParser;
import android.content.res.TypedArray;
import android.content.Context;
import android.app.ActivityThread;
import com.nezuko.support.monet.colorgiber;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Draws a filled {@link ShapeDrawable} using the path from {@link AdaptiveIconDrawable}.
 */
public class MonetAdaptiveIconShapeDrawable extends ShapeDrawable {

    final Context context = ActivityThread.currentApplication();
    boolean isDarkM = context.getResources().getConfiguration().isNightModeActive();
    colorgiber cg = new colorgiber();

    public MonetAdaptiveIconShapeDrawable() {
        super();
    }

    public MonetAdaptiveIconShapeDrawable(Resources resources) {
        super();
        init(resources);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        init(r);
    }

    private void init(Resources resources) {
        final float pathSize = AdaptiveIconDrawable.MASK_SIZE;
        final Path path = new Path(PathParser.createPathFromPathData(
                resources.getString(com.android.internal.R.string.config_icon_mask)));
        setShape(new PathShape(path, pathSize, pathSize));
        if (!isDarkM){
            setColorFilter(cg.noSysPriviledgeMoment(4, 2, context), PorterDuff.Mode.SRC_ATOP);
        } else{
            setColorFilter(cg.noSysPriviledgeMoment(4, 8, context), PorterDuff.Mode.SRC_ATOP);
        }
    }
}