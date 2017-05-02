/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.lmcw.vpn.R;
import cn.lmcw.vpn.openvpn.DisconnectVPN;
import cn.lmcw.vpn.openvpn.LaunchVPN;
import cn.lmcw.vpn.openvpn.ProfileUtil;
import cn.lmcw.vpn.openvpn.VpnProfile;
import cn.lmcw.vpn.openvpn.core.ConfigParser;
import cn.lmcw.vpn.openvpn.core.VpnStatus;
import cn.lmcw.vpn.utils.Util;


/**
 * Created by bruce on 2016/11/1.
 * BaseFragment
 */

public class YunFragment extends Fragment {
    public static YunFragment newInstance(String info) {
        Bundle args = new Bundle();
        YunFragment fragment = new YunFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    Button btnParse;
    Button btnOpen;
    Button btnClose;

    Button btnCenter;

    /* vpn连接配置文件对象 */
    private VpnProfile vpnProfile;
    AsyncTask<String, Integer, VpnProfile> asyncTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yun, null);

        btnCenter = (Button) view.findViewById(R.id.btn_center);

        btnParse = (Button) view.findViewById(R.id.btn_parse);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btnOpen = (Button) view.findViewById(R.id.open);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vpnProfile == null) {
                    Toast.makeText(getContext(), "vpnProfle is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                startOrStopVPN(vpnProfile);

            }
        });


        btnClose = (Button) view.findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VpnStatus.isVPNActive() && vpnProfile.getUUIDString().equals(VpnStatus.getLastConnectedVPNProfile())) {

                    Intent disconnectVPN = new Intent(getActivity(), DisconnectVPN.class);
                    startActivity(disconnectVPN);
                }

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        asyncTask = taskParseConf();
        asyncTask.execute("news.ovpn");


    }


    public void setStatus(final boolean enabled) {
        if (btnCenter == null)
            return;
        btnCenter.post(new Runnable() {
            @Override
            public void run() {
                btnCenter.setEnabled(enabled);
            }
        });
    }

    public void setText(final String str) {
        if (btnCenter == null)
            return;
        btnCenter.post(new Runnable() {
            @Override
            public void run() {
                btnCenter.setText(str);
            }
        }) ;
    }


    @NonNull
    private AsyncTask<String, Integer, VpnProfile> taskParseConf() {
        return new AsyncTask<String, Integer, VpnProfile>() {
            @Override
            protected VpnProfile doInBackground(String... params) {
                // 读取配置文件
                VpnProfile vpnProfile = null;
                try {
                    InputStream in = new FileInputStream(Util.getSDPath() + "/" + params[0]);
                    vpnProfile = doImport(in);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return vpnProfile;
            }

            @Override
            protected void onPostExecute(VpnProfile vpnProfile) {
                super.onPostExecute(vpnProfile);
                if (vpnProfile != null) {
                    YunFragment.this.vpnProfile = vpnProfile;
                    Toast.makeText(getContext(), "解析配置成功", Toast.LENGTH_SHORT).show();
                    btnCenter.setBackgroundColor(0xff6fcacf);
                    btnCenter.setEnabled(true);
                    btnCenter.setText("连接VPN");
                } else
                    Toast.makeText(getContext(), "解析配置失败", Toast.LENGTH_SHORT).show();

            }
        };
    }


    /**
     * 解析配置文件核心方法
     *
     * @param is
     */
    private VpnProfile doImport(InputStream is) throws IOException, ConfigParser.ConfigParseError {
        ConfigParser cp = new ConfigParser();
        InputStreamReader isr = new InputStreamReader(is);
        cp.parseConfig(isr);
        return cp.convertProfile();
    }

    /**
     * 启动 vpn
     *
     * @param profile
     */
    private void startOrStopVPN(VpnProfile profile) {
        if (VpnStatus.isVPNActive() && profile.getUUIDString().equals(VpnStatus.getLastConnectedVPNProfile())) {
            Toast.makeText(getContext(), "已经是连接状态", Toast.LENGTH_SHORT).show();
//            Intent disconnectVPN = new Intent(getActivity(), DisconnectVPN.class);
//            startActivity(disconnectVPN);
        } else {
            //设置账户和密码
            profile.mUsername = "vnser";
            profile.mPassword = "19980808";

            startVPN(profile);
        }
    }

    private void startVPN(VpnProfile profile) {

        Intent intent = new Intent(getActivity(), LaunchVPN.class);
        intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
        ProfileUtil.addProfile(profile.getUUID().toString(), profile);
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }

}
