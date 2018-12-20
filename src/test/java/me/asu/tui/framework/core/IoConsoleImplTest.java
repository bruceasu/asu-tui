package me.asu.tui.framework.core;

import me.asu.tui.framework.api.IoConsole;

/**
 * @author suk
 * @since 2018/8/16
 */
public class IoConsoleImplTest {

    public static void main(String[] args) {
        ShellContext ctx =  ShellContext.createInstance();
        IoConsole console=  new IoConsoleImpl();
        console.plug(ctx);
        String s = console.readLine("xxxx:");
        System.out.println("s = " + s);

        char[] chars = console.readPassword();
        System.out.println(new String(chars));

    }

}