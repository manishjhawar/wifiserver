package org.openintents.wifiserver.test;

import junit.framework.AssertionFailedError;

import org.openintents.wifiserver.OIWiFiServerActivity_;
import org.openintents.wifiserver.R;
import org.openintents.wifiserver.preference.OIWiFiPreferencesActivity_;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.Smoke;

import com.jayway.android.robotium.solo.Solo;

public class TestPreferencesActivity extends InstrumentationTestCase {
    
    private Solo solo;
    private static final String TAG = TestPreferencesActivity.class.getSimpleName();
    private Activity activity;
    
    private String getString(int resId) {
        return activity.getString(resId);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent i = new Intent();
        i.setAction("android.intent.action.MAIN");
        i.setClassName(OIWiFiServerActivity_.class.getPackage().getName(), OIWiFiServerActivity_.class.getCanonicalName());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity = getInstrumentation().startActivitySync(i);

        this.solo = new Solo(getInstrumentation(), activity);
    }
    
    @Override
    protected void tearDown() {
        solo.finishOpenedActivities();
    }
    
    /**
     * Enables or disables ssl by clicking on the specific preference.
     * Preference activity must be in foreground when calling this function.
     * 
     * @param enable <code>true</code> if ssl should be enabled, <code>false</code> otherwise.
     */
    private void enableSSL(boolean enable) {
        solo.assertCurrentActivity("Expected "+OIWiFiPreferencesActivity_.class.getSimpleName()+" activity!", OIWiFiPreferencesActivity_.class);
        if (enable ^ PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.prefsSSLEnableKey), Boolean.parseBoolean(getString(R.string.prefsSSLEnableDefault)))) {
            solo.clickOnText(getString(R.string.prefsSSLEnableTitle));
        }        
    }
    
    private int getSSLPort() {
        return PreferenceManager.getDefaultSharedPreferences(activity).getInt(getString(R.string.prefsSSLPortKey), Integer.parseInt(getString(R.string.prefsSSLPortDefault))); 
    }
    
    private int getStdPort() {
        return PreferenceManager.getDefaultSharedPreferences(activity).getInt(getString(R.string.prefsPortKey), Integer.parseInt(getString(R.string.prefsPortDefault))); 
    }
    
    private void gotoPreferenceActivity() {
        solo.assertCurrentActivity("Expected "+OIWiFiServerActivity_.class.getSimpleName()+" activity!", OIWiFiServerActivity_.class);
        solo.clickOnMenuItem(getString(R.string.preferences), true);
        solo.assertCurrentActivity("Expected "+OIWiFiPreferencesActivity_.class.getSimpleName()+" activity!", OIWiFiPreferencesActivity_.class);
    }
    
    @Smoke
    public void test000Eula() {
        String accept = getString(org.openintents.distribution.R.string.oi_distribution_eula_accept);
        String cancel = getString(org.openintents.distribution.R.string.oi_distribution_eula_refuse);
        boolean existsAccept = solo.searchButton(accept);
        boolean existsCancel = solo.searchButton(cancel);

        if (existsAccept && existsCancel) {
            solo.clickOnButton(accept);
        }
    }

    @Smoke
    public void test001RecentChanges() {
        String recentChanges = getString(org.openintents.distribution.R.string.oi_distribution_newversion_recent_changes);
        String cont = getString(org.openintents.distribution.R.string.oi_distribution_newversion_continue);
        while (solo.scrollUp())
            ;
        boolean existsRecentChanges = solo.searchText(recentChanges);
        boolean existsCont = solo.searchButton(cont);

        if (existsRecentChanges && existsCont) {
            solo.clickOnButton(cont);
        }
    }    
    
    /**
     * This test activates ssl and sets the standard port to the same value as
     * ssl port.
     */
    public void testDuplicatedPort_1() {
        gotoPreferenceActivity();
        enableSSL(true);
        int sslPort = getSSLPort();
        
        solo.clickOnText(getString(R.string.prefsPortTitle));
        solo.clearEditText(0);
        solo.waitForText("");
        solo.typeText(0, String.valueOf(sslPort));
        
        solo.clickOnButton(getString(android.R.string.ok));
        
        assertTrue(solo.waitForText(getString(R.string.errorPortDuplicate)));
        
        solo.goBackToActivity(OIWiFiServerActivity_.class.getSimpleName());
    }
    
    /**
     * This test activates ssl and sets the ssl port to the same value as
     * standard port.
     */
    public void testDuplicatedPort_2() {
        gotoPreferenceActivity();
        enableSSL(true);
        int stdPort = getStdPort();
        
        solo.clickOnText(getString(R.string.prefsSSLPortTitle));
        solo.clearEditText(0);
        solo.waitForText("");
        solo.typeText(0, String.valueOf(stdPort));
        
        solo.clickOnButton(getString(android.R.string.ok));
        
        assertTrue(solo.waitForText(getString(R.string.errorPortDuplicate)));

        solo.goBackToActivity(OIWiFiServerActivity_.class.getSimpleName());
    }
    
    /**
     * This test deactivates ssl and sets the standard port to the same value as
     * ssl port.
     */
    public void testDuplicatedPort_3() {
        gotoPreferenceActivity();
        enableSSL(false);
        int sslPort = getSSLPort();
        
        solo.clickOnText(getString(R.string.prefsPortTitle));
        solo.clearEditText(0);
        solo.waitForText("");
        solo.typeText(0, String.valueOf(sslPort));
        
        solo.clickOnButton(getString(android.R.string.ok));
        
        assertTrue(solo.waitForText(getString(R.string.errorPortDuplicate)));

        solo.goBackToActivity(OIWiFiServerActivity_.class.getSimpleName());
    }
}
