package functions.tabulated;

import functions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class TabulatedFunctions {
    // Фабрика для создания табулированных функций
    private static TabulatedFunctionFactory factory =
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    private TabulatedFunctions() {
        throw new UnsupportedOperationException("Нельзя создавать экземпляры утилитного класса");
    }

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory newFactory) {
        if (newFactory == null) {
            throw new IllegalArgumentException("Фабрика не может быть null");
        }
        factory = newFactory;
    }

    // ==================== МЕТОДЫ РЕФЛЕКСИИ ====================
    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> clazz,
            double leftX, double rightX, int pointsCount) {

        validateClass(clazz);

        try {
            Constructor<? extends TabulatedFunction> constructor =
                    clazz.getConstructor(double.class, double.class, int.class);
            return constructor.newInstance(leftX, rightX, pointsCount);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getSimpleName() + " не имеет конструктора (double, double, int)", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Не удалось создать экземпляр класса " + clazz.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new IllegalArgumentException("Ошибка в конструкторе", cause);
            }
        }
    }

    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> clazz,
            double leftX, double rightX, double[] values) {

        validateClass(clazz);

        try {
            Constructor<? extends TabulatedFunction> constructor =
                    clazz.getConstructor(double.class, double.class, double[].class);
            return constructor.newInstance(leftX, rightX, values);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getSimpleName() + " не имеет конструктора (double, double, double[])", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Не удалось создать экземпляр класса " + clazz.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new IllegalArgumentException("Ошибка в конструкторе", cause);
            }
        }
    }

    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> clazz,
            FunctionPoint[] points) {

        validateClass(clazz);

        try {
            Constructor<? extends TabulatedFunction> constructor =
                    clazz.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object) points);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getSimpleName() + " не имеет конструктора (FunctionPoint[])", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Не удалось создать экземпляр класса " + clazz.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new IllegalArgumentException("Ошибка в конструкторе", cause);
            }
        }
    }

    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> clazz, Function f, double leftX, double rightX, int pointsCount) {

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (leftX < f.getLeftDomainBorder() || rightX > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал выходит за границы области определения функции");
        }

        // Используем рефлексивный метод создания
        TabulatedFunction tabulatedFunc = createTabulatedFunction(clazz, leftX, rightX, pointsCount);

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = f.getFunctionValue(x);
            if (Double.isNaN(y)) {
                throw new IllegalArgumentException("Функция не определена в точке x=" + x);
            }
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }

    public static TabulatedFunction tabulate(
            Function f, double leftX, double rightX, int pointsCount) {

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (leftX < f.getLeftDomainBorder() || rightX > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал выходит за границы области определения функции");
        }

        // Используем фабрику для создания
        TabulatedFunction tabulatedFunc = createTabulatedFunction(leftX, rightX, pointsCount);

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = f.getFunctionValue(x);
            if (Double.isNaN(y)) {
                throw new IllegalArgumentException("Функция не определена в точке x=" + x);
            }
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }

    private static void validateClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Класс не может быть null");
        }

        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Класс " + clazz.getSimpleName() + " не реализует интерфейс TabulatedFunction");
        }
    }

    // ==================== ОСНОВНЫЕ МЕТОДЫ ФАБРИКИ ====================

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        return factory.createTabulatedFunction(points);
    }
}