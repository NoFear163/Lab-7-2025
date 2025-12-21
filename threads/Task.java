package threads;

import functions.Function;


public class Task {
    private Function function;      // Интегрируемая функция
    private double leftBound;       // Левая граница интегрирования
    private double rightBound;      // Правая граница интегрирования
    private double step;            // Шаг дискретизации
    private int taskCount;          // Количество выполняемых заданий
    private volatile boolean generationComplete = false;

    // Конструктор
    public Task(Function function, double leftBound, double rightBound,
                double step, int taskCount) {
        this.function = function;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.step = step;
        this.taskCount = taskCount;
    }

    // Конструктор по умолчанию
    public Task() {
        this(null, 0, 0, 0, 0);
    }

    // Геттеры и сеттеры
    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(double leftBound) {
        this.leftBound = leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }

    public void setRightBound(double rightBound) {
        this.rightBound = rightBound;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public void setGenerationComplete(boolean complete) {
        this.generationComplete = complete;
    }

    public boolean isGenerationComplete() {
        return generationComplete;
    }

    public boolean isReady() {
        return function != null;
    }

    public void reset() {
        function = null;
        leftBound = 0;
        rightBound = 0;
        step = 0;
    }

    public Task copy() {
        return new Task(function, leftBound, rightBound, step, taskCount);
    }

    public String getInfo() {
        if (function == null) {
            return "Task{NOT READY}";
        }
        return String.format("Task{left=%.2f, right=%.2f, step=%.4f}",
                leftBound, rightBound, step);
    }

    @Override
    public String toString() {
        return String.format("Task{function=%s, left=%.2f, right=%.2f, step=%.4f, count=%d}",
                function != null ? function.getClass().getSimpleName() : "null",
                leftBound, rightBound, step, taskCount);
    }
}