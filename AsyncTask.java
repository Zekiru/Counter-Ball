/**
    The AsyncTask abstract class, practically the backbone for majority
    of the (pseudo)asynchronous task handling. Allows for easy creation of
    looped threads, either with a defined end time or without.
    @author Ezekiel Villasurda (236689)
    @version 20 May 2025
    I have not discussed the Java language code in our program
    with anyone other than my instructor or the teaching assistants
    assigned to this course.
    I have not used Java language code obtained from another student,
    or any other unauthorized source, either modified or unmodified.
    If any Java language code or documentation used in my program
    was obtained from another source, such as a textbook or website,
    that has been clearly noted with a proper citation in the comments
    of my program.
**/

public abstract class AsyncTask implements Runnable {
    protected volatile boolean running = true;
    protected int interval; // In Milliseconds
    protected double duration = 0; // In Seconds
    private boolean defined;

    // Undefined Loop End Time:
    public AsyncTask(int interval) {
        this.interval = interval;
        this.defined = false;
    }

    // Defined Loop End Time:
    public AsyncTask(int interval, double duration) {
        this.interval = interval;
        this.duration = duration;
        this.defined = true;
    }
    
    public void startTask() { new Thread(this).start(); } // Creates and runs the Thread
    public void endTask() { this.running = false; } // Ends the Thread
    public boolean isRunning() {return this.running; } // Returns the running status of the Thread

    protected abstract void runnable(); // Establishes the contents of the loop
    protected void finish() {} // Optional instructions at the end of the loop (needs to be overriden)

    // The System for creating the Thread Loops
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
