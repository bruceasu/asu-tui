package me.asu.tui.framework.core;

import java.nio.file.Path;
import org.junit.Test;

/**
 * @author suk
 * @since 2018/8/20
 */
public class ClassPathUtilsTest {

    @Test
    public void findRootPathForClass() throws Exception {
        Path rootPathForClass = ClassPathUtils.findRootPathForResource("org/junit/Test.class", getClass().getClassLoader());
        System.out.println(rootPathForClass);

        rootPathForClass = ClassPathUtils.findRootPathForResource("java/lang/String.class", getClass().getClassLoader());
        System.out.println(rootPathForClass);

        Path rootPathForClass2 = ClassPathUtils.findRootPathForClass(ClassPathUtilsTest.class);
        System.out.println(rootPathForClass2);
    }

}