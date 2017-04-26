/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.lmcw.vpn.openvpn.utils.pem;

public interface PemObjectGenerator
{
    PemObject generate()
        throws PemGenerationException;
}
