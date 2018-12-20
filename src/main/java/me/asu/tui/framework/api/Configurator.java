/*
 * #%L
 * clamshell-api
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package me.asu.tui.framework.api;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This interface encapsulates the configuration.
 *
 * @author suk
 */
public interface Configurator {

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
