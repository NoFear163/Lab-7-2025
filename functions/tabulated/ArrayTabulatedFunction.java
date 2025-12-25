package functions.tabulated;

import functions.*;
import java.io.Serializable;
import java.util.Iterator;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 2L;
    private FunctionPoint[] points;
    private int pointsCount;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = pointsCount;
        points = new FunctionPoint[pointsCount + 10];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + i * step, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);
        for (int i = 0; i < values.length; i++) {
            points[i].setY(values[i]);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int i = 0; i < pointsCount; i++) {
            if (i > 0) sb.append(", ");
            sb.append(points[i].toString());
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction other = (TabulatedFunction) o;

        // Оптимизация: если количество точек разное, сразу false
        if (this.pointsCount != other.getPointsCount()) return false;

        // Оптимизация для сравнения двух ArrayTabulatedFunction
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction otherArray = (ArrayTabulatedFunction) o;

            // Быстрое сравнение массивов одинакового размера
            for (int i = 0; i < pointsCount; i++) {
                // Делегируем сравнение методу equals класса FunctionPoint
                if (!this.points[i].equals(otherArray.points[i])) {
                    return false;
                }
            }
            return true;
        }

        // Общий случай для любого TabulatedFunction
        for (int i = 0; i < pointsCount; i++) {
            // Делегируем сравнение методу equals класса FunctionPoint
            if (!this.getPoint(i).equals(other.getPoint(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = pointsCount; // Начинаем с количества точек

        // XOR хэш-кодов всех точек
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();
        }

        return hash;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            ArrayTabulatedFunction cloned = (ArrayTabulatedFunction) super.clone();

            // Глубокое копирование массива точек
            cloned.points = new FunctionPoint[this.points.length];
            for (int i = 0; i < this.pointsCount; i++) {
                cloned.points[i] = new FunctionPoint(this.points[i]);
            }

            // Копирование оставшихся null-элементов
            for (int i = this.pointsCount; i < this.points.length; i++) {
                cloned.points[i] = null;
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        final double eps = 1e-10;
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - x) <= eps) {
                return points[i].getY();
            }
        }

        int i = 0;
        while (i < pointsCount && points[i].getX() < x) i++;

        if (i == 0) return points[0].getY();
        if (i == pointsCount) return points[pointsCount - 1].getY();

        FunctionPoint left = points[i - 1];
        FunctionPoint right = points[i];
        return left.getY() + (right.getY() - left.getY()) * (x - left.getX()) / (right.getX() - left.getX());
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]"
            );
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point)
            throws InappropriateFunctionPointException {

        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if ((index > 0 && point.getX() <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && point.getX() >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x)
            throws InappropriateFunctionPointException {

        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        points[index].setX(x);
    }

    @Override
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: останется меньше 2 точек");
        }

        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    @Override
    public void addPoint(FunctionPoint point)
            throws InappropriateFunctionPointException {

        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
        }

        int i = 0;
        while (i < pointsCount && points[i].getX() < point.getX()) i++;

        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        System.arraycopy(points, i, points, i + 1, pointsCount - i);
        points[i] = new FunctionPoint(point);
        pointsCount++;
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("Следующая точка не существует");
                }
                return new FunctionPoint(points[currentIndex++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Операция удаления не поддерживается");
            }
        };
    }

    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }

    public void printPoints() {
        System.out.println("Массив точек (всего " + pointsCount + "):");
        for (int i = 0; i < pointsCount; i++) {
            System.out.printf("[%d]: [%.2f; %.2f]\n",
                    i, points[i].getX(), points[i].getY());
        }
    }
}