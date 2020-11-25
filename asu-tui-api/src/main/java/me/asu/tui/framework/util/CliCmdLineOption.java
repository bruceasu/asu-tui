package me.asu.tui.framework.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CliCmdLineOption
{

    String  shortName;
    String  longName;
    String  description;
    boolean hasArg = false;


}
