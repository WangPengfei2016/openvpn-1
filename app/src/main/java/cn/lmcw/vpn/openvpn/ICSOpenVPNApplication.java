/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.openvpn;

import android.app.Application;

import cn.lmcw.vpn.openvpn.core.PRNGFixes;
import cn.lmcw.vpn.openvpn.core.StatusListener;

/*
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
*/

public class ICSOpenVPNApplication extends Application {
    private StatusListener mStatus;

    @Override
    public void onCreate() {
        super.onCreate();


        PRNGFixes.apply();

        mStatus = new StatusListener();
        mStatus.init(getApplicationContext());
    }
}
