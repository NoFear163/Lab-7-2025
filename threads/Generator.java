package threads;

import functions.basic.Log;
import java.util.Random;

public class Generator extends Thread {
    private final Task task;
    private final Semaphore semaphore;
    private volatile boolean running = true;
    private final Random random = new Random();
    private int tasksGenerated = 0;

    public Generator(Task task, Semaphore semaphore) {
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
            System.out.printf("Генератор: начинаю генерацию %d задач\n", taskCount);

            while (running && tasksGenerated < taskCount) {
                // Быстрая проверка на прерывание
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                // 1. Захватываем семафор
                semaphore.beginGenerate();

                // 2. Быстрая генерация данных (без пауз)
                double base = 1.0 + random.nextDouble() * 9.0;
                double leftBound = random.nextDouble() * 100;
                double rightBound = 100 + random.nextDouble() * 100;
                double step = 0.001 + random.nextDouble() * 0.999;

                // 3. Устанавливаем в задачу (atomic операции)
                task.setFunction(new Log(base));
                task.setLeftBound(leftBound);
                task.setRightBound(rightBound);
                task.setStep(step);

                tasksGenerated++;

                // 4. Отладочный вывод только для первых и последних задач
                if (tasksGenerated <= 5 || tasksGenerated >= taskCount - 5 || tasksGenerated % 20 == 0) {
                    System.out.printf("Генератор: %d/%d (%.3f-%.3f шаг=%.3f)\n",
                            tasksGenerated, taskCount, leftBound, rightBound, step);
                }

                // 5. Освобождаем семафор для интегратора
                semaphore.endGenerate();

                // 6. Минимальная пауза для yield (отдаем CPU интегратору)
                if (tasksGenerated % 5 == 0) {
                    Thread.yield(); // Вместо sleep
                }
            }

            // Завершаем генерацию
            task.setGenerationComplete(true);
            System.out.printf("\n✓ Генератор ЗАВЕРШЕН. Всего: %d/%d задач\n",
                    tasksGenerated, taskCount);

        } catch (InterruptedException e) {
            System.out.printf("Генератор ПРЕРВАН на задаче %d/%d\n",
                    tasksGenerated, task.getTaskCount());
        }
    }

    public int getTasksGenerated() {
        return tasksGenerated;
    }
}