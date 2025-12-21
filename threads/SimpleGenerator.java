package threads;

import functions.basic.Log;
import java.util.Random;

public class SimpleGenerator implements Runnable {
    private final Task task;
    private final Random random = new Random();
    private volatile boolean running = true;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            int taskCount = task.getTaskCount();
            System.out.println("Генератор [" + Thread.currentThread().getName() + "]: начал работу, заданий: " + taskCount);

            for (int i = 1; i <= taskCount && running && !Thread.currentThread().isInterrupted(); i++) {
                double base = 1.0 + random.nextDouble() * 9.0;
                Log logFunction = new Log(base);

                double leftBound = random.nextDouble() * 100;
                double rightBound = 100 + random.nextDouble() * 100;
                double step = random.nextDouble();

                synchronized (task) {
                    task.setFunction(logFunction);
                    task.setLeftBound(leftBound);
                    task.setRightBound(rightBound);
                    task.setStep(step);
                }

                System.out.printf("Генератор: Source %.6f %.6f %.6f (основание: %.3f) [%d/%d]\n",
                        leftBound, rightBound, step, base, i, taskCount);

                Thread.sleep(10);
            }

            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Генератор [" + Thread.currentThread().getName() + "]: был прерван");
            } else {
                System.out.println("Генератор [" + Thread.currentThread().getName() + "]: завершил работу");
            }

        } catch (InterruptedException e) {
            System.out.println("Генератор [" + Thread.currentThread().getName() + "]: был прерван ожиданием");
        } catch (Exception e) {
            System.out.println("Генератор [" + Thread.currentThread().getName() + "]: ошибка - " + e.getMessage());
        }
    }
}