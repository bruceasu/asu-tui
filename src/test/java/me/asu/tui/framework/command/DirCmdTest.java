package me.asu.tui.framework.command;

import me.asu.tui.framework.api.Context;
import me.asu.tui.framework.core.Shell.Runtime;
import me.asu.tui.framework.core.ShellContext;

/**
 * @author suk
 * @since 2018/8/24
 */
public class DirCmdTest {

    public static void main(String[] args) {

        DirCmd cmd = new DirCmd();
        String[] args1 = new String[]{""};
        ShellContext context = Runtime.getContext();
        context.putValue(Context.KEY_COMMAND_LINE_ARGS, args1);
        cmd.plug(context);
        Object execute = cmd.execute(context);
        System.out.println("execute = " + execute);
        cmd.unplug(context);
    }
}