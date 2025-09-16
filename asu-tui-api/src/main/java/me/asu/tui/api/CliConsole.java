package me.asu.tui.api;

import java.io.PrintWriter;
import java.io.Reader;

/**
 * @author suk
 * @since 2018/8/16
 */
public interface CliConsole extends CliPlugin {
    PrintWriter writer();

    Reader reader();

    void printf(String format, Object... args);

    void println(String format, Object... args);

    void newline();

    String readLine(String fmt, Object... args);

    String readLine();

    char[] readPassword(String fmt, Object... args);

    char[] readPassword();

    void flush();
}