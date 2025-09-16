package me.asu.tui;

import java.util.*;

public class AlignPrint {
    public static void main(String[] args) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("cd", "cd <dir> Change directory.");
        map.put("echo", "Echo the input");
        map.put("exit", "Type 'exit' to exits ClamShell.");
        map.put("gc", "type 'gc' to run the garbage collector.");
        map.put("help", "Type 'help' or 'help [command_name]' to displays help information for available commands.");
        map.put("history", "history [n] prints the last n commands. If n is omitted,\nall recorded commands are printed. In both cases,\nthe number of commands printed is limited by the\nvalue of asu.shell.history_size.");
        map.put("jobs", "Prints currently running background jobs.");
        map.put("killjob", "killjob <jobId> to terminates execution of the specified jobs.\nThe job numbers are obtained by running the jobs Cmd.");
        map.put("native2ascii", "Escape or unescape unicode character to ascii");
        map.put("sysinfo", "Displays current JVM runtime information.");

        System.out.println(formatAligned(map, 80));
    }

    public static String formatAligned(Map<String, String> map, int maxWidth) {
        StringBuilder result = new StringBuilder();

        // 计算最长 key 的字节宽度（中文=2，英文=1）
        int maxKeyWidth = map.keySet().stream()
                .mapToInt(AlignPrint::stringWidth)
                .max().orElse(0);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String[] rawLines = value.split("\n");
            boolean firstLine = true;

            for (String rawLine : rawLines) {
                int valueLimit = maxWidth - maxKeyWidth - 1;
                List<String> wrapped = wrapText(rawLine, valueLimit);

                for (String w : wrapped) {
                    if (firstLine) {
                        result.append(padRight(key, maxKeyWidth))
                              .append(" ")
                              .append(w)
                              .append("\n");
                        firstLine = false;
                    } else {
                        result.append(padRight("", maxKeyWidth))
                              .append(" ")
                              .append(w)
                              .append("\n");
                    }
                }
            }
        }

        return result.toString();
    }

    // 按字节宽度切分字符串（中文=2，英文=1）
    private static List<String> wrapText(String text, int limit) {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int width = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = (c <= 0x7F) ? 1 : 2;

            if (width + charWidth > limit) {
                result.add(line.toString());
                line = new StringBuilder();
                width = 0;
            }
            line.append(c);
            width += charWidth;
        }
        if (line.length() > 0) {
            result.add(line.toString());
        }
        return result;
    }

    // 计算字符串显示宽度
    private static int stringWidth(String s) {
        int w = 0;
        for (char c : s.toCharArray()) {
            w += (c <= 0x7F) ? 1 : 2;
        }
        return w;
    }

    // 按字节宽度右填充
    private static String padRight(String s, int targetWidth) {
        int width = stringWidth(s);
        StringBuilder sb = new StringBuilder(s);
        while (width < targetWidth) {
            sb.append(" ");
            width++;
        }
        return sb.toString();
    }
}