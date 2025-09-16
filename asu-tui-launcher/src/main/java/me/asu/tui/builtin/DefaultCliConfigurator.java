/*
 * #%L
 * clamshell-commons
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
package me.asu.tui.builtin;

import me.asu.tui.ClassPathUtils;
import me.asu.tui.PlaceholderUtils;
import me.asu.tui.api.CliConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;

/**
 * This is a default implementation of the Configurator.
 * It provides configuration information about the shell.
 *
 * @author suk
 */
public class DefaultCliConfigurator implements CliConfigurator {
    private static final String CONFIG_FILE_PATH = VALUE_CONFIG_FILE;
    private static String SUFFIX = ".jar";
    private File configFile;
    private Properties config = new Properties();

    private DefaultCliConfigurator(String configFileName) {
        configFile = createConfigFile(configFileName);
        initialize();
    }

    public static DefaultCliConfigurator createNewInstance(String configFileName) {
        return new DefaultCliConfigurator(configFileName);
    }

    public static DefaultCliConfigurator createNewInstance() {
        return new DefaultCliConfigurator(null);
    }

    private File createConfigFile(String fileName) {
        String configFilePath = findConfigFilePath();
        return (fileName != null) ? new File(fileName) : new File(configFilePath);
    }

    private void initialize() {
        if (configFile != null && configFile.exists() && configFile.isFile()) {
            try {
                config.load(new FileInputStream(configFile));
                Map m = new HashMap(System.getenv());
                m.putAll(System.getProperties());
                Set<String> names = config.stringPropertyNames();
                for (String name : names) {
                    String value = config.getProperty(name);
                    if (value.matches(".*\\$\\{.+\\}.*")) {
                        value = PlaceholderUtils.resolvePlaceholders(value, m);
                        config.put(name, value);
                    }
                    m.put(name, value);
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private String findConfigFilePath() {
        String cf = System.getProperty(KEY_CONFIG_FILE);
        if (cf != null) {
            return PlaceholderUtils.resolvePlaceholders(cf, System.getProperties());
        }

        String appHome = getAppHomeDir();
        if (appHome != null) {
            File file = new File(appHome, CONFIG_FILE_PATH);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }

        File file = new File(CliConfigurator.VALUE_USER_DIR, CONFIG_FILE_PATH);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        file = new File(CliConfigurator.VALUE_USER_HOME, CONFIG_FILE_PATH);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        URL resource = getClass().getClassLoader().getResource(CONFIG_FILE_PATH);
        if (resource != null && resource.getProtocol().startsWith("file")) {
            return resource.getFile();
        }
        file = new File(CONFIG_FILE_PATH);
        return file.getAbsolutePath();

    }

    @Override
    public String getAppHomeDir() {
        Path path = ClassPathUtils.findRootPathForClass(CliConfigurator.class);
        if (path == null) {
            return null;
        } else {
            Matcher matcher = JAR_FILE_PATTERN.matcher(path.toString());
            if (matcher.matches()) {
                return path.getParent().toString();
            } else {
                return path.toString();
            }
        }
    }

    @Override
    public Properties getConfig() {
        return config;
    }

    @Override
    public String getSystemProperties(String key) {
        return System.getProperty(key);
    }

    @Override
    public String getSystemProperties(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    @Override
    public String getProperty(String key) {
        return config.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    @Override
    public Enumeration<?> propertyNames() {
        return config.propertyNames();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return config.stringPropertyNames();
    }

    @Override
    public Object get(Object key) {
        return config.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return config.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return config.remove(key);
    }

    @Override
    public void putAll(Map<?, ?> t) {
        config.putAll(t);
    }

    @Override
    public Set<Object> keySet() {
        return config.keySet();
    }

    public File getConfigFile() {
        return configFile;
    }
}