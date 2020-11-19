package me.asu.tui.framework.core.util;

class ConsoleEraser extends Thread {
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
