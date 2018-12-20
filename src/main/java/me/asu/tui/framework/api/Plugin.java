/*
 * Copyright 2012 ClamShell-Cli.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.asu.tui.framework.api;

/**
 * Interface for Plugin.  This is the root interface for all other runtime-loadable 
 * compoennts for clamshell.
 * @author vladimir vivien
 */
public interface Plugin {
    /**
     * This is the entry point to all plugin components when they instantiated
     * by the clam container.
     * @param plug the global context for component.
     */
    public void plug(Context plug);
    
    /**
     * This is the exit point when components are done during lifecycle of the
     * clamshell container.
     * @param plug context
     */
    public void unplug(Context plug);
}