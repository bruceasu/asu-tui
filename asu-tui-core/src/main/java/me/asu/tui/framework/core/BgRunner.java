package me.asu.tui.framework.core;

import java.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;
import me.asu.tui.framework.api.CliCommand;
import me.asu.tui.framework.api.CliContext;

@Getter
public class BgRunner implements Runnable
{
    @Setter
    Integer id;

    private CliCommand cmd;

    private String[] cmdOpts;

    private Future<?> future;

    public void setFuture(Future<?> future)
    {
        this.future = future;
    }

    @Override
    public void run() {
        try {
            if (cmd != null) {
                CliContext context = CliRuntime.getContext();
                cmd.execute(context, cmdOpts);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public BgRunner(CliCommand cmd, String args[]) {
        this.cmd = cmd;
        this.cmdOpts = args;
    }

    public void kill() {
        boolean started = future != null;
        if (started && !(isCancelled() || isDone())) {
            future.cancel(true);
        }
    }

    public boolean isDone()
    {
        return future.isDone();
    }

    public boolean isCancelled()
    {
        return future.isCancelled();
    }


}
