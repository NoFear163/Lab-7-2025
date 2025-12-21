package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final Semaphore semaphore;
    private volatile boolean running = true;
    private int tasksProcessed = 0;
    private long totalProcessingTime = 0;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
        this.setDaemon(false);
    }

    public void stopRunning() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        try {
            int taskCount = task.getTaskCount();
            System.out.printf("Интегратор: готов обработать до %d задач\n", taskCount);

            while (running) {
                // Быстрая проверка завершения
                if (tasksProcessed >= taskCount && task.isGenerationComplete()) {
                    System.out.println("Интегратор: все задачи обработаны, завершаюсь");
                    break;
                }

                // Проверка на прерывание
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                try {
                    // 1. Ждем доступности задачи
                    semaphore.beginIntegrate();

                    // 2. Быстрое интегрирование
                    long startTime = System.nanoTime();

                    double result = Functions.integrate(
                            task.getFunction(),
                            task.getLeftBound(),
                            task.getRightBound(),
                            task.getStep()
                    );

                    long endTime = System.nanoTime();
                    long taskTime = (endTime - startTime) / 1_000_000; // мс
                    totalProcessingTime += taskTime;

                    tasksProcessed++;

                    // 3. Отладочный вывод
                    if (tasksProcessed <= 5 || tasksProcessed >= taskCount - 5 || tasksProcessed % 20 == 0) {
                        System.out.printf("Интегратор: %d/%d за %d мс, рез=%.6f\n",
                                tasksProcessed, taskCount, taskTime, result);
                    }

                    // 4. Освобождаем семафор
                    semaphore.endIntegrate();

                    // 5. Yield после каждых 5 задач
                    if (tasksProcessed % 5 == 0) {
                        Thread.yield();
                    }

                } catch (IllegalArgumentException e) {
                    // Ошибка в данных - пропускаем задачу
                    tasksProcessed++;
                    semaphore.endIntegrate();
                    System.out.printf("Интегратор: ошибка в задаче %d - %s\n",
                            tasksProcessed, e.getMessage());
                }
            }

            System.out.printf("\n✓ Интегратор ЗАВЕРШЕН. Обработано: %d/%d задач\n" +
                            "Среднее время на задачу: %.1f мс\n",
                    tasksProcessed, taskCount,
                    tasksProcessed > 0 ? (double)totalProcessingTime / tasksProcessed : 0);

        } catch (InterruptedException e) {
            System.out.printf("Интегратор ПРЕРВАН после %d/%d задач\n",
                    tasksProcessed, task.getTaskCount());
        }
    }

    public int getTasksProcessed() {
        return tasksProcessed;
    }

    public long getTotalProcessingTime() {
        return totalProcessingTime;
    }
}