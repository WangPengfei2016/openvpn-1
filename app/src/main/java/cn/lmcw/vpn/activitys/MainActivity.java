/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import cn.lmcw.vpn.R;
import cn.lmcw.vpn.adapter.ViewPagerAdapter;
import cn.lmcw.vpn.base.BaseActivity;
import cn.lmcw.vpn.fragment.OtherFragment;
import cn.lmcw.vpn.fragment.YunFragment;
import cn.lmcw.vpn.openvpn.core.ConnectionStatus;
import cn.lmcw.vpn.openvpn.core.OpenVPNManagement;
import cn.lmcw.vpn.openvpn.core.VpnStatus;
import cn.lmcw.vpn.utils.Util;

import static cn.lmcw.vpn.openvpn.core.ConnectionStatus.LEVEL_AUTH_FAILED;
import static cn.lmcw.vpn.openvpn.core.ConnectionStatus.LEVEL_CONNECTED;
import static cn.lmcw.vpn.openvpn.core.ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET;
import static cn.lmcw.vpn.openvpn.core.ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED;
import static cn.lmcw.vpn.openvpn.core.ConnectionStatus.LEVEL_NOTCONNECTED;

public class MainActivity extends BaseActivity implements VpnStatus.StateListener, VpnStatus.ByteCountListener {


    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private YunFragment yunFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    protected void initViews() {

        viewPager = (ViewPager) findViewById(R.id.vp);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn);
    }

    @Override
    protected void initEvents() {

        VpnStatus.addStateListener(this);
        VpnStatus.addByteCountListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.item_news:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_lib:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_find:
                                viewPager.setCurrentItem(2);
                                break;

                        }
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(yunFragment = YunFragment.newInstance("新闻"));
        adapter.addFragment(OtherFragment.newInstance("图书"));
        adapter.addFragment(OtherFragment.newInstance("发现"));


        viewPager.setAdapter(adapter);
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        Log.i("ttttttttttt", "state:" + state + " logmessage:" + logmessage + " localizedResId:" + getString(localizedResId) + " level:" + level);
        if (localizedResId == R.string.state_noprocess && level == LEVEL_NOTCONNECTED) {
            //未运行 初始化状态和断开状态
            setText("连接VPN");

        } else if (localizedResId == R.string.state_resolve && level == LEVEL_CONNECTING_NO_SERVER_REPLY_YET) {
            //正在解析主机名
            setText("正在解析主机名");
        } else if (localizedResId == R.string.state_tcp_connect && level == LEVEL_CONNECTING_NO_SERVER_REPLY_YET) {
            //连接中(TCP)
            setText("连接中(TCP)");
        } else if (localizedResId == R.string.state_wait && level == LEVEL_CONNECTING_NO_SERVER_REPLY_YET) {
            //等待服务器响应
            setText("等待服务器响应");
        } else if (localizedResId == R.string.state_auth && level == LEVEL_CONNECTING_SERVER_REPLIED) {
            //验证中
        } else if (localizedResId == R.string.state_get_config && level == LEVEL_CONNECTING_SERVER_REPLIED) {
            //正在获取客户端配置
        } else if (localizedResId == R.string.state_assign_ip && level == LEVEL_CONNECTING_SERVER_REPLIED) {

        } else if (localizedResId == R.string.state_connected && level == LEVEL_CONNECTED) {
            //连接VPN成功
            setText("连接VPN成功");
            setStatus(true);

        } else if (localizedResId == R.string.state_auth_failed && level == LEVEL_AUTH_FAILED) {
            //账号密码验证失败
            setText("账号密码验证失败");
        }
        //按钮可点击状态

        setStatus(level == LEVEL_NOTCONNECTED || level == LEVEL_CONNECTED);
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }

    @Override
    public void updateByteCount(long in, long out, long diffIn, long diffOut) {
        String down = String.format("%2$s/s %1$s", Util.humanReadableByteCount(in, false), Util.humanReadableByteCount(diffIn / OpenVPNManagement.mBytecountInterval, true));
        String up = String.format("%2$s/s %1$s", Util.humanReadableByteCount(out, false), Util.humanReadableByteCount(diffOut / OpenVPNManagement.mBytecountInterval, true));
        Log.i("ttttttttttt", down + " " + up);
    }


    private void setText(String str) {
        if (yunFragment == null) {
            return;
        }
        yunFragment.setText(str);
    }

    private void setStatus(boolean b) {
        if (yunFragment == null) {
            return;
        }
        yunFragment.setStatus(b);
    }


}
