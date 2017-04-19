package com.example.udptest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by George on 2017-04-19.
 */

/**
 * This class provides information about the client, such as IP-address
 */
public class MobileInfo {


    /**
     * Get IP address from first non-localhost interface
     *
     * @param //ipv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            //Creates a list of interfaces.
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            //For each interface in the list get an IP adress and place into list of InetAdress.
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                //For each  address in the list.
                for (InetAddress addr : addrs) {
                    //isLoopbackAddress() method returns true if the adress is the loopback address, false otherwise.
                    if (!addr.isLoopbackAddress()) {
                        //getHostAddress() returns string representation of IP adress.
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        //If useIPv4 is true and isIPv4 is true, return string representation of IPv4 adress.
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                            //Else return string representation of IPv6 adress.
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        //If adress is a loopback adress return empty string
        return "";
    }

}
