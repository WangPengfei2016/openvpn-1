/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

    private VpnProfile mResult;
    private transient List<String> mPathsegments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yun, null);
        btnParse = (Button) view.findViewById(R.id.btn_parse);

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputStream in = new FileInputStream(Util.getSDPath() + "/news.ovpn");
                    doImport(in);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

        btnOpen = (Button) view.findViewById(R.id.open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mResult == null) {
                    Toast.makeText(getContext(), "vpnProfle is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                startOrStopVPN(mResult);

            }
        });


        btnClose = (Button) view.findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VpnStatus.isVPNActive() && mResult.getUUIDString().equals(VpnStatus.getLastConnectedVPNProfile())) {

                    Intent disconnectVPN = new Intent(getActivity(), DisconnectVPN.class);
                    startActivity(disconnectVPN);
                }

            }
        });

        return view;
    }


    /**
     * 解析配置文件核心方法
     *
     * @param is
     */
    private void doImport(InputStream is) {
        ConfigParser cp = new ConfigParser();
        try {
            InputStreamReader isr = new InputStreamReader(is);

            cp.parseConfig(isr);
            mResult = cp.convertProfile();

            Log.i("aaaaaaaa", "解析成功" + cp.convertProfile().getUUIDString());
            Toast.makeText(getActivity(), "解析文件成功", Toast.LENGTH_SHORT).show();
            return;

        } catch (IOException | ConfigParser.ConfigParseError e) {
            Log.i("aaaaaaaa", "解析异常" + e.getLocalizedMessage());
            Toast.makeText(getContext(), "解析异常" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        mResult = null;

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
