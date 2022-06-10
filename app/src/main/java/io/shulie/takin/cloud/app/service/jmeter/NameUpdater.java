//package io.shulie.takin.cloud.app.service.jmeter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//
//import java.io.*;
//import java.net.URL;
//import java.util.Enumeration;
//import java.util.Properties;
//
///**
// * ClassName:    NameUpdater
// * Package:    io.shulie.takin.cloud.app.service.jmeter
// * Description:
// * Datetime:    2022/6/6   13:46
// * Author:   chenhongqiao@shulie.com
// */
//public class NameUpdater {
//    private static final Properties nameMap;
//    // Read-only access after class has been initialised
//
//    private static final Logger log = LoggerFactory.getLogger(NameUpdater.class);
//
//    private static final String NAME_UPDATER_PROPERTIES =
//            "META-INF/resources/org.apache.jmeter.nameupdater.properties";
//
//    static {
//        nameMap = new Properties();
//        FileInputStream fis = null;
//        File f = null;
//        try {
//            f = getUpgradeFile();
//            fis = new FileInputStream(f);
//            nameMap.load(fis);
//        } catch (FileNotFoundException e) {
//            log.error("Could not find upgrade file.", e);
//        } catch (IOException e) {
//            log.error("Error processing upgrade file: {}", f, e);
//        } finally {
//            JOrphanUtils.closeQuietly(fis);
//        }
//
//        //load additional name conversion rules from plugins
//        Enumeration<URL> enu = null;
//
//        try {
//            enu = NameUpdater.class.getClassLoader().getResources(NAME_UPDATER_PROPERTIES);
//        } catch (IOException e) {
//            log.error("Error in finding additional nameupdater.properties files.", e);
//        }
//
//        if(enu != null) {
//            while(enu.hasMoreElements()) {
//                URL ressourceUrl = enu.nextElement();
//                log.info("Processing {}", ressourceUrl);
//                Properties prop = new Properties();
//                InputStream is = null;
//                try {
//                    is = ressourceUrl.openStream();
//                    prop.load(is);
//                } catch (IOException e) {
//                    log.error("Error processing upgrade file: {}", ressourceUrl.getPath(), e);
//                } finally {
//                    JOrphanUtils.closeQuietly(is);
//                }
//
//                @SuppressWarnings("unchecked") // names are Strings
//                Enumeration<String> propertyNames = (Enumeration<String>) prop.propertyNames();
//                while (propertyNames.hasMoreElements()) {
//                    String key = propertyNames.nextElement();
//                    if (!nameMap.containsKey(key)) {
//                        nameMap.put(key, prop.get(key));
//                        log.info("Added additional nameMap entry: {}", key);
//                    } else {
//                        log.warn("Additional nameMap entry: '{}' rejected as already defined.", key);
//                    }
//                }
//            }
//        }
//    }
//
//    private static File getUpgradeFile() throws IOException {
//        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        Resource[] files = resourcePatternResolver.getResources("classpath:jmeter/upgrade.properties");
//        return files[0].getFile();
//    }
//
//    /**
//     * Looks up the class name; if that does not exist in the map,
//     * then defaults to the input name.
//     *
//     * @param className the classname from the script file
//     * @return the class name to use, possibly updated.
//     */
//    public static String getCurrentName(String className) {
//        if (nameMap.containsKey(className)) {
//            String newName = nameMap.getProperty(className);
//            log.info("Upgrading class {} to {}", className, newName);
//            return newName;
//        }
//        return className;
//    }
//}
