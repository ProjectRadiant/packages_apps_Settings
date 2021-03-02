/*
 * Copyright (C) 2020 The Android Open Source Project
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
package com.android.settings.network.telephony;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/**
 * Preference controller for "Enable 2G"
 */
public class Enable2gPreferenceController extends TelephonyTogglePreferenceController {

    private static final String LOG_TAG = "Enable2gPreferenceController";
    private static final long BITMASK_2G =  TelephonyManager.NETWORK_TYPE_BITMASK_GSM
                | TelephonyManager.NETWORK_TYPE_BITMASK_GPRS
                | TelephonyManager.NETWORK_TYPE_BITMASK_EDGE
                | TelephonyManager.NETWORK_TYPE_BITMASK_CDMA
                | TelephonyManager.NETWORK_TYPE_BITMASK_1xRTT;

    private final MetricsFeatureProvider mMetricsFeatureProvider;

    private CarrierConfigManager mCarrierConfigManager;
    private TelephonyManager mTelephonyManager;

    /**
     * Class constructor of "Enable 2G" toggle.
     *
     * @param context of settings
     * @param key assigned within UI entry of XML file
     */
    public Enable2gPreferenceController(Context context, String key) {
        super(context, key);
        mCarrierConfigManager = context.getSystemService(CarrierConfigManager.class);
        mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    /**
     * Initialization based on a given subscription id.
     *
     * @param subId is the subscription id
     * @return this instance after initialization
     */
    public Enable2gPreferenceController init(int subId) {
        mSubId = subId;
        mTelephonyManager = mContext.getSystemService(TelephonyManager.class)
              .createForSubscriptionId(mSubId);
        return this;
    }

    @Override
    public int getAvailabilityStatus(int subId) {
        final PersistableBundle carrierConfig = mCarrierConfigManager.getConfigForSubId(subId);
        boolean visible =
                subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID
                && carrierConfig != null
                && !carrierConfig.getBoolean(CarrierConfigManager.KEY_HIDE_ENABLE_2G)
                && mTelephonyManager.isRadioInterfaceCapabilitySupported(
                    mTelephonyManager.CAPABILITY_ALLOWED_NETWORK_TYPES_USED);
        return visible ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public boolean isChecked() {
        long currentlyAllowedNetworkTypes = mTelephonyManager.getAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_2G);
        return (currentlyAllowedNetworkTypes & BITMASK_2G) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        long currentlyAllowedNetworkTypes = mTelephonyManager.getAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_2G);
        boolean enabled = (currentlyAllowedNetworkTypes & BITMASK_2G) != 0;
        if (enabled == isChecked) {
            return false;
        }
        long newAllowedNetworkTypes = currentlyAllowedNetworkTypes;
        if (isChecked) {
            newAllowedNetworkTypes = currentlyAllowedNetworkTypes | BITMASK_2G;
            Log.i(LOG_TAG, "Enabling 2g. Allowed network types: " + newAllowedNetworkTypes);
        } else {
            newAllowedNetworkTypes = currentlyAllowedNetworkTypes & ~BITMASK_2G;
            Log.i(LOG_TAG, "Disabling 2g. Allowed network types: " + newAllowedNetworkTypes);
        }
        mTelephonyManager.setAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_2G, newAllowedNetworkTypes);
        mMetricsFeatureProvider.action(
                mContext, SettingsEnums.ACTION_2G_ENABLED, isChecked);
        return true;
    }
}
