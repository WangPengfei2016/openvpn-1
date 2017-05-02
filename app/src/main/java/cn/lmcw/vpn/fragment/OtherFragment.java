package cn.lmcw.vpn.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lmcw.vpn.R;

/**
 * Created by admin on 2017/4/26.
 */

public class OtherFragment extends Fragment {


    public static OtherFragment newInstance(String info) {

        Bundle args = new Bundle();
        args.putString("info", info);
        OtherFragment fragment = new OtherFragment();
        fragment.setArguments(args);
        return fragment;
    }


    View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.fragment_other,null);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
