package me.asu.tui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PasswordReader {
    public static String readPassword(String paramString)
            throws Exception {
        if (System.console() != null) {
            char[] chars = System.console().readPassword(paramString);
            return new String(chars);
        } else {
            ConsoleEraser localConsoleEraser = new ConsoleEraser();
            System.out.print(paramString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            localConsoleEraser.start();
            String str = reader.readLine();
            localConsoleEraser.halt();
            System.out.print("\b");
            return str;
        }
    }

    static class ConsoleEraser extends Thread {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (this.running) {
                System.out.print("\b ");
                try {
                    sleep(5L);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public synchronized void halt() {
            this.running = false;
            interrupt();
        }
    }
}