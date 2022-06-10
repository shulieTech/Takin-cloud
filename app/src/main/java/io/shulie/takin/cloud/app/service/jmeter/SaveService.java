package io.shulie.takin.cloud.app.service.jmeter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import io.shulie.takin.cloud.app.classloader.JmeterLibClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * ClassName:    SaveService
 * Package:    io.shulie.takin.cloud.app.service.jmeter
 * Description:
 * Datetime:    2022/6/6   11:18
 * Author:   chenhongqiao@shulie.com
 */
@Slf4j
public class SaveService {
    private static final XStream JMXSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
    private static final XStream JTLSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
    private static String fileEncoding = "UTF-8"; // read from properties file
    private static String fileVersion = ""; // computed from saveservice.properties file
    private static String propertiesVersion = "";// read from properties file; written to JMX files

    // Holds the mappings from the saveservice properties file
    // Key: alias Entry: full class name
    // There may be multiple aliases which map to the same class
    private static final Properties aliasToClass = new Properties();

    // Holds the reverse mappings
    // Key: full class name Entry: primary alias
    private static final Properties classToAlias = new Properties();

    static {
        setupXStreamSecurityPolicy(JMXSAVER);
    }

    /**
     * Setup default security policy
     *
     * @param xstream {@link XStream}
     */
    private static void setupXStreamSecurityPolicy(XStream xstream) {
        // This will lift the insecure warning
        xstream.addPermission(NoTypePermission.NONE);
        // We reapply very permissive policy
        // See https://groups.google.com/forum/#!topic/xstream-user/wiKfdJPL8aY
        // TODO : How much are we concerned by CVE-2013-7285
        xstream.addPermission(AnyTypePermission.ANY);
    }

