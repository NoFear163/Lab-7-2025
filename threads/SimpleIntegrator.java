package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;
    private volatile boolean running = true;

    public SimpleIntegrator(Task task) {
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
            int tasksProcessed = 0;

            System.out.println("Интегратор [" + Thread.currentThread().getName() + "]: начал работу, ожидаю заданий: " + taskCount);

            while (tasksProcessed < taskCount && running && !Thread.currentThread().isInterrupted()) {
                double leftBound = 0, rightBound = 0, step = 0;
                boolean hasTask = false;

                synchronized (task) {
                    if (task.getFunction() != null) {
                        leftBound = task.getLeftBound();
                        rightBound = task.getRightBound();
                        step = task.getStep();
                        hasTask = true;
                    }
                }

                if (!hasTask) {
                    Thread.sleep(10);
                    continue;
                }

                try {
                    double result = Functions.integrate(task.getFunction(), leftBound, rightBound, step);

                    System.out.printf("Интегратор: Result %.6f %.6f %.6f %.15f [%d/%d]\n",
                            leftBound, rightBound, step, result, tasksProcessed + 1, taskCount);

                    tasksProcessed++;

                    synchronized (task) {
                        task.setFunction(null);
                    }

                    Thread.sleep(10);

                } catch (IllegalArgumentException e) {
                    System.out.printf("Интегратор: ошибка задания - %s [%d/%d]\n",
                            e.getMessage(), tasksProcessed + 1, taskCount);
                    tasksProcessed++;

                    synchronized (task) {
                        task.setFunction(null);
                    }
                }
            }

            System.out.println("Интегратор [" + Thread.currentThread().getName() + "]: завершил, обработано: " + tasksProcessed + "/" + taskCount);

        } catch (InterruptedException e) {
            System.out.println("Интегратор [" + Thread.currentThread().getName() + "]: был прерван");
        } catch (Exception e) {
            System.out.println("Интегратор [" + Thread.currentThread().getName() + "]: ошибка - " + e.getMessage());
        }
    }
}