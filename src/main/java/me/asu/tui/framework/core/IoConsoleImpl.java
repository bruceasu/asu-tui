package me.asu.tui.framework.core;

import static me.asu.tui.framework.api.Context.KEY_INPUT_STREAM;
import static me.asu.tui.framework.api.Context.KEY_OUTPUT_STREAM;
import static me.asu.tui.framework.core.ResourceUtils.text;

import me.asu.gui.util.PasswordDialog;
import me.asu.tui.framework.api.Context;
import me.asu.tui.framework.api.IoConsole;
import java.awt.HeadlessException;
import java.io.*;

/**
 * @author suk
 * @since 2018/8/16
 */
public class IoConsoleImpl implements IoConsole {

    private static final char LF    = '\n';
    private static final char CR    = '\r';
    private static final char LEFT  = 37;
    private static final char UP    = 38;
    private static final char RIGHT = 39;
    private static final char DOWN  = 40;
    Console console = System.console();
    Context     ctx;
    PrintWriter printWriter;
    Reader      reader;

    @Override
    public void plug(Context plug) {
        ctx = plug;
        PrintStream value = (PrintStream) ctx.getValue(KEY_OUTPUT_STREAM);
        if (value == null) {
            value = System.out;
        }
        printWriter = new PrintWriter(value);
        InputStream in = (InputStream) ctx.getValue(KEY_INPUT_STREAM);
        if (in == null) {
            in = System.in;
        }
        reader = new InputStreamReader(in);
    }

    @Override
    public void unplug(Context plug) {

    }

    @Override
    public PrintWriter writer() {
        if (console == null) {
            return printWriter;
        } else {
            return console.writer();
        }
    }

    @Override
    public Reader reader() {
        if (console == null) {
            return reader;
        } else {
            return console.reader();
        }
    }

    @Override
    public void printf(String format, Object... args) {
        if (console == null) {
            printWriter.printf(format, args);
        } else {
            console.printf(format, args);
        }
        flush();
    }

    @Override
    public String readLine(String fmt, Object... args) {
        if (console == null) {
            synchronized (printWriter) {
                printWriter.printf(fmt, args);
                printWriter.flush();
                return readByInnerReader();
            }
        } else {
            return console.readLine(fmt, args);
        }
    }

    @Override
    public String readLine() {
        if (console == null) {
            return readByInnerReader();
        } else {
            return console.readLine();
        }
    }

    @Override
    public char[] readPassword(String fmt, Object... args) {
        if (console == null) {
            try {
                return PasswordDialog.show(String.format(fmt, args));
            } catch (HeadlessException e) {
                synchronized (printWriter) {
                    printWriter.printf(fmt, args);
                    printWriter.flush();
                    return readByInnerReader().toCharArray();
                }
            }
        } else {
            return console.readPassword(fmt, args);
        }

    }

    @Override
    public char[] readPassword() {
        if (console == null) {
            try {
                return PasswordDialog.show(text("prompt.password.message"));
            } catch (HeadlessException e) {
                return readByInnerReader().toCharArray();
            }
        } else {
            return console.readPassword();
        }
    }

    @Override
    public void flush() {
        if (console == null) {
            printWriter.flush();
        } else {
            console.flush();
        }
    }


    private String readByInnerReader() {
        StringBuilder builder = new StringBuilder();
        synchronized (reader) {
            try {
                int i;
                while (LF != (i = reader.read())) {
                    switch (i) {
                        case CR:
                            // skip
                            break;
                        default:
                            builder.append((char) i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
