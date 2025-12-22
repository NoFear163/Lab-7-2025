package functions.tabulated;

import functions.*;

import java.io.*;
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

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out)
            throws IOException {

        PrintWriter writer = new PrintWriter(out);
        writer.print(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.println();
        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in)
            throws IOException {

        StreamTokenizer tokenizer = createTokenizer(in);

        tokenizer.nextToken();
        if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
            throw new IOException("Ожидалось количество точек");
        }

        int pointsCount;
        try {
            pointsCount = Integer.parseInt(tokenizer.sval);
        } catch (NumberFormatException e) {
            throw new IOException("Некорректное количество точек: " + tokenizer.sval);
        }

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение X для точки " + (i+1));
            }

            double x;
            try {
                x = Double.parseDouble(tokenizer.sval);
            } catch (NumberFormatException e) {
                throw new IOException("Некорректное значение X: " + tokenizer.sval);
            }

            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение Y для точки " + (i+1));
            }

            double y;
            try {
                y = Double.parseDouble(tokenizer.sval);
            } catch (NumberFormatException e) {
                throw new IOException("Некорректное значение Y: " + tokenizer.sval);
            }

            points[i] = new FunctionPoint(x, y);
        }

        for (int i = 1; i < pointsCount; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        return new ArrayTabulatedFunction(points);
    }

    // ==================== ПЕРЕГРУЖЕННЫЕ МЕТОДЫ СЕРИАЛИЗАЦИИ ====================

    public static TabulatedFunction readTabulatedFunction(Reader in, TabulatedFunctionFactory factory)
            throws IOException {

        StreamTokenizer tokenizer = createTokenizer(in);

        tokenizer.nextToken();
        if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
            throw new IOException("Ожидалось количество точек");
        }

        int pointsCount;
        try {
            pointsCount = Integer.parseInt(tokenizer.sval);
        } catch (NumberFormatException e) {
            throw new IOException("Некорректное количество точек: " + tokenizer.sval);
        }

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение X для точки " + (i+1));
            }

            double x = parseDoubleSafe(tokenizer.sval, "X");

            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение Y для точки " + (i+1));
            }

            double y = parseDoubleSafe(tokenizer.sval, "Y");

            points[i] = new FunctionPoint(x, y);
        }

        for (int i = 1; i < pointsCount; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        return factory.createTabulatedFunction(points);
    }

    public static TabulatedFunction readTabulatedFunction(Reader in, Class<? extends TabulatedFunction> clazz)
            throws IOException {

        StreamTokenizer tokenizer = createTokenizer(in);

        tokenizer.nextToken(); // пропускаем имя класса
        tokenizer.nextToken(); // Читаем количество точек
        if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
            throw new IOException("Ожидалось количество точек");
        }

        int pointsCount;
        try {
            pointsCount = Integer.parseInt(tokenizer.sval);
        } catch (NumberFormatException e) {
            throw new IOException("Некорректное количество точек: " + tokenizer.sval);
        }

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение X для точки " + (i+1));
            }

            double x = parseDoubleSafe(tokenizer.sval, "X");

            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new IOException("Ожидалось значение Y для точки " + (i+1));
            }

            double y = parseDoubleSafe(tokenizer.sval, "Y");

            points[i] = new FunctionPoint(x, y);
        }

        for (int i = 1; i < pointsCount; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        try {
            Constructor<? extends TabulatedFunction> constructor =
                    clazz.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object) points);
        } catch (Exception e) {
            throw new IOException("Не удалось создать экземпляр класса " + clazz.getName(), e);
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out,
                                              TabulatedFunctionFactory factory) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        writer.print(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.println();
        writer.flush();
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out,
                                              Class<? extends TabulatedFunction> clazz)
            throws IOException {
        PrintWriter writer = new PrintWriter(out);

        writer.print(clazz.getName() + " ");
        writer.print(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.println();
        writer.flush();
    }

// ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private static StreamTokenizer createTokenizer(Reader in) {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.resetSyntax();
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('-', '-');
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('$', '$');
        tokenizer.wordChars('[', '[');
        tokenizer.wordChars(']', ']');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.whitespaceChars('\r', '\r');
        return tokenizer;
    }

    private static double parseDoubleSafe(String value, String fieldName) throws IOException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IOException("Некорректное значение " + fieldName + ": " + value);
        }
    }

}