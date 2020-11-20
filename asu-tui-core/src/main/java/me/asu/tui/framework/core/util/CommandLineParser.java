package me.asu.tui.framework.core.util;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;

@Data
public class CommandLineParser
{

    boolean  bg = false;
    String   cmd;
    String[] args;
    String[] tokens;


    public void parse(String cl)
    {

        if (cl == null || cl.trim().length() == 0) {
            return;
        }

        cl = cl.trim();
        if (cl.charAt(cl.length() - 1) == '&') {
            bg = true;
            cl = cl.substring(0, cl.length() - 1).trim();
        }
        //String[] tokens = cl.split("\\s+");
        tokens = token(cl);
        if (tokens.length == 1) {
            this.cmd = tokens[0];
        } else {
            this.cmd = tokens[0];
            this.args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, this.args, 0, this.args.length);
        }
    }

    /**
     * 支持 double quote 做为单字分界 支持 转义符号 \ 转义： \\ \" \t \n \[SPC]
     *
     * @param line 命令行
     * @return 单词数组
     */
    String[] token(String line)
    {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        List<String> tokens = new LinkedList<>();
        char[] chars = line.toCharArray();
        StringBuilder buffer = new StringBuilder();
        boolean quoteStart = false;
        boolean escapeStart = false;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '\"') {
                if (escapeStart) {
                    buffer.append('\"');
                    escapeStart = false;
                } else if (quoteStart) {
                    // finished
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                    quoteStart = false;
                } else {
                    quoteStart = true;
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
            } else if (ch == '\\') {
                if (escapeStart) {
                    buffer.append('\\');
                    escapeStart = false;
                } else {
                    escapeStart = true;
                }
            } else if (ch == 't') {
                if (escapeStart) {
                    buffer.append('\t');
                    escapeStart = false;
                } else {
                    buffer.append(ch);
                }
            } else if (ch == 'n') {
                if (escapeStart) {
                    buffer.append('\n');
                    escapeStart = false;
                } else {
                    buffer.append(ch);
                }
            } else if (ch == ' ') {
                if (quoteStart) {
                    buffer.append(' ');
                } else if (escapeStart) {
                    buffer.append(' ');
                    escapeStart = false;
                } else {
                    // finish
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    } else {
                        // skip
                    }
                }
            } else if (ch == '\t') {
                if (quoteStart) {
                    buffer.append('\t');
                } else if (escapeStart) {
                    buffer.append('\t');
                    escapeStart = false;
                } else {
                    // finish
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    } else {
                        // skip
                    }
                }
            } else {
                buffer.append(ch);
            }
        }
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
            buffer.setLength(0);
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}