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
package me.asu.tui.builtin;

import me.asu.tui.CliArguments;
import me.asu.tui.CliCmdLineOption;
import me.asu.tui.CliCmdLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliConsole;
import me.asu.tui.api.CliContext;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map.Entry;

public class SysInfoCmd implements CliCommand {

    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "sysinfo";
    CliCmdLineParser parser = new CliCmdLineParser();

    public SysInfoCmd() {
        CliCmdLineOption opt1 = CliCmdLineOption.builder()
                .shortName("p")
                .longName("props")
                .description(
                        "Displays the JVM's system properties.")
                .build();
        CliCmdLineOption opt2 = CliCmdLineOption.builder()
                .shortName("cp")
                .longName("classpath")
                .description(
                        "Displays JVM classpath information.")
                .build();
        CliCmdLineOption opt3 = CliCmdLineOption.builder()
                .shortName("m")
                .longName("mem")
                .description(
                        "Displays memory inforamtion about current JVM.")
                .build();
        CliCmdLineOption opt4 = CliCmdLineOption.builder()
                .shortName("h")
                .longName("help")
                .description(
                        "Print help message.")
                .build();
        this.parser.addOption(opt1, opt2, opt3, opt4);
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        CliConsole c = ctx.getCliConsole();
        CliArguments arguments = parse(args);
        boolean showHelp = true;
        if (arguments.hasParam("p")) {
            props(c);
            showHelp = false;
        }
        if (arguments.hasParam("cp")) {
            classpath(c);
            showHelp = false;
        }

        if (arguments.hasParam("m")) {
            mem(c);
            showHelp = false;
        }

        if (arguments.hasParam("h") || showHelp) {
            printUsage(c);
        }

        return null;
    }


    private void mem(CliConsole c) {
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        c.printf("%nHeap Memory Usage:%n");
        c.printf("\t-Initial: %d%n", bean.getHeapMemoryUsage().getInit());
        c.printf("\t-Max: %d%n", bean.getHeapMemoryUsage().getMax());
        c.printf("\t-Committed: %d%n", bean.getHeapMemoryUsage().getCommitted());
        c.printf("\t-Used: %d", bean.getHeapMemoryUsage().getUsed());
        c.printf("%n%n");
    }

    private void classpath(CliConsole c) {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        c.printf("%nClasspath: %s", bean.getClassPath());
        c.printf("%nBoot Classpath: %s%n%n", bean.getBootClassPath());
    }

    private void props(CliConsole c) {
        c.printf("%nSystem Properties");
        c.printf("%n-----------------");
        for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
            displaySystemProperty(c, (String) entry.getKey());
        }
        c.printf("%n%n");
    }

    private void displaySystemProperty(CliConsole c, String propName) {
        if (propName == null || propName.isEmpty()) {
            c.printf("%n Property name is missing. Provide a property name.%n%n");
            return;
        }
        String propVal = System.getProperty(propName);
        if (propVal != null) {
            c.printf("%n%1$30s %2$5s %3$s", propName, " ", propVal);
        }
    }

    @Override
    public void plug(CliContext plug) {
        //descriptor = new SysInfoDescriptor();
    }

    @Override
    public void unplug(CliContext plug) {
        // nothing to do
    }

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public String getDescription() {
        return "Displays current JVM runtime information.";
    }

}