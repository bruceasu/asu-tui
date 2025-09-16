package me.asu.tui;

import me.asu.tui.api.CliConsole;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static final List<String> commands = new ArrayList<>();

    public static void load() {
        // todo load from file
    }

    public static void store() {
        // todo save to file

    }

    public static void add(String current) {
        commands.add(current);
    }

    public static String previous(int n) {
        if (commands.isEmpty()) {
            return "";
        }

        int i = commands.size() - n;
        if (i == -1) {
            i = 0;
        }
        ;
        return commands.get(i);
    }

    public static String next(int n) {
        int i = n;
        if (i > commands.size()) {
            i = commands.size() - 1;
        }
        ;
        return commands.get(i);
    }

    public static void printLast(int n, CliConsole console) {
        if ((n == 0) || (n > commands.size())) {
            n = commands.size();
        }
        int i = commands.size() - n;
        int j = commands.size() - 1;
        for (int k = i; k <= j; k++) {
            String localRecord = commands.get(k);
            console.printf("%s%n", localRecord);
        }
    }

    private static void trimHistory(int limit) {
        int k = commands.size() - limit;
        if (k > 0) {
            String[] strings = commands.toArray(new String[0]);
            commands.clear();
            for (int j = k; j < strings.length; j++) {
                commands.add(strings[j]);
            }

        }
    }

    public String[] getHistArray() {
        return commands.toArray(new String[0]);
    }
}