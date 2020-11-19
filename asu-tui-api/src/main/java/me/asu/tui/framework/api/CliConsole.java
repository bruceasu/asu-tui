package me.asu.tui.framework.api;

import java.io.*;
import me.asu.tui.framework.api.CliPlugin;

/**
 * @author suk
 * @since 2018/8/16
 */
public interface CliConsole extends CliPlugin
{
    PrintWriter writer();

    Reader reader();

    void printf(String format, Object... args);

    String readLine(String fmt, Object... args);
    String readLine();

    char[] readPassword(String fmt, Object... args);

    char[] readPassword();

    void flush();
}
