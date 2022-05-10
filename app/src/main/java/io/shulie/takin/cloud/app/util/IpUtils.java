package io.shulie.takin.cloud.app.util;

import java.net.InetAddress;
import java.util.Enumeration;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.text.CharSequenceUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址转换工具类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月21日
 */
@Slf4j
public class IpUtils {

    private IpUtils() {}

    /**
     * 获取用户的IP地址
     *
     * @param httpServletRequest http请求
     * @return 用户的ip地址
     * @author shulie
     * @date 2018年5月21日
     */
    public static String getIp(HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getHeader("Proxy-Client-IP");
        //这里多个判断条件是相同的，不知道用意何为？？？？
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("HTTP_CLIENT_IP");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("X-Forwarded-For");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getHeader("X-Forwarded-Host");
        }
        if (checkIp(ip)) {
            ip = httpServletRequest.getRemoteAddr();
        }
        if (CharSequenceUtil.isBlank(ip) && (ip.length() >= (((1 + 1) << (1 + 1 + 1)) - 1))) {
            ip = CharSequenceUtil.subBefore(ip, ",", false);
        }
        return ip;
    }

    private static boolean checkIp(String ip) {
        String unknownStr = "unknown";
        return !CharSequenceUtil.isBlank(ip) && !unknownStr.equalsIgnoreCase(ip);
    }

    /**
     * 将255.255.255.255 形式的IP地址转换成long型，
     * 传入的IP格式为"100.010.000.111"或者"1.10.0.111"，
     * 不能包含字母等
     *
     * @param ipString 字符串ip地址
     * @return long型的ip地址
     * @author shulie
     * @date 2018年5月21日
     */
    public static long ipToLong(String ipString) {
        if (ipString == null || "".equals(ipString)) {
            return 0;
        }
        long[] ip = new long[4];
        int position1 = ipString.indexOf(".");
        int position2 = ipString.indexOf(".", position1 + 1);
        int position3 = ipString.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(ipString.substring(0, position1));
        ip[1] = Long.parseLong(ipString.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(ipString.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(ipString.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    /**
     * 说明：判断是否为windows操作系统
     *
     * @return -
     * @author shulie
     * @date 2017年10月5日 下午2:46:05
     */
    public static boolean isWindowOs() {
        return CharSequenceUtil.containsIgnoreCase(System.getProperty("os.name"), "windows");
    }

    /**
     * 说明：获取linux服务器的ip地址
     *
     * @return -
     * @author shulie
     * @date 2017年10月5日 下午2:46:42
     */
    public static InetAddress getInetAddress() {
        try {
            if (isWindowOs()) {
                return InetAddress.getLocalHost();
            }

            // 定义一个内容都是NetworkInterface的枚举对象
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            // 如果枚举对象里面还有内容(NetworkInterface)
            while (netInterfaces.hasMoreElements()) {
                // 获取下一个内容(NetworkInterface)
                NetworkInterface ni = netInterfaces.nextElement();
                // 遍历所有IP
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    // 属于本地地址
                    if (inetAddress.isSiteLocalAddress()
                        // 不是回环地址
                        && !inetAddress.isLoopbackAddress()
                        // 地址里面没有:号
                        && !inetAddress.getHostAddress().contains(":")) {
                        return inetAddress;
                    }
                }
            }
        } catch (UnknownHostException e) {
            log.error("未知的ip端口，获取远程ip地址失败.\n", e);
        } catch (SocketException e) {
            log.error("socket异常，获取远程ip地址失败.\n", e);
        }
        return null;
    }
}
