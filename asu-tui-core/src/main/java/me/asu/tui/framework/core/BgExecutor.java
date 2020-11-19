package me.asu.tui.framework.core;

import java.util.concurrent.*;
import me.asu.tui.framework.core.util.NamedThreadFactory;

public class BgExecutor
{

    private static final ExecutorService es = new ThreadPoolExecutor(128, 128, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            new NamedThreadFactory("BgExecutor"));

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));
    }

    public static void shutdown()
    {
        if (es != null && !es.isTerminated()) {
            es.shutdownNow();
            try {
                es.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void execute(Runnable command)
    {
        es.execute(command);
    }

    public static <T> Future<T> submit(Callable<T> task)
    {
        return es.submit(task);
    }

    public static Future<?> submit(Runnable task)
    {
        return es.submit(task);
    }
}
