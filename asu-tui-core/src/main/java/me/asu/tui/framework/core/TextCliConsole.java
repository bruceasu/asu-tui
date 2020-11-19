package me.asu.tui.framework.core;

import static me.asu.tui.framework.api.CliContext.KEY_INPUT_STREAM;
import static me.asu.tui.framework.api.CliContext.KEY_OUTPUT_STREAM;
import static me.asu.tui.framework.core.util.ResourceUtils.text;

import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.api.CliConsole;
import java.io.*;
import me.asu.tui.framework.core.util.PasswordReader;

/**
 * @author suk
 * @since 2018/8/16
 */
public class TextCliConsole implements CliConsole
{

    protected static final char LF    = '\n';
    protected static final char CR    = '\r';
    protected static final char LEFT  = 37;
    protected static final char UP    = 38;
    protected static final char RIGHT = 39;
    protected static final char DOWN  = 40;
    protected CliContext  ctx;
    protected PrintWriter printWriter;
    protected Reader      reader;

    @Override
    public void plug(CliContext plug) {
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
    public void unplug(CliContext plug) {
    }

    @Override
    public PrintWriter writer() {
        return printWriter;
    }

    @Override
    public Reader reader() {
        return reader;
    }

    @Override
    public void printf(String format, Object... args) {
        printWriter.printf(format, args);
        flush();
    }

    @Override
    public String readLine(String fmt, Object... args) {
        synchronized (printWriter) {
            printWriter.printf(fmt, args);
            printWriter.flush();
            return readByInnerReader();
        }
    }

    @Override
    public String readLine() {
        return readByInnerReader();
    }

    @Override
    public char[] readPassword(String fmt, Object... args) {
        printWriter.printf(fmt, args);
        printWriter.flush();
        //return readByInnerReader().toCharArray();
        try {
            return PasswordReader.readPassword("").toCharArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //try {
        //    return PasswordDialog.show(String.format(fmt, args));
        //} catch (HeadlessException e) {
        //    synchronized (printWriter) {
        //        printWriter.printf(fmt, args);
        //        printWriter.flush();
        //        return readByInnerReader().toCharArray();
        //    }
        //}

    }

    @Override
    public char[] readPassword() {
       // return readByInnerReader().toCharArray();
        try {
            return PasswordReader.readPassword(text("prompt.password.message")).toCharArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //try {
        //    return PasswordDialog.show(text("prompt.password.message"));
        //} catch (HeadlessException e) {
        //    return readByInnerReader().toCharArray();
        //}
    }

    @Override
    public void flush() {
        printWriter.flush();
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
