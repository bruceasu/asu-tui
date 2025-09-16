package me.asu.tui.js;

import lombok.Data;
import me.asu.tui.CliCmdLineParser;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suk
 * @since 2018/8/17
 */
@Data
public class JsWrapperCmd implements CliCommand {

    private static final String NAMESPACE = "shcmd";
    private final Map<String, String> arguments = new HashMap<>();
    private final Path file;

    private CliCmdLineParser parser = new CliCmdLineParser();
    private String name;
    private List<String> commands = new ArrayList<String>();
    private String description;
    private String usage;
    private String charset = Charset.defaultCharset().name();
    private CliContext ctx;

    public JsWrapperCmd(Path file) {
        this.file = file;
        this.name = file.getFileName().toString();
    }

    @Override
    public void plug(CliContext plug) {
        this.ctx = plug;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public CliCmdLineParser getCliCmdLineParser() {
        return parser;
    }

    @Override
    public Object execute(CliContext ctx, String[] args) {
        return JsEngineUtil.execute(ctx, this.file, args);
    }


}