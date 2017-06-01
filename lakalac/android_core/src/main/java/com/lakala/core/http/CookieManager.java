package com.lakala.core.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 这是一个简单的内存级别的 cookie，当应用结束后 cookie 将被清空。
 * 不支持过期时间的设置。
 * 不支持多级cookie 合并读取，如果存在多级 cookie 则只能读取最顶级cookie。
 * Created by Michael on 15-1-9.
 */
public class CookieManager {
    private Map<String,List<String>>       hostMap = new HashMap<String, List<String>>();
    private Map<String,Map<String,String>> cookies = new HashMap<String, Map<String,String>>();

    private static CookieManager instance;

    public static synchronized CookieManager getInstance(){
        if (instance == null){
            instance = new CookieManager();
        }
        return instance;
    }

    protected CookieManager(){}

    /**
     * 设置 cookie
     * @param urlstr  当前请求的 URL
     * @param value   Reponse 头中的 Set-Cookie 的值
     */
    public void setCookie(String urlstr ,String value){
        //获取主机地址及端口字符串。
        String host = getHostAndPort(urlstr);

        if (host.isEmpty()){
            return;
        }

        //解析 Cookie 值。
        Map<String,String> kv = handleValue(value);

        if (kv.isEmpty()){
            return;
        }

        //获取 path 信息，path 用于区分同一站点下不同路径下的 cookie。
        String path = null;
        path = kv.remove("Path");
        if (path == null){
            path = kv.remove("path");
        }

        //如查没有设置path，则表示Cookie 全站点有效。
        if (path == null){
            path = "/";
        }

        //获取当前站点下所有 cookie 路径信息，如果当前cookie 路么没有保存，则保存这个路径到列表。
        //将 Host 与 Path 合并生成 cookie 访问 Cookie Map。
        synchronized (hostMap){
            List<String> cookiePaths = hostMap.get(host);
            if (cookiePaths == null){
                cookiePaths = new ArrayList<>();
                hostMap.put(host,cookiePaths);
            }

            if (!cookiePaths.contains(path)){
                cookiePaths.add(path);
            }

            //生成 cookie 访问 Key
            String key = host.concat(path);
            //获取已保存的cookie，如果没有则创建一个新的。
            Map<String,String> cookie = cookies.get(key);
            if (cookie == null){
                cookie = new HashMap<>();
                cookies.put(key,cookie);
            }

            cookie.putAll(kv);
        }
    }

    /**
     * 获取 cookie，如果不存在则返加一个空 map。
     * @param urlstr 当前请求的 URL
     * @return cookie 值
     */
    public Map<String,String> getCookie(String urlstr){
        Map<String,String> cookie = new HashMap<>();

        //获取主机地址及端口字符串。
        String host = getHostAndPort(urlstr);

        if (host.isEmpty()){
            return cookie;
        }

        synchronized (hostMap) {
            List<String> cookiePaths = hostMap.get(host);

            if (cookiePaths == null || cookiePaths.isEmpty()){
                return cookie;
            }

            ArrayList<String> matchedPaths = new ArrayList<>();

            for (String path :cookiePaths){
                int index = urlstr.indexOf(path);

                //找到与当前 URL 匹配的路径，将配配到的路径按从父到子顺序排列。
                if (index >=0){
                    matchedPaths.add(path);
                }
            }

            //排序 path
            Collections.sort(matchedPaths, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.length() - o2.length();
                }
            });

            //将所有匹配的 Path 的 cookie 合成一个 cookie
            for (String path :matchedPaths){
                String key = host.concat(path);

                Map<String,String> c = cookies.get(key);
                if (c != null && !c.isEmpty()){
                    cookie.putAll(c);
                }
            }
        }

        return cookie;
    }

    /**
     * 获取 cookie，如果不存在则返加 null。
     * @param urlstr 当前请求的 URL
     * @return cookie 字符串
     */
    public String getCookieString(String urlstr){
        Map<String,String> cookie = getCookie(urlstr);

        if (cookie.isEmpty()){
            return null;
        }

        Set<Map.Entry<String,String>> set = cookie.entrySet();
        StringBuffer string = new StringBuffer();

        for (Map.Entry<String,String> entry:set){
            string.append(entry.getKey());
            string.append("=");
            string.append(entry.getValue());
            string.append(";");
        }

        return string.toString();
    }

    public void clear(){
        synchronized (hostMap){
            hostMap.clear();
            cookies.clear();
        }
    }

    /**
     * 根据 URL 解析出主机地址及端口，返回的主机名格式为 host:port
     * @param url URL 字符串
     * @return
     */
    private String getHostAndPort(String url){
        String host = "";
        String port = "";
        try {
            URI uri = new URI(url);
            host = uri.getHost();
            port = String.valueOf(uri.getPort());
        } catch (URISyntaxException e) {}

        return String.format("%s:%s",host,port);
    }

    /**
     * 解析 cookie值，首先按 “;”拆分，然后在按“=”拆分。
     * @param cookie  setCookie 头中的值。
     * @return 拆分开后的 cookie 的值。
     */
    private Map<String,String> handleValue(String cookie){
        Map<String,String> kvmap = new HashMap<>();

        if (cookie == null && cookie.length() == 0){
            return kvmap;
        }

        String[] list = cookie.split(";");
        for (String kv : list){
            if (kv == null || kv.length() == 0){
                continue;
            }

            String[] value = kv.split("=");

            if (value == null || value.length < 2){
                continue;
            }

            if (value[0] == null || value[0].length() == 0){
                continue;
            }

            kvmap.put(value[0],value[1]);
        }

        return kvmap;
    }
}
