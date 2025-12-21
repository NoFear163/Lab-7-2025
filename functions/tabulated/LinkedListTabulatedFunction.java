package functions.tabulated;

import functions.*;
import java.io.*;
import java.util.Iterator;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable, Cloneable {

    protected class FunctionNode implements Serializable {
        private static final long serialVersionUID = 3L;
        public FunctionPoint point;
        public FunctionNode prev;
        public FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }
    }

    private transient FunctionNode head;
    private transient FunctionNode lastAccessed;
    private transient int lastIndex;
    private int pointsCount;

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;

        this.pointsCount = 0;
        this.lastAccessed = null;
        this.lastIndex = -1;

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            FunctionPoint point = new FunctionPoint(leftX + i * step, 0);
            addNodeToTail().point = point;
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);

        FunctionNode curr = head.next;
        for (int i = 0; i < values.length; i++) {
            curr.point.setY(values[i]);
            curr = curr.next;
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;

        this.pointsCount = 0;
        this.lastAccessed = null;
        this.lastIndex = -1;

        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
    }

    public LinkedListTabulatedFunction() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;

        this.pointsCount = 0;
        this.lastAccessed = null;
        this.lastIndex = -1;
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }

        if (lastAccessed != null && Math.abs(index - lastIndex) <= 1) {
            if (index == lastIndex) return lastAccessed;
            if (index == lastIndex + 1) {
                lastAccessed = lastAccessed.next;
                lastIndex = index;
                return lastAccessed;
            }
            if (index == lastIndex - 1) {
                lastAccessed = lastAccessed.prev;
                lastIndex = index;
                return lastAccessed;
            }
        }

        if (index == 0) {
            lastAccessed = head.next;
            lastIndex = 0;
            return lastAccessed;
        }
        if (index == pointsCount - 1) {
            lastAccessed = head.prev;
            lastIndex = pointsCount - 1;
            return lastAccessed;
        }

        FunctionNode current;
        int startIndex;
        if (lastAccessed != null && Math.abs(index - lastIndex) < pointsCount / 2) {
            current = lastAccessed;
            startIndex = lastIndex;
        } else {
            current = head.next;
            startIndex = 0;
        }

        if (index > startIndex) {
            for (int i = startIndex; i < index; i++) {
                current = current.next;
            }
        } else {
            for (int i = startIndex; i > index; i--) {
                current = current.prev;
            }
        }

        lastAccessed = current;
        lastIndex = index;

        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.prev, head);
        head.prev.next = newNode;
        head.prev = newNode;
        pointsCount++;
        lastAccessed = null;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне границ [0, " + pointsCount + "]"
            );
        }

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null, nodeAtIndex.prev, nodeAtIndex);

        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;

        pointsCount++;
        lastAccessed = null;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: останется меньше 2 точек");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);

        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;

        pointsCount--;
        lastAccessed = null;

        return nodeToDelete;
    }

    @Override
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        if (x < leftBorder || x > rightBorder) {
            return Double.NaN;
        }

        if (Math.abs(x - leftBorder) < 1e-10) {
            return head.next.point.getY();
        }
        if (Math.abs(x - rightBorder) < 1e-10) {
            return head.prev.point.getY();
        }

        FunctionNode current = head.next;
        while (current != head && current.point.getX() < x) {
            current = current.next;
        }

        if (Math.abs(current.point.getX() - x) < 1e-10) {
            return current.point.getY();
        }

        FunctionPoint left = current.prev.point;
        FunctionPoint right = current.point;
        return left.getY() + (right.getY() - left.getY()) *
                (x - left.getX()) / (right.getX() - left.getX());
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point)
            throws InappropriateFunctionPointException {

        FunctionNode node = getNodeByIndex(index);

        if ((index > 0 && point.getX() <= node.prev.point.getX()) ||
                (index < pointsCount - 1 && point.getX() >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x)
            throws InappropriateFunctionPointException {

        FunctionNode node = getNodeByIndex(index);

        if ((index > 0 && x <= node.prev.point.getX()) ||
                (index < pointsCount - 1 && x >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point)
            throws InappropriateFunctionPointException {

        if (point.getX() < getLeftDomainBorder()) {
            FunctionNode newNode = new FunctionNode(new FunctionPoint(point), head, head.next);
            head.next.prev = newNode;
            head.next = newNode;
            pointsCount++;
            lastAccessed = null;
            return;
        }

        if (point.getX() > getRightDomainBorder()) {
            FunctionNode newNode = new FunctionNode(new FunctionPoint(point), head.prev, head);
            head.prev.next = newNode;
            head.prev = newNode;
            pointsCount++;
            lastAccessed = null;
            return;
        }

        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
            if (current.point.getX() > point.getX()) {
                break;
            }
            current = current.next;
        }

        FunctionNode newNode = new FunctionNode(new FunctionPoint(point), current.prev, current);
        current.prev.next = newNode;
        current.prev = newNode;
        pointsCount++;
        lastAccessed = null;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);

        FunctionNode current = head.next;
        while (current != head) {
            out.writeDouble(current.point.getX());
            out.writeDouble(current.point.getY());
            current = current.next;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();

        if (head == null) {
            head = new FunctionNode(null);
            head.next = head;
            head.prev = head;
        } else {
            while (head.next != head) {
                FunctionNode toRemove = head.next;
                head.next = toRemove.next;
                toRemove.next.prev = head;
            }
        }

        lastAccessed = null;
        lastIndex = -1;

        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            FunctionPoint point = new FunctionPoint(x, y);

            FunctionNode newNode = new FunctionNode(point, head.prev, head);
            head.prev.next = newNode;
            head.prev = newNode;
        }
    }

    public FunctionPoint[] getPointsRange(int startIndex, int count) {
        if (startIndex < 0 || startIndex + count > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionPoint[] result = new FunctionPoint[count];
        FunctionNode current = getNodeByIndex(startIndex);

        for (int i = 0; i < count; i++) {
            result[i] = new FunctionPoint(current.point);
            current = current.next;
        }

        return result;
    }

    public void printPoints() {
        System.out.println("Связный список точек (всего " + pointsCount + "):");
        FunctionNode current = head.next;
        int i = 0;
        while (current != head) {
            System.out.printf("[%d]: [%.2f; %.2f]\n",
                    i++, current.point.getX(), current.point.getY());
            current = current.next;
        }
    }

    // ==================== ПЕРЕОПРЕДЕЛЕННЫЕ МЕТОДЫ Object ====================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        FunctionNode curr = head.next;
        boolean first = true;
        while (curr != head) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(curr.point.toString());
            curr = curr.next;
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        // Оптимизация для сравнения двух LinkedListTabulatedFunction
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;

            // Быстрая проверка количества точек
            if (this.pointsCount != other.pointsCount) return false;

            // Прямое сравнение узлов списка
            FunctionNode thisCurr = this.head.next;
            FunctionNode otherCurr = other.head.next;

            while (thisCurr != this.head && otherCurr != other.head) {
                // Делегируем сравнение методу equals класса FunctionPoint
                if (!thisCurr.point.equals(otherCurr.point)) {
                    return false;
                }
                thisCurr = thisCurr.next;
                otherCurr = otherCurr.next;
            }

            // Проверяем, что оба списка закончились одновременно
            return thisCurr == this.head && otherCurr == other.head;
        }

        // Общий случай для любого TabulatedFunction
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction other = (TabulatedFunction) o;

        // Проверка количества точек
        if (this.pointsCount != other.getPointsCount()) return false;

        // Делегируем сравнение методу equals класса FunctionPoint
        for (int i = 0; i < pointsCount; i++) {
            if (!this.getPoint(i).equals(other.getPoint(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = pointsCount;

        // XOR хэш-кодов всех точек
        FunctionNode curr = head.next;
        while (curr != head) {
            hash ^= curr.point.hashCode();
            curr = curr.next;
        }

        return hash;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {

            // Создаем массив точек из текущего списка (глубокое копирование точек)
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            FunctionNode curr = head.next;
            int i = 0;
            while (curr != head) {
                points[i++] = new FunctionPoint(curr.point);
                curr = curr.next;
            }

            LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction(points);

            cloned.lastAccessed = null;
            cloned.lastIndex = -1;

            return cloned;
        } catch (Exception e) {
            throw new CloneNotSupportedException("Ошибка при клонировании: " + e.getMessage());
        }
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next;
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentNode != head;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more points in tabulated function");
                }
                FunctionPoint point = new FunctionPoint(currentNode.point);
                currentNode = currentNode.next;
                currentIndex++;
                return point;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }

}