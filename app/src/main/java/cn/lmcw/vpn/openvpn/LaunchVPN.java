/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.openvpn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import cn.lmcw.vpn.R;
import cn.lmcw.vpn.openvpn.core.ConnectionStatus;
import cn.lmcw.vpn.openvpn.core.IServiceStatus;
import cn.lmcw.vpn.openvpn.core.OpenVPNStatusService;
import cn.lmcw.vpn.openvpn.core.PasswordCache;
import cn.lmcw.vpn.openvpn.core.Preferences;
import cn.lmcw.vpn.openvpn.core.VPNLaunchHelper;
import cn.lmcw.vpn.openvpn.core.VpnStatus;

//import openvpn.activities.LogWindow;

/**
 * This Activity actually handles two stages of a launcher shortcut's life cycle.
 * <p/>
 * 1. Your application offers to provide shortcuts to the launcher.  When
 * the user installs a shortcut, an activity within your application
 * generates the actual shortcut and returns it to the launcher, where it
 * is shown to the user as an icon.
 * <p/>
 * 2. Any time the user clicks on an installed shortcut, an intent is sent.
 * Typically this would then be handled as necessary by an activity within
 * your application.
 * <p/>
 * We handle stage 1 (creating a shortcut) by simply sending back the information (in the form
 * of an {@link Intent} that the launcher will use to create the shortcut.
 * <p/>
 * You can also implement this in an interactive way, by having your activity actually present
 * UI for the user to select the specific nature of the shortcut, such as a contact, picture, URL,
 * media item, or action.
 * <p/>
 * We handle stage 2 (responding to a shortcut) in this sample by simply displaying the contents
 * of the incoming {@link Intent}.
 * <p/>
 * In a real application, you would probably use the shortcut intent to display specific content
 * or start a particular operation.
 */
public class LaunchVPN extends Activity {

    public static final String EXTRA_KEY = "cn.lmcw.vpn.shortcutProfileUUID";
    public static final String EXTRA_NAME = "cn.lmcw.vpn.shortcutProfileName";
    public static final String EXTRA_HIDELOG = "cn.lmcw.vpn.showNoLogWindow";
    public static final String CLEARLOG = "clearlogconnect";


    private static final int START_VPN_PROFILE = 70;


    private VpnProfile mSelectedProfile;
    private boolean mhideLog = false;

    private boolean mCmfixed = false;
    private String mTransientAuthPW;
    private String mTransientCertOrPCKS12PW;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.launchvpn);
        startVpnFromIntent();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            IServiceStatus service = IServiceStatus.Stub.asInterface(binder);
            try {
                if (mTransientAuthPW != null)

                    service.setCachedPassword(mSelectedProfile.getUUIDString(), PasswordCache.AUTHPASSWORD, mTransientAuthPW);
                if (mTransientCertOrPCKS12PW != null)
                    service.setCachedPassword(mSelectedProfile.getUUIDString(), PasswordCache.PCKS12ORCERTPASSWORD, mTransientCertOrPCKS12PW);

                onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    protected void startVpnFromIntent() {
        // Resolve the intent

        final Intent intent = getIntent();
        final String action = intent.getAction();

        // If the intent is a request to create a shortcut, we'll do that and exit

        if (Intent.ACTION_MAIN.equals(action)) {
            // Check if we need to clear the log
            if (Preferences.getDefaultSharedPreferences(this).getBoolean(CLEARLOG, true))
                VpnStatus.clearLog();

            // we got called to be the starting point, most likely a shortcut
            String shortcutUUID = intent.getStringExtra(EXTRA_KEY);


            mhideLog = intent.getBooleanExtra(EXTRA_HIDELOG, false);

            VpnProfile profileToConnect = ProfileUtil.getProfile(shortcutUUID);


            if (profileToConnect == null) {
                VpnStatus.logError(R.string.shortcut_profile_notfound);
                // show Log window to display error
                showLogWindow();
                finish();
            } else {
                mSelectedProfile = profileToConnect;
                launchVPN();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(LaunchVPN.this, "启动VPN", Toast.LENGTH_SHORT).show();

                //设置使用的 配置文件
                ProfileUtil.setUseConnectedProfile(mSelectedProfile);
                Intent intent = new Intent(LaunchVPN.this, OpenVPNStatusService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//
//                VpnStatus.updateStateString("USER_VPN_PASSWORD", "", R.string.state_user_vpn_password,
//                        ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
                VPNLaunchHelper.startOpenVpn(mSelectedProfile, getBaseContext());
                finish();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                VpnStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                        ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    VpnStatus.logError(R.string.nought_alwayson_warning);

                finish();
            }
        }
    }

    void showLogWindow() {

//        Intent startLW = new Intent(getBaseContext(), LogWindow.class);
//        startLW.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(startLW);

    }

    void showConfigErrorDialog(int vpnok) {
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle(R.string.config_error_found);
        d.setMessage(vpnok);
        d.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            setOnDismissListener(d);
        d.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setOnDismissListener(AlertDialog.Builder d) {
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    void launchVPN() {
        int vpnok = mSelectedProfile.checkProfile(this);
        if (vpnok != R.string.no_error_found) {
            showConfigErrorDialog(vpnok);
            return;
        }

        Intent intent = VpnService.prepare(this);
        // Check if we want to fix /dev/tun
        SharedPreferences prefs = Preferences.getDefaultSharedPreferences(this);
        boolean usecm9fix = prefs.getBoolean("useCM9Fix", false);
        boolean loadTunModule = prefs.getBoolean("loadTunModule", false);

        if (loadTunModule)
            execeuteSUcmd("insmod /system/lib/modules/tun.ko");

        if (usecm9fix && !mCmfixed) {
            execeuteSUcmd("chown system /dev/tun");
        }

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                VpnStatus.logError(R.string.no_vpn_support_image);
                showLogWindow();
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }

    }

    private void execeuteSUcmd(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
            Process p = pb.start();
            int ret = p.waitFor();
            if (ret == 0)
                mCmfixed = true;
        } catch (InterruptedException | IOException e) {
            VpnStatus.logException("SU command", e);
        }
    }
}
