package me.asu.tui.framework.api;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This interface encapsulates the configuration.
 *
 * @author suk
 */
public interface CliConfigurator
{

    /**
     * property name for property file.
     */
    String KEY_CONFIG_FILE          = "cli.config.file";
    /**
     * 命令插件库
     */
    String VALUE_CONFIG_PLUGINS_DIR = "plugins";
    /**
     * 自定义依赖库
     */
    String VALUE_CONFIG_LIBDIR  = "lib";
    /**
     * 配置文件
     */
    String VALUE_CONFIG_FILE    = "cli.properties";
    String VALUE_LINE_SEP       = System.getProperty("line.separator");
    String VALUE_USER_HOME      = System.getProperty("user.home");
    String VALUE_USER_DIR       = System.getProperty("user.dir");
    String VALUE_PATH_SEPARATOR = System.getProperty("path.separator");

    Pattern JAR_FILE_PATTERN = Pattern.compile(".*\\.jar");

    String getAppHomeDir();

    /**
     * Returns the raw config map from cli.properties.
     *
     * @return Properties
     */
    Properties getConfig();

    String getSystemProperties(String key);

    String getSystemProperties(String key, String defaultValue);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    Enumeration<?> propertyNames();

    Set<String> stringPropertyNames();

    Object get(Object key);

    Object put(Object key, Object value);

    Object remove(Object key);

    void putAll(Map<?, ?> t);

    Set<Object> keySet();

}
