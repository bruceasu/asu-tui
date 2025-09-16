package me.asu.tui;

import java.util.*;

public class StringParser {

    public static String[] parseString(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder currentString = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean inBackticks = false; // 新增：用于跟踪是否在反引号内
        boolean isEscaping = false;
        Map<String, String> env = new HashMap<>();
        env.putAll(System.getenv());
        Properties properties = System.getProperties();
        properties.forEach((key, value) -> env.put(key.toString(), value.toString()));

        for (char ch : input.toCharArray()) {
            if (isEscaping && (inSingleQuotes || inDoubleQuotes)) {
                currentString.append(translateEscapeSequence(ch));
                isEscaping = false;
            } else if (ch == '\\' && (inSingleQuotes || inDoubleQuotes)) {
                isEscaping = true;
            } else if (ch == '\'' && !inDoubleQuotes && !inBackticks) {
                inSingleQuotes = !inSingleQuotes;
                if (!inSingleQuotes) {
                    result.add(currentString.toString());
                    currentString = new StringBuilder();
                }
            } else if (ch == '\"' && !inSingleQuotes && !inBackticks) {
                inDoubleQuotes = !inDoubleQuotes;
                if (!inDoubleQuotes) {
                    final String string = currentString.toString();
                    result.add(PlaceholderUtils.resolvePlaceholders(string, env));
                    currentString = new StringBuilder();
                }
            } else if (ch == '`' && !inSingleQuotes && !inDoubleQuotes) {
                inBackticks = !inBackticks;
                if (!inBackticks) {
                    final String string = currentString.toString();
                    result.add(PlaceholderUtils.resolvePlaceholders(string, env));
                    currentString = new StringBuilder();
                }
            } else if (Character.isWhitespace(ch) && !inSingleQuotes && !inDoubleQuotes && !inBackticks) {
                if (currentString.length() > 0) {
                    result.add(currentString.toString());
                    currentString = new StringBuilder();
                }
            } else {
                currentString.append(ch);
            }
        }

        if (currentString.length() > 0) {
            result.add(currentString.toString());
        }

        return result.toArray(new String[0]);
    }

    private static char translateEscapeSequence(char ch) {
        switch (ch) {
        case 'n':
            return '\n';
        case 't':
            return '\t';
        case 'r':
            return '\r';
        case 'f':
            return '\f';
        case 'b':
            return '\b';
        case '\'':
            return '\'';
        case '\"':
            return '\"';
        case '\\':
            return '\\';
        // 添加对其他转义序列的处理
        default:
            return ch; // 不识别的转义序列，返回原字符
        }
    }

    public static void main(String[] args) {
        String test = "This is a test 'string with single quotes' and \"string with double quotes ${user.dir} and \\\" escaped quote\" and `string in backticks ${USERPROFILE} \n with new line`.";
        String[] parsedStrings = parseString(test);
        for (int i = 0; i < parsedStrings.length; i++) {
            String str = parsedStrings[i];
            System.out.println(i + " >  " + str);
        }
    }
}