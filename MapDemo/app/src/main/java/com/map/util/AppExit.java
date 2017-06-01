package com.map.util;

import java.io.File;

/**
 * Created by xg on 2016/3/5.
 */
public class AppExit {
    /**
     * 用来判断某个app是否存在
     *
     * @param packageName
     * @return
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
