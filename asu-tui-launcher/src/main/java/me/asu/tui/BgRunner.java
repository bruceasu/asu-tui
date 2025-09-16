package me.asu.tui;

import lombok.Getter;
import lombok.Setter;
import me.asu.tui.api.CliCommand;
import me.asu.tui.api.CliContext;

import java.util.concurrent.Future;

@Getter
public class BgRunner implements Runnable {
    @Setter
    Integer id;

    private CliCommand cmd;

    private String[] cmdOpts;

    private Future<?> future;

    public BgRunner(CliCommand cmd, String args[]) {
        this.cmd = cmd;
        this.cmdOpts = args;
    }

    public void setFuture(Future<?> future) {
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

    public void kill() {
        boolean started = future != null;
        if (started && !(isCancelled() || isDone())) {
            future.cancel(true);
        }
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }


}