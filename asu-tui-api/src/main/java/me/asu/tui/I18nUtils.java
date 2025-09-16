package me.asu.tui;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author victor.
 * @since 2018/8/16
 */
public class I18nUtils {

    static ResourceBundle rb;

    static {
        rb = loadResourceBundle("cli-messages");
    }

    public static String text(String key) {
        return rb.getString(key);
    }

    public static String[] array(String key) {
        return rb.getStringArray(key);
    }

    public static ResourceBundle loadResourceBundle(String baseName) {
        Locale locale = getLocale();
        try {
            return ResourceBundle.getBundle(baseName, locale);
        } catch (Exception e) {
            // 如果找不到对应语言的资源包，则使用默认资源包
            return ResourceBundle.getBundle(baseName, Locale.getDefault());
        }
    }

    private static Locale getLocale() {
        String cliLang = System.getProperty("cli.lang");
        if (cliLang != null && !cliLang.isEmpty()) {
            // 环境变量中的语言设置
            return Locale.forLanguageTag(cliLang);
        } else {
            // 操作系统的默认语言设置
            return Locale.getDefault();
        }
    }
}