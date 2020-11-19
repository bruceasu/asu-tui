package me.asu.tui.framework.util;

import lombok.Data;

@Data
public class CliCmdLineOption
{
    String shortName;
    String longName;
    String description;
    boolean hasArg = false;
}
