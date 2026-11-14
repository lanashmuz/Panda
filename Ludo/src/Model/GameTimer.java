
package Model;

import java.util.concurrent.TimeUnit;

/** Measures actual elapsed game time independently of the UI refresh rate. */
public final class GameTimer {

    private long startNanos;
    private long elapsedNanos;
    private boolean started;
    private boolean running;

    /** Starts the timer once for a new game. */
    public void start() {
        if (started) {
            throw new IllegalStateException(
                    "The game timer has already started.");
        }

        startNanos = System.nanoTime();
        started = true;
        running = true;
    }

    /** Stops the timer and preserves the final duration. */
    public void stop() {
        if (!running) return;

        elapsedNanos = System.nanoTime() - startNanos;
        running = false;
    }

    /** Returns the elapsed duration in seconds for display and game history. */
    public long getElapsedSeconds() {
        if (!started) return 0;

        long elapsed = running
                ? System.nanoTime() - startNanos
                : elapsedNanos;

        return TimeUnit.NANOSECONDS.toSeconds(elapsed);
    }

    public boolean isRunning() {
        return running;
    }
}