package threads;

public class Semaphore {
    private boolean canGenerate = true;
    private boolean canIntegrate = false;

    public synchronized void beginGenerate() throws InterruptedException {
        while (!canGenerate) {
            wait();
        }
    }

    public synchronized void endGenerate() {
        canGenerate = false;
        canIntegrate = true;
        notifyAll();
    }

    public synchronized void beginIntegrate() throws InterruptedException {
        while (!canIntegrate) {
            wait();
        }
    }

    public synchronized void endIntegrate() {
        canIntegrate = false;
        canGenerate = true;
        notifyAll();
    }
}