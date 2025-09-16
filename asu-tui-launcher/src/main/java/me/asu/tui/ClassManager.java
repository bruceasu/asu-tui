package me.asu.tui;

import me.asu.tui.api.CliConfigurator;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Shell ClassManager utility Classes/Methods.
 */
public class ClassManager {

    /**
     * Creates classloader from directories.  The specified directory must contain class files
     * that will be searched by the ClassLoader
     *
     * @param dirs   directories to be used for class loading
     * @param parent parent directory
     * @return ClassLoader
     */
    public static ClassLoader getClassLoaderFromDirs(File[] dirs, ClassLoader parent)
            throws Exception {
        File[] fileDirs = correctPaths(dirs);
        List<URL> classpath = new ArrayList<URL>();
        for (int i = 0; i < fileDirs.length; i++) {
            File f = fileDirs[i];
            if (f.isDirectory()) {
                classpath.add(fileDirs[i].toURI().toURL());
            }
        }
        return new URLClassLoader(classpath.toArray(new URL[classpath.size()]), parent);
    }

    /**
     * Adds the trailing slash in the path name.
     */
    private static File[] correctPaths(File[] paths) throws Exception {
        File[] correctedPaths = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String pathName = paths[i].getName();
            if (!pathName.endsWith(System.getProperty("file.separator"))) {
                String fullPath = paths[i].getCanonicalPath();
                correctedPaths[i] = new File(fullPath + System.getProperty("file.separator"));
            } else {
                correctedPaths[i] = paths[i];
            }
        }
        return correctedPaths;
    }

    /**
     * Creates ClassLoader instance from files searched in provided directories.
     *
     * @param paths  directories to search for files to include in ClassLoader
     * @param parent parent class loader
     * @return ClassLoader
     */
    public static ClassLoader createClassLoaderFromFiles(File[] paths, ClassLoader parent)
            throws Exception {

        return getClassLoaderFromFiles(paths, CliConfigurator.JAR_FILE_PATTERN, parent);
    }

    /**
     * Creates a classloader by searching for specified files in given search directories.
     *
     * @param filePaths   directory or files to add to class loader.  If DIR search content of
     *                    the dir that maches expression.  if FILE and matches expression, add
     *                    to classloader.
     * @param filePattern regex pattern used to match filename.
     * @param parent      parent class loader
     */
    public static ClassLoader getClassLoaderFromFiles(final File[] filePaths,
            final Pattern filePattern,
            final ClassLoader parent) throws Exception {
        List<URL> classpath = new ArrayList<URL>();

        for (int i = 0; i < filePaths.length; i++) {
            File filePath = filePaths[i];
            if (filePath == null || !filePath.exists()) continue;
            filePath = filePath.getCanonicalFile();

            // if file is FILE and matches search, add to classloader
            if (filePath.isFile() && filePattern.matcher(filePath.getName()).matches()) {
                classpath.add(filePath.toURI().toURL());
                continue;
            }

            // if directory, search all matching files to add to classloader
            if (filePath.isDirectory()) {
                File[] files = filePath.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return filePattern.matcher(file.getName()).matches();
                    }
                });

                for (int j = 0; j < files.length; j++) {
                    URL url = files[j].toURI().toURL();
                    classpath.add(url);
                }
            }
        }

        URL[] urls = new URL[classpath.size()];
        ClassLoader cl = new URLClassLoader(classpath.toArray(urls), parent);
        return cl;
    }
}