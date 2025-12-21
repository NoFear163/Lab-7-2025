package functions;

import functions.meta.*;

public final class Functions {

    private Functions() {
        throw new UnsupportedOperationException("Нельзя создавать экземпляры утилитного класса");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new functions.meta.Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new functions.meta.Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new functions.meta.Mult(f1, f2);
    }

    public static Function composition(Function outer, Function inner) {
        return new functions.meta.Composition(outer, inner);
    }

    public static Function opposite(Function f) {
        return new functions.meta.Scale(f, 1, -1);
    }

    public static Function inverse(Function f) {
        return new functions.meta.Scale(f, -1, 1);
    }

    public static double integrate(Function f, double leftBound, double rightBound, double step) {
        // Проверка корректности границ
        if (leftBound >= rightBound) {
            throw new IllegalArgumentException(
                    String.format("Левая граница (%.6f) должна быть меньше правой (%.6f)",
                            leftBound, rightBound)
            );
        }

        // Проверка шага
        if (step <= 0) {
            throw new IllegalArgumentException(
                    String.format("Шаг дискретизации (%.6f) должен быть положительным", step)
            );
        }

        // Проверка области определения
        if (leftBound < f.getLeftDomainBorder()) {
            throw new IllegalArgumentException(
                    String.format("Левая граница (%.6f) меньше области определения функции (%.6f)",
                            leftBound, f.getLeftDomainBorder())
            );
        }

        if (rightBound > f.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                    String.format("Правая граница (%.6f) больше области определения функции (%.6f)",
                            rightBound, f.getRightDomainBorder())
            );
        }

        // Проверка, что функция определена на всем интервале
        // (для логарифма левая граница должна быть > 0)
        double testValue = f.getFunctionValue(leftBound);
        if (Double.isNaN(testValue)) {
            throw new IllegalArgumentException(
                    String.format("Функция не определена в левой границе x=%.6f", leftBound)
            );
        }

        testValue = f.getFunctionValue(rightBound);
        if (Double.isNaN(testValue)) {
            throw new IllegalArgumentException(
                    String.format("Функция не определена в правой границе x=%.6f", rightBound)
            );
        }

        double integral = 0.0;
        double currentX = leftBound;
        final double EPSILON = 1e-12;

        // Основной цикл интегрирования
        while (currentX < rightBound - EPSILON) {
            double nextX = Math.min(currentX + step, rightBound);

            double fCurrent = f.getFunctionValue(currentX);
            double fNext = f.getFunctionValue(nextX);

            // Проверка на корректность значений функции
            if (Double.isNaN(fCurrent)) {
                throw new IllegalArgumentException(
                        String.format("Функция не определена в точке x=%.6f", currentX)
                );
            }
            if (Double.isNaN(fNext)) {
                throw new IllegalArgumentException(
                        String.format("Функция не определена в точке x=%.6f", nextX)
                );
            }

            // Площадь трапеции
            double trapezoidArea = (fCurrent + fNext) * (nextX - currentX) / 2.0;
            integral += trapezoidArea;

            currentX = nextX;
        }

        return integral;
    }
}