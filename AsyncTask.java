
// The backbone of all (Pseudo)asynchronous Processing:

public abstract class AsyncTask implements Runnable {
    protected volatile boolean running = true;
    protected int interval; // In Milliseconds
    protected double duration = 0; // In Seconds
    private boolean defined;

    // Undefined End Time:
    public AsyncTask(int interval) {
        this.interval = interval;
        this.defined = false;
    }

    // Defined End Time:
    public AsyncTask(int interval, double duration) {
        this.interval = interval;
        this.duration = duration;
        this.defined = true;
    }

    public void startTask() { new Thread(this).start(); }
    public void endTask() { this.running = false; }
    public boolean isRunning() {return this.running; }

    protected abstract void runnable();
    protected void finish() {}

    @Override
    public void run() {
        if (!defined) {
            while (running) {
                runnable();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            long iterations = (long) ((duration * 1000) / interval);
            for (long i = 0; i < iterations; i++) {
                if (!running) break;
                runnable();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        finish();
    }
}
