/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by admin on 2017/4/22.
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected abstract void initViews();

    protected abstract void initEvents();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        init();
    }


    protected void init() {
        initViews();
        initEvents();
    }

    ;

}
