package cn.lmcw.vpn.openvpn;

import java.util.HashMap;

import cn.lmcw.vpn.utils.Util;

/**
 * Created by dingyong on 2017/4/26.
 */
public class ProfileUtil {

    private static VpnProfile _vpn;

    private static String CONF_PATH = "us_conf.data";


    private static HashMap<String, VpnProfile> CACHE = new HashMap<>();


    public static void addProfile(String uuid, VpnProfile vpnProfile) {
        CACHE.put(uuid, vpnProfile);
    }

    public static VpnProfile getProfile(String uuid) {
        return CACHE.get(uuid);
    }

    public static void clear() {
        CACHE.clear();
    }

    public static synchronized VpnProfile getUseConnectedProfile() {

        if (_vpn == null) {
            _vpn = (VpnProfile) Util.read(Util.getSDPath() + "/" + CONF_PATH);
        }
        return _vpn;
    }

    public static synchronized void setUseConnectedProfile(VpnProfile vp) {
        if (vp != null) {
            _vpn = vp;
            Util.save(Util.getSDPath() + "/" + CONF_PATH, vp);
        }
    }

}
