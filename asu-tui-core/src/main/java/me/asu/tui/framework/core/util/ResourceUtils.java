package me.asu.tui.framework.core.util;

import java.util.ResourceBundle;

/**
 * @author suk
 * @since 2018/8/16
 */
public class ResourceUtils {

    static ResourceBundle rb;

    static {
        rb = ResourceBundle.getBundle("cli-messages");
    }

    public static String text(String key) {
        return rb.getString(key);
    }

    public static String[] array(String key) {
        return rb.getStringArray(key);
    }

}
