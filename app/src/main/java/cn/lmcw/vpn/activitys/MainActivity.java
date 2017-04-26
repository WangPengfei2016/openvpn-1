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
import cn.lmcw.vpn.fragment.YunFragment;
import cn.lmcw.vpn.openvpn.core.ConnectionStatus;
import cn.lmcw.vpn.openvpn.core.OpenVPNManagement;
import cn.lmcw.vpn.openvpn.core.VpnStatus;
import cn.lmcw.vpn.utils.Util;

public class MainActivity extends BaseActivity implements VpnStatus.StateListener, VpnStatus.ByteCountListener {


    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;


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

        adapter.addFragment(YunFragment.newInstance("新闻"));
        adapter.addFragment(YunFragment.newInstance("图书"));
        adapter.addFragment(YunFragment.newInstance("发现"));


        viewPager.setAdapter(adapter);
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        Log.i("ttttttttttt", "state:" + state + " logmessage:" + logmessage + " localizedResId:" + getString(localizedResId) + " level:" + level);
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
}
