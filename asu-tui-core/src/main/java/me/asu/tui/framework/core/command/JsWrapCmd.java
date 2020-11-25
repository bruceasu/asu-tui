package me.asu.tui.framework.core.command;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import lombok.Data;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliConfigurator;
import me.asu.tui.framework.api.CliContext;
import me.asu.tui.framework.util.CliCmdLineParser;

/**
 * @author suk
 * @since 2018/8/17
 */
@Data
public class JsWrapCmd implements CliCommand
{

    private static final String NAMESPACE = "shcmd";


    private final InnerDescriptor     descriptor = new InnerDescriptor();
    private final Map<String, String> arguments  = new HashMap<>();
    private final Path                file;
    private       String              name;
    private       List<String>        commands   = new ArrayList<String>();
    private       String              description;
    private       String              usage;
    private       String              charset    = Charset.defaultCharset().name();
    private       CliContext          ctx;

    public JsWrapCmd(Path file)
    {
        this.file = file;
        this.name = file.getFileName().toString();
    }

    @Override
    public void plug(CliContext plug)
    {
        this.ctx = plug;
    }

    @Override
    public void unplug(CliContext plug)
    {

    }

    @Override
    public Descriptor getDescriptor()
    {
        return descriptor;
    }


    @Override
    public Object execute(CliContext ctx, String[] args)
    {

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        //ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            Reader scriptFile = Files.newBufferedReader(this.file);
            SimpleBindings simpleBindings = new SimpleBindings();
            simpleBindings.put("arguments", args);
            return engine.eval(scriptFile, simpleBindings);
        } catch (ScriptException | IOException e) {
            ctx.getCliConsole().printf("Cause error: %s%n", e.getMessage());

            return null;
        }
    }


    private class InnerDescriptor implements Descriptor
    {
        CliCmdLineParser parser = new CliCmdLineParser();
        @Override
        public String getNamespace()
        {
            return NAMESPACE;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getDescription()
        {
            return description;
        }

        @Override
        public CliCmdLineParser getCliCmdLineParser()
        {
            return parser;
        }
    }
}
