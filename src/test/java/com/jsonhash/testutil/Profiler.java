package com.jsonhash.testutil;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * This class helps to measure approximate performance of any runtime code in operations per second (OPS).
 */
public class Profiler {
    private final String id;
    private final CurrentTimeProvider timeProvider;

    private long startAtMillis = 0;
    private AtomicLong tickCounter = new AtomicLong();
    private long runningTimeMillis = 0;
    private boolean started = false;
    private boolean finished = false;

    public Profiler(String id, CurrentTimeProvider timeProvider) {
        this.id = id;
        this.timeProvider = timeProvider;
    }

    public static Profiler start(String id, CurrentTimeProvider timeProvider) {
        Profiler profiler = new Profiler(id, timeProvider);
        profiler.start();
        return profiler;
    }

    public static Profiler start(String id) {
        return Profiler.start(id, System::currentTimeMillis);
    }

    public void start() {
        ensureNotFinished();
        started = true;
        startAtMillis = timeProvider.currentTimeMillis();
    }

    public long tick() {
        ensureStarted();
        ensureNotFinished();
        return tickCounter.incrementAndGet();
    }

    public long getTickCount() {
        return tickCounter.get();
    }

    public Duration getRunningTime() {
        Duration runningTime;
        if (finished) {
            runningTime = Duration.ofMillis(runningTimeMillis);
        } else {
            runningTime = Duration.ofMillis(getRunningTimeMillis());
        }
        return runningTime;
    }

    public Duration finish() {
        ensureStarted();
        ensureNotFinished();
        finished = true;
        runningTimeMillis = getRunningTimeMillis();
        return Duration.of(runningTimeMillis, ChronoUnit.MILLIS);
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    private void ensureStarted() {
        if (!started) {
            throw new IllegalStateException("Profiler should be started first.");
        }
    }

    private void ensureNotFinished() {
        if (finished) {
            throw new IllegalStateException("Profiler has already been stopped.");
        }
    }

    /**
     * @return Operations per second. If profiling is not finished yet, current elapsed time is used.
     */
    public float getOps() {
        Duration runningTime = getRunningTime();
        return ((float) tickCounter.get() / max(runningTime.toMillis(), 1)) * 1000;
    }

    public String report() {
        float ops = getOps();
        long tickCount = getTickCount();
        float runningTimeSeconds = (float) getRunningTime().toMillis() / 1000;

        return format(
                "[Profiler] %s: task count = %d, running time seconds = %.3f, ops = %.2f.",
                id, tickCount, runningTimeSeconds, ops
        );
    }

    private long getRunningTimeMillis() {
        long now = timeProvider.currentTimeMillis();
        return (now - startAtMillis);
    }
}
