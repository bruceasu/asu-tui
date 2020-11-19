package me.asu.tui.framework.api;

/**
 * The role of the InputController component is to take a input from the command line
 * and interpret it accordingly.  A simple implementation may include all logic,
 * however, more sophisticated implementations may delegate workload to Command
 * objects.
 *
 * @author suk
 */
public interface CliController extends CliPlugin
{

    /**
     * This method is invoked when there is an input from the console to be interpreted.
     * The input value is passed to the controller via the context instance
     * as Context.KEY_COMMAND_LINE_INPUT.  Implementors should return a boolean
     * indicating if the controller handled the input.
     *
     * @param ctx instance of Context
     * @return true - if handled by the controller, false if not.
     */
    boolean handle(CliContext ctx);

    Boolean isEnabled();

    void setEnabled(Boolean flag);

}
