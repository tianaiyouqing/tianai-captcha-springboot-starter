package cloud.tianai.captcha.spring.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class IPUtils {

    private static final String UNKNOWN = "unknown";

    public static final Pattern INNER_IP_PATTERN = Pattern.compile("(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-9]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})");

    protected IPUtils() {

    }

    /**
     * 获取 IP地址
     * 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()获取 IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static boolean isInnerIp(String ip) {
        if ("127.0.0.1".equals(ip) || "localhost".equals(ip)) {
            return true;
        }
        Matcher matcher = INNER_IP_PATTERN.matcher(ip);
        return matcher.find();
    }

    public static void main(String[] args) {
        System.out.println(isInnerIp("127.0.0.1"));
        System.out.println(isInnerIp("172.17.196.250"));
    }
}