    public static void initProps() {
        // Load the alias properties
        try {
            Properties nameMap = loadProperties();
            try {
                fileVersion = checksum(nameMap);
            } catch (NoSuchAlgorithmException e) {
                log.error("Can't compute checksum for saveservice properties file", e);
                throw new RuntimeException("JMeter requires the checksum of saveservice properties file to continue", e);
            }
            // now create the aliases
            for (Map.Entry<Object, Object> me : nameMap.entrySet()) {
                String key = (String) me.getKey();
                String val = (String) me.getValue();
                if (!key.startsWith("_")) {
                    makeAlias(key, val);
                } else {
                    // process special keys
                    if (key.equalsIgnoreCase("_version")) {
                        propertiesVersion = val;
                        log.info("Using SaveService properties version {}", propertiesVersion);
                    } else if (key.equalsIgnoreCase("_file_version")) {
                        log.info("SaveService properties file version is now computed by a checksum,"
                                + "the property _file_version is not used anymore and can be removed.");
                    } else if (key.equalsIgnoreCase("_file_encoding")) {
                        fileEncoding = val;
                        log.info("Using SaveService properties file encoding {}", fileEncoding);
                    } else {
                        key = key.substring(1);// Remove the leading "_"
                        registerConverter(key, val);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Bad saveservice properties file", e);
            throw new RuntimeException("JMeter requires the saveservice properties file to continue");
        }
    }

    private static void registerConverter(String key, String val) {
        try {
            final String trimmedValue = val.trim();
            boolean useMapper = "collection".equals(trimmedValue) || "mapping".equals(trimmedValue); // $NON-NLS-1$ $NON-NLS-2$
            registerConverter(key, JMXSAVER, useMapper);
            registerConverter(key, JTLSAVER, useMapper);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IllegalArgumentException |
                SecurityException | InvocationTargetException | NoSuchMethodException e1) {
            log.warn("Can't register a converter: {}", key, e1);
        }
    }

    /**
     * Register converter.
     *
     * @param key
     * @param jmxsaver
     * @param useMapper
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    private static void registerConverter(String key, XStream jmxsaver, boolean useMapper)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException {
        if (useMapper) {
            jmxsaver.registerConverter((Converter) Class.forName(key).getConstructor(Mapper.class).newInstance(jmxsaver.getMapper()));
        } else {
            jmxsaver.registerConverter((Converter) Class.forName(key).getDeclaredConstructor().newInstance());
        }
    }

    public static Properties loadProperties() throws IOException {
        Properties nameMap = new Properties();
        File saveServiceFile = getSaveServiceFile();
        if (saveServiceFile.canRead()) {
            try (FileInputStream fis = new FileInputStream(saveServiceFile)) {
                nameMap.load(fis);
            }
        }
        return nameMap;
    }

    private static String checksum(Properties nameMap) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        // This checksums the actual entries, and it ignores comments and blank lines
        nameMap.entrySet().stream().sorted(
                Comparator.comparing((Map.Entry<Object, Object> e) -> e.getKey().toString())
                        .thenComparing(e -> e.getValue().toString())
        ).forEachOrdered(e -> {
            md.update(e.getKey().toString().getBytes(StandardCharsets.UTF_8));
            md.update(e.getValue().toString().getBytes(StandardCharsets.UTF_8));
        });
        return JOrphanUtils.baToHexString(md.digest());
    }

    private static File getSaveServiceFile() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] files = resourcePatternResolver.getResources("classpath:jmeter/saveservice.properties");
        return files[0].getFile();
    }

    // Helper method to simplify alias creation from properties
    private static void makeAlias(String aliasList, String clazz) {
        String[] aliases = aliasList.split(","); // Can have multiple aliases for same target classname
        String alias = aliases[0];
        for (String a : aliases) {
            Object old = aliasToClass.setProperty(a, clazz);
            if (old != null) {
                log.error("Duplicate class detected for {}: {} & {}", alias, clazz, old);
            }
        }
        Object oldval = classToAlias.setProperty(clazz, alias);
        if (oldval != null) {
            log.error("Duplicate alias detected for {}: {} & {}", clazz, alias, oldval);
        }
    }

    /**
     * Load a Test tree (JMX file)
     *
     * @param file the JMX file
     * @return the loaded tree
     * @throws IOException if there is a problem reading the file or processing it
     */
    public static HashTree loadTree(File file) {
        log.info("Loading file: {}", file);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bufferedInputStream =
                new BufferedInputStream(inputStream);
        return readTree(bufferedInputStream, file);
    }

    /**
     * @param inputStream {@link InputStream}
     * @param file        the JMX file used only for debug, can be null
     * @return the loaded tree
     * @throws IOException if there is a problem reading the file or processing it
     */
    private static HashTree readTree(InputStream inputStream, File file) {
        ScriptWrapper wrapper = new ScriptWrapper();
        try {
            // Get the InputReader to use
            InputStreamReader inputStreamReader = getInputStreamReader(inputStream);
            Object obj = JMXSAVER.fromXML(inputStreamReader);
            Field testPlan = obj.getClass().getDeclaredField("testPlan");
            testPlan.setAccessible(true);
            wrapper.testPlan = (HashTree) testPlan.get(obj);
            inputStreamReader.close();
            if (wrapper == null) {
                log.error("Problem loading XML: see above.");
                return null;
            }
            return wrapper.testPlan;
        } catch (CannotResolveClassException | ConversionException | NoClassDefFoundError | IOException e) {
            if (file != null) {
                throw new IllegalArgumentException("Problem loading XML from:'" + file.getAbsolutePath() + "'. \nCause:\n" +
                        ExceptionUtils.getRootCauseMessage(e) + "\n\n Detail:" + e, e);
            } else {
                throw new IllegalArgumentException("Problem loading XML. \nCause:\n" +
                        ExceptionUtils.getRootCauseMessage(e) + "\n\n Detail:" + e, e);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return wrapper.testPlan;

    }

    private static InputStreamReader getInputStreamReader(InputStream inStream) {
        // Check if we have a encoding to use from properties
        Charset charset = getFileEncodingCharset();
        return new InputStreamReader(inStream, charset);
    }

    private static OutputStreamWriter getOutputStreamWriter(OutputStream outStream) {
        // Check if we have a encoding to use from properties
        Charset charset = getFileEncodingCharset();
        return new OutputStreamWriter(outStream, charset);
    }

    /**
     * Returns the file Encoding specified in saveservice.properties or the default
     *
     * @param dflt value to return if file encoding was not provided
     * @return file encoding or default
     */
    // Used by ResultCollector when creating output files
    public static String getFileEncoding(String dflt) {
        if (fileEncoding != null && fileEncoding.length() > 0) {
            return fileEncoding;
        } else {
            return dflt;
        }
    }

    // @NotNull
    private static Charset getFileEncodingCharset() {
        // Check if we have a encoding to use from properties
        if (fileEncoding != null && fileEncoding.length() > 0) {
            return Charset.forName(fileEncoding);
        } else {

            // We use the default character set encoding of the JRE
            log.info("fileEncoding not defined - using JRE default");
            return Charset.defaultCharset();
        }
    }

    // For converters to use
    public static String aliasToClass(String s) {
        String r = aliasToClass.getProperty(s);
        return r == null ? s : r;
    }

    // For converters to use
    public static String classToAlias(String s) {
        String r = classToAlias.getProperty(s);
        return r == null ? s : r;
    }

    private static class XStreamWrapper extends XStream {
        private XStreamWrapper(ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        // Override wrapMapper in order to insert the Wrapper in the chain
        @Override
        protected MapperWrapper wrapMapper(MapperWrapper next) {
            // Provide our own aliasing using strings rather than classes
            return new MapperWrapper(next) {
                // Translate alias to classname and then delegate to wrapped class
                @Override
                public Class<?> realClass(String alias) {
                    String fullName = aliasToClass(alias);
//                    if (fullName != null) {
//                        fullName = NameUpdater.getCurrentName(fullName);
//                    }
                    try {
                        return Class.forName(fullName, false, JmeterLibClassLoader.getInstance());
//                        return Class.forName(fullName, false, this.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
//                    return super.realClass(fullName == null ? alias : fullName);
                }

                // Translate to alias and then delegate to wrapped class
                @Override
                public String serializedClass(@SuppressWarnings("rawtypes") // superclass does not use types
                                                      Class type) {
                    if (type == null) {
                        return super.serializedClass(null); // was type, but that caused FindBugs warning
                    }
                    String alias = classToAlias(type.getName());
                    return alias == null ? super.serializedClass(type) : alias;
                }
            };
        }
    }

}

