package me.asu.tui.framework.api;

/**
 * Interface for Plugin.  This is the root interface for all other runtime-loadable 
 * compoennts for clamshell.
 * @author vladimir vivien
 */
public interface CliPlugin
{
    /**
     * This is the entry point to all plugin components when they instantiated
     * by the clam container.
     * @param plug the global context for component.
     */
    public void plug(CliContext plug);
    
    /**
     * This is the exit point when components are done during lifecycle of the
     * clamshell container.
     * @param plug context
     */
    public void unplug(CliContext plug);
}