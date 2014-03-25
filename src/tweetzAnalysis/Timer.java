package tweetzAnalysis;

import java.util.Observable;

/**
 * Waits for a specified time and sends an interrupt to all registered Observers.
 * @author Christopher Kerth
 *
 */
public class Timer extends Observable implements Runnable {

    public static final int MIN_INTERVAL_TIME = 17 * 60000;
    public static final int MAX_INTERVAL_TIME = 34 * 60000;
    private int millis = MIN_INTERVAL_TIME;
    private static final int SLEEP_TIME = 5;
    private boolean loop;
    private Thread thread;
    private boolean paused;
    public static final int WAITING_FINISHED = 1;

    public Timer() {
        thread = new Thread(this);
    }

    public Timer(int millis, boolean loop) {
        setMillis(millis);
        setLoop(loop);
        thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    public void interrupt() {
        thread.interrupt();
    }

    public void pause() throws InterruptedException {
        setPaused(true);
    }

    public void resume() {
        setPaused(false);
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(getMillis());
                setChanged();
                notifyObservers(WAITING_FINISHED);
                while (isPaused()) {
                    Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                thread.interrupt();
            }
        } while(isLoop() && !thread.isInterrupted());
    }

    public int getMillis() {
        return millis;
    }

    public void setMillis(int millis) {
        if (millis >= MIN_INTERVAL_TIME && millis <= MAX_INTERVAL_TIME) {
            this.millis = millis;
        } else {
            this.millis = MIN_INTERVAL_TIME;
        }
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

}

