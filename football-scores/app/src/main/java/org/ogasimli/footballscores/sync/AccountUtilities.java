package org.ogasimli.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * Created by com.ogasimli on 11.10.2015.
 */
public class AccountUtilities {

    private static final String LOG_TAG = AccountUtilities.class.getSimpleName();

    public static final String AUTHORITY = "com.ogasimli.footballscores";
    private static final String ACCOUNT_TYPE = "com.ogasimli.footballscores";
    private static final String ACCOUNT = "Football Scores";

    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SYNC_INTERVAL_IN_MINUTES = 60L * 4L;
    private static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    private static Account getAccount() {
        return new Account(ACCOUNT, ACCOUNT_TYPE);
    }

    public static Account createSyncAccount(Context context) {

        Account newAccount = AccountUtilities.getAccount();
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if(accountManager.addAccountExplicitly(newAccount, null, null)) {
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(newAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
        } else {
            List<PeriodicSync> periodicSyncs = ContentResolver.getPeriodicSyncs(newAccount, AUTHORITY);
            for(PeriodicSync periodicSync : periodicSyncs) {
                Log.d(LOG_TAG, "Periodic Sync info: " + periodicSync.toString());
            }
        }

        return newAccount;
    }

    public static boolean isSyncing() {
        return ContentResolver.isSyncActive(getAccount(), AUTHORITY);
    }
}
