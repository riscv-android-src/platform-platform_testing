/*
 * Copyright (C) 2021 The Android Open Source Project
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

package android.platform.scenario.multiuser;

import static junit.framework.Assert.assertTrue;

import android.content.pm.UserInfo;
import android.platform.helpers.AutoUtility;
import android.platform.helpers.HelperAccessor;
import android.platform.helpers.IAutoProfileHelper;
import android.platform.helpers.IAutoSettingHelper;
import android.platform.helpers.MultiUserHelper;
import androidx.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test will create user through API and delete the same user from UI
 *
 * <p>It should be running under user 0, otherwise instrumentation may be killed after user
 * switched.
 */
@RunWith(AndroidJUnit4.class)
public class AddUserQuickSettings {

    private final MultiUserHelper mMultiUserHelper = MultiUserHelper.getInstance();
    private HelperAccessor<IAutoProfileHelper> mProfilesHelper;
    private HelperAccessor<IAutoSettingHelper> mSettingHelper;

    public AddUserQuickSettings() {
        mProfilesHelper = new HelperAccessor<>(IAutoProfileHelper.class);
        mSettingHelper = new HelperAccessor<>(IAutoSettingHelper.class);
    }

    @BeforeClass
    public static void exitSuw() {
        AutoUtility.exitSuw();
    }

    @After
    public void goBackToHomeScreen() {
        mSettingHelper.get().goBackToSettingsScreen();
    }

    @Test
    public void testAddUser() throws Exception {
        // create new user quick settings
        UserInfo initialUser = mMultiUserHelper.getCurrentForegroundUserInfo();
        mProfilesHelper.get().addProfileQuickSettings(initialUser.name);
        // switched to new user
        UserInfo newUser = mMultiUserHelper.getCurrentForegroundUserInfo();
        // switch from new user to initial user
        mProfilesHelper.get().switchProfile(newUser.name, initialUser.name);
        // verify new user is seen in list of users
        assertTrue(mMultiUserHelper.getUserByName(newUser.name) != null);
        // remove new user
        mMultiUserHelper.removeUser(newUser);
    }
}
