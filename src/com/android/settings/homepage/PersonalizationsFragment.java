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

package com.android.settings.homepage;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

import androidx.preference.Preference;
import com.radiant.support.preferences.SwitchPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

public class PersonalizationsFragment extends DashboardFragment implements OnPreferenceChangeListener {

    private static final String TAG = "PersonalizationsFragment";
    private SwitchPreference mPitchPreference;
    IOverlayManager mOverlayManager;


    @Override
    public int getMetricsCategory() {
        return MetricsEvent.Radiant;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        mPitchPreference = findPreference("pitch_theme");
        try {
            mPitchPreference.setChecked(mOverlayManager.getOverlayInfo("com.radiant.pitchsystem", UserHandle.USER_CURRENT).isEnabled());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mPitchPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPitchPreference) {
            setOverlay("com.radiant.pitchsystem", (Boolean) newValue);
            setOverlay("com.radiant.pitchsettings", (Boolean) newValue);
            setOverlay("com.radiant.pitchsystemui", (Boolean) newValue);
            return true;
        }
        return false;
    }

    private void setOverlay(String overlay, boolean status) {
        try {
            mOverlayManager.setEnabled(overlay, status, UserHandle.USER_CURRENT);
        } catch (RemoteException | IllegalStateException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.other_settings;
    }

}
