package me.asu.tui;

import me.asu.tui.api.CliConsole;
import me.asu.tui.api.CliContext;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.asu.tui.I18nUtils.text;

/**
 * @author suk
 * @since 2018/8/16
 */
public class JlineCliConsole implements CliConsole {

    protected Character mask = '*'; // null to hide the input
    protected CliContext ctx;
    //    protected Reader reader;
    protected Terminal terminal;
    protected LineReader lineReader;



    @Override
    public void plug(CliContext plug) {
        ctx = plug;

        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            EnhancedDefaultParser parser = new EnhancedDefaultParser(true);
            parser.setEscapeChars(new char[]{'\\'}); // 支持 \ 作为转义符
            parser.setQuoteChars(new char[]{'"', '\''}); // 支持单/双引号
            // todo: add all the commands
            Completer completer = new StringsCompleter("help", "exit", "cd", "echo",
                    "gc", "history", "jobs", "kill", "native2ascii", "sysinfo");
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .completer(completer)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public PrintWriter writer() {
        return terminal.writer();
    }

    @Override
    public Reader reader() {
        return terminal.reader();
    }

    @Override
    public void printf(String format, Object... args) {
        terminal.writer().printf(format, args);
        flush();
    }

    @Override
    public void println(String format, Object... args) {
        writer().printf(format + "\n", args);
    }

    @Override
    public void newline() {
        writer().printf("\n");
    }

    @Override
    public String readLine(String fmt, Object... args) {
        String prompt = String.format(fmt, args);
        try {
            return lineReader.readLine(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String readLine() {
        return lineReader.readLine();
    }

    @Override
    public char[] readPassword(String fmt, Object... args) {
        String prompt = String.format(fmt, args);
        try {
            return lineReader.readLine(prompt, mask).toCharArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public char[] readPassword() {
        try {
            return lineReader.readLine(text("prompt.password.message"), '*').toCharArray();
        } catch (Exception e) {
            e.printStackTrace(writer());
            return null;
        }
//        // 带掩码
//        String pwd1 = reader.readLine("Password (masked): ", '*');
//        terminal.writer().println("你输入了(长度): " + pwd1.length());
//
//        // 完全不回显
//        String pwd2 = reader.readLine("Password (hidden): ", (Character) null);
//        terminal.writer().println("你输入了(长度): " + pwd2.length());
//
//        terminal.flush();

    }

    @Override
    public void flush() {
        terminal.flush();
    }

    /**
     * 在 DefaultParser 基础上增强：
     * - 支持嵌套引号
     * - 支持变量展开 ($VAR, ${VAR})
     * - 支持宽松模式（lenient）
     * - 支持命令替换 (`cmd`, $(cmd))
     */
    public static class EnhancedDefaultParser extends DefaultParser {

        private final boolean lenient;

        public EnhancedDefaultParser(boolean lenient) {
            this.lenient = lenient;
            setEscapeChars(new char[]{'\\'});
            setQuoteChars(new char[]{'"', '\''});
        }

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
            ParsedLine parsed;
            try {
                parsed = super.parse(line, cursor, context);
            } catch (SyntaxError e) {
                if (lenient) {
                    return new SimpleParsedLine(line, cursor, Collections.singletonList(line));
                } else {
                    throw e;
                }
            }

            List<String> words = new ArrayList<>(parsed.words());

            // Step 1: 变量展开
            List<String> expanded = new ArrayList<>();
            for (String w : words) {
                expanded.add(expandVariables(w));
            }

            // Step 2: 命令替换
            List<String> substituted = new ArrayList<>();
            for (String w : expanded) {
                substituted.add(expandCommand(w));
            }

            // Step 3: 去掉多余的引号
            List<String> finalWords = handleQuotes(substituted);

            return new SimpleParsedLine(parsed.line(), parsed.cursor(), finalWords);
        }

        /**
         * 去掉首尾引号
         */
        private List<String> handleQuotes(List<String> words) {
            List<String> result = new ArrayList<>();
            for (String word : words) {
                if ((word.startsWith("\"") && word.endsWith("\"")) ||
                        (word.startsWith("'") && word.endsWith("'"))) {
                    result.add(word.substring(1, word.length() - 1));
                } else {
                    result.add(word);
                }
            }
            return result;
        }

        /**
         * 变量展开：$VAR 和 ${VAR}
         */
        private String expandVariables(String token) {
            StringBuilder result = new StringBuilder();
            char[] chars = token.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '$') {
                    if (i + 1 < chars.length && chars[i + 1] == '{') {
                        int end = token.indexOf('}', i + 2);
                        if (end > i) {
                            String var = token.substring(i + 2, end);
                            result.append(resolveVar(var));
                            i = end;
                            continue;
                        }
                    } else {
                        int j = i + 1;
                        while (j < chars.length &&
                                (Character.isLetterOrDigit(chars[j]) || chars[j] == '_')) {
                            j++;
                        }
                        String var = token.substring(i + 1, j);
                        result.append(resolveVar(var));
                        i = j - 1;
                        continue;
                    }
                }
                result.append(chars[i]);
            }
            return result.toString();
        }

        private String resolveVar(String name) {
            String val = System.getenv(name);
            if (val == null) {
                val = System.getProperty(name);
            }
            return val != null ? val : "";
        }

        /**
         * 命令替换：`cmd` 或 $(cmd)
         */
        private String expandCommand(String token) {
            String result = token;

            // $(cmd)
            int start = result.indexOf("$(");
            while (start >= 0) {
                int end = findMatchingParen(result, start + 2);
                if (end > 0) {
                    String cmd = result.substring(start + 2, end);
                    String output = runCommand(cmd);
                    result = result.substring(0, start) + output + result.substring(end + 1);
                } else {
                    break;
                }
                start = result.indexOf("$(", start + 1);
            }

            // `cmd`
            int backtickStart = result.indexOf('`');
            while (backtickStart >= 0) {
                int backtickEnd = result.indexOf('`', backtickStart + 1);
                if (backtickEnd > backtickStart) {
                    String cmd = result.substring(backtickStart + 1, backtickEnd);
                    String output = runCommand(cmd);
                    result = result.substring(0, backtickStart) + output + result.substring(backtickEnd + 1);
                } else {
                    break;
                }
                backtickStart = result.indexOf('`', backtickStart + 1);
            }

            return result.trim();
        }

        private int findMatchingParen(String str, int start) {
            int depth = 1;
            for (int i = start; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') {
                    depth--;
                    if (depth == 0) return i;
                }
            }
            return -1;
        }

        private String runCommand(String cmd) {
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line).append(" ");
                    }
                    process.waitFor();
                    return out.toString().trim();
                }
            } catch (IOException | InterruptedException e) {
                return "";
            }
        }

        /**
         * 简单的 ParsedLine 实现
         */
        private static class SimpleParsedLine implements ParsedLine {
            private final String line;
            private final int cursor;
            private final List<String> words;

            SimpleParsedLine(String line, int cursor, List<String> words) {
                this.line = line;
                this.cursor = cursor;
                this.words = words;
            }

            @Override public String word() { return words.isEmpty() ? "" : words.get(words.size() - 1); }
            @Override public int wordCursor() { return cursor; }
            @Override public int wordIndex() { return words.size() - 1; }
            @Override public List<String> words() { return words; }
            @Override public String line() { return line; }
            @Override public int cursor() { return cursor; }
        }
    }
}