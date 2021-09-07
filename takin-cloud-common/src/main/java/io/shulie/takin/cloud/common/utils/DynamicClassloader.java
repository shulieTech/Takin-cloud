package io.shulie.takin.cloud.common.utils;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author shulie
 * @date 2019-06-26 15:32
 */
public class DynamicClassloader extends URLClassLoader {

    public DynamicClassloader() {
        this(new URL[] {});
    }

    public DynamicClassloader(URL[] urls) {
        super(urls);
    }

    public DynamicClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

}
