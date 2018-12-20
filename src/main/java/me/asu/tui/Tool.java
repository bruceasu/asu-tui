package me.asu.tui;


import static me.asu.tui.framework.api.Context.KEY_COMMAND_LINE_ARGS;

import me.asu.tui.framework.api.InputController;
import me.asu.tui.framework.api.IoConsole;
import me.asu.tui.framework.core.Shell.Runtime;
import me.asu.tui.framework.core.ShellContext;
import java.util.List;

/**
 * @author suk
 * @since 2018/8/16
 */
public class Tool {

    public static void main(String[] args) {
        ShellContext context = Runtime.getContext();
        IoConsole console = context.getConsole();

        List<InputController> controllers = context.getControllers();
        if (controllers == null || controllers.isEmpty()) {
            console.printf("没有");
        } else {
            context.putValue(KEY_COMMAND_LINE_ARGS, args);
            for (InputController controller : controllers) {
                boolean handle = controller.handle(context);
                if (handle) {
                    break;
                }
            }
        }
    }


}