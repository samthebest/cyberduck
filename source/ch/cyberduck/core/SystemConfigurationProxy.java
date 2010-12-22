package ch.cyberduck.core;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @version $Id$
 */
public class SystemConfigurationProxy extends AbstractProxy implements Proxy {
    private static Logger log = Logger.getLogger(SystemConfigurationProxy.class);

    public static void register() {
        ProxyFactory.addFactory(Factory.NATIVE_PLATFORM, new Factory());
    }

    private static class Factory extends ProxyFactory {
        @Override
        protected Proxy create() {
            return new SystemConfigurationProxy();
        }
    }

    private SystemConfigurationProxy() {
        ;
    }

    private static boolean JNI_LOADED = false;

    /**
     * Load native library extensions
     *
     * @return
     */
    private static boolean loadNative() {
        if(!JNI_LOADED) {
            JNI_LOADED = Native.load("Proxy");
        }
        return JNI_LOADED;
    }

    /**
     * Use passive connect mode
     *
     * @return True if enabled in the system preferences
     */
    public native boolean usePassiveFTPNative();

    public boolean usePassiveFTP() {
        if(!loadNative()) {
            return false;
        }
        return this.usePassiveFTPNative();
    }

    /**
     *
     * @param hostname
     * @return
     */
    private boolean isHostExcluded(String hostname) {
        if(!loadNative()) {
            return false;
        }
        if(!hostname.contains(".")) {
            // Non fully qualified hostname
            if(this.isSimpleHostnameExcludedNative()) {
                return true;
            }
        }
        for(String exception : this.getProxyExceptionsNative()) {
            if(StringUtils.isBlank(exception)) {
                continue;
            }
            if(this.matches(exception, hostname)) {
                return true;
            }
            try {
                SubnetUtils subnet = new SubnetUtils(exception);
                try {
                    String ip = Inet4Address.getByName(hostname).getHostAddress();
                    if(subnet.getInfo().isInRange(ip)) {
                        return true;
                    }
                }
                catch(UnknownHostException e) {
                    // Should not happen as we resolve addresses before attempting to connect
                    // in ch.cyberduck.core.Resolver
                    log.warn(e.getMessage());
                }
            }
            catch(IllegalArgumentException e) {
                // A hostname pattern but not CIDR. Does not
                // match n.n.n.n/m where n=1-3 decimal digits, m = 1-3 decimal digits in range 1-32
                log.debug("Invalid CIDR notation:" + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Check to see if the hostname is excluded from proxy settings
     *
     * @return Exception patterns
     */
    public native String[] getProxyExceptionsNative();

    public native boolean isSimpleHostnameExcludedNative();

    public boolean isSOCKSProxyEnabled(Host host) {
        if(!loadNative()) {
            return false;
        }
        if(this.isHostExcluded(host.getHostname())) {
            return false;
        }
        return this.isSOCKSProxyEnabledNative();
    }

    public String getSOCKSProxyHost() {
        if(!loadNative()) {
            return null;
        }
        return this.getSOCKSProxyHostNative();
    }

    public int getSOCKSProxyPort() {
        if(!loadNative()) {
            return -1;
        }
        return this.getSOCKSProxyPortNative();
    }

    public boolean isHTTPProxyEnabled(Host host) {
        if(!loadNative()) {
            return false;
        }
        if(this.isHostExcluded(host.getHostname())) {
            return false;
        }
        return this.isHTTPProxyEnabledNative();
    }

    public String getHTTPProxyHost() {
        if(!loadNative()) {
            return null;
        }
        return this.getHTTPProxyHostNative();
    }

    public int getHTTPProxyPort() {
        if(!loadNative()) {
            return -1;
        }
        return getHTTPProxyPortNative();
    }

    public boolean isHTTPSProxyEnabled(Host host) {
        if(!loadNative()) {
            return false;
        }
        if(this.isHostExcluded(host.getHostname())) {
            return false;
        }
        return this.isHTTPSProxyEnabledNative();
    }

    public String getHTTPSProxyHost() {
        if(!loadNative()) {
            return null;
        }
        return this.getHTTPSProxyHostNative();
    }

    public int getHTTPSProxyPort() {
        if(!loadNative()) {
            return -1;
        }
        return this.getHTTPSProxyPortNative();
    }

    /**
     * SOCKS proxy setting enabled
     *
     * @return
     */
    public native boolean isSOCKSProxyEnabledNative();

    /**
     * SOCKS proxy setting hostname
     *
     * @return
     */
    public native String getSOCKSProxyHostNative();

    /**
     * HTTP proxy setting port
     *
     * @return
     */
    public native int getSOCKSProxyPortNative();

    /**
     * HTTP proxy setting enabled
     *
     * @return
     */
    public native boolean isHTTPProxyEnabledNative();

    /**
     * HTTP proxy setting hostname
     *
     * @return
     */
    public native String getHTTPProxyHostNative();

    /**
     * HTTP proxy setting port
     *
     * @return
     */
    public native int getHTTPProxyPortNative();

    /**
     * HTTPS proxy setting enabled
     *
     * @return
     */
    public native boolean isHTTPSProxyEnabledNative();

    /**
     * HTTPS proxy setting hostname
     *
     * @return
     */
    public native String getHTTPSProxyHostNative();

    /**
     * HTTPS proxy setting port
     *
     * @return
     */
    public native int getHTTPSProxyPortNative();
}