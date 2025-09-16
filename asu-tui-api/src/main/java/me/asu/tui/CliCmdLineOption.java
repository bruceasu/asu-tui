package me.asu.tui;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CliCmdLineOption {
    String shortName;
    String longName;
    String description;
    boolean hasArg = false;

    public CliCmdLineOption(String shortName, String longName, String description, boolean hasArg) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.hasArg = hasArg;
    }

    public static CliCmdLineOptionBuilder builder() {
        return new CliCmdLineOptionBuilder();
    }

    public static class CliCmdLineOptionBuilder {
        String shortName;
        String longName;
        String description;
        boolean hasArg = false;
        public CliCmdLineOptionBuilder shortName(String n) {
            shortName = n;
            return this;
        }

        public CliCmdLineOptionBuilder longName(String n) {
            longName = n;
            return this;
        }

        public CliCmdLineOptionBuilder description(String n) {
            description = n;
            return this;
        }

        public CliCmdLineOptionBuilder hasArg(boolean n) {
            hasArg = n;
            return this;
        }

        public CliCmdLineOption build() {
            return new CliCmdLineOption(shortName, longName, description, hasArg);
        }
    }
}