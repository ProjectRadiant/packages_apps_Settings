/*
 * Copyright (C) 2021 Project Radiant
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

package com.radiant.settings.controller;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import android.content.Intent;
import android.util.Log;
import com.radiant.settings.color.WallpaperColorActivity;

public class RadiantColorPreferenceController extends BasePreferenceController {

    public RadiantColorPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if(getPreferenceKey().equals(preference.getKey())){
            Intent intent = new Intent(mContext, WallpaperColorActivity.class);
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }
}
