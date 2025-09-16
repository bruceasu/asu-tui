package me.asu.tui.api;

/**
 * Interface for Plugin.  This is the root interface for all other runtime-loadable
 * compoennts for clamshell.
 *
 * @author vladimir vivien
 */
public interface CliPlugin {
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * This is the entry point to all plugin components when they instantiated
     * by the clam container.
     *
     * @param plug the global context for component.
     */
    default void plug(CliContext plug) {}

    /**
     * This is the exit point when components are done during lifecycle of the
     * clamshell container.
     *
     * @param plug context
     */
    default void unplug(CliContext plug) {}
}