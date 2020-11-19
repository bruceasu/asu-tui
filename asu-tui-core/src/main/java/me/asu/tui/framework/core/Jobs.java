package me.asu.tui.framework.core;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import me.asu.tui.framework.api.CliCommand.Descriptor;
import me.asu.tui.framework.api.CliConsole;

public class Jobs {

    private AtomicInteger idGenerator = new AtomicInteger(0);

    private static Jobs instance = new Jobs();

    private final Vector<BgRunner> jobs = new Vector<BgRunner>();

    public static Jobs getInstance() {
        return instance;
    }

    public void add(BgRunner runner) {
        this.jobs.addElement(runner);
        Future<?> future = BgExecutor.submit(runner);
        runner.setFuture(future);
        int id = idGenerator.incrementAndGet();
        if (id < 0) {
            idGenerator.set(0);
            id = idGenerator.incrementAndGet();
        }
        runner.setId(id);
    }

    public void remove(BgRunner runner) {
        if (runner != null) {
            this.jobs.removeElement(runner);
        }
    }

    public void print(CliConsole console) {
        Enumeration<BgRunner> localEnumeration = this.jobs.elements();
        while (localEnumeration.hasMoreElements()) {
            BgRunner runner = localEnumeration.nextElement();
            boolean done = false;
            if (runner.isDone()) {
                this.remove(runner);
                done = true;
            }
            console.printf("%d", runner.getId());
            console.printf(": ");
            Descriptor descriptor = runner.getCmd().getDescriptor();
            console.printf("%s/%s %s%n",
                    descriptor.getNamespace(),
                    descriptor.getName(),
                    (done ? " done " : " running "));

        }
    }

    public void kill(int[] jobIds) {
        for (int i = 0; i < jobIds.length; i++) {
            int j = jobIds[i];
            BgRunner runner = find(j);
            if (runner == null) {
                continue;
            }
            remove(runner);
            runner.kill();
        }
    }

    private BgRunner find(long jobId) {
        Enumeration<BgRunner> elements = this.jobs.elements();
        while (elements.hasMoreElements()) {
            BgRunner runner = elements.nextElement();

            if (runner.getId() == jobId) {
                return runner;
            }
        }
        return null;
    }


}