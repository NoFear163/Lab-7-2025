import functions.*;
import functions.basic.*;
import functions.tabulated.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ПРОВЕРКА ЗАДАНИЙ ПО ТАБУЛИРОВАННЫМ ФУНКЦИЯМ ===\n");

        try {
            // ============ ПРОВЕРКА 1: ИТЕРАТОРЫ ============
            System.out.println("--- ПРОВЕРКА 1: ИТЕРАТОРЫ TABULATED FUNCTION ---");
            testIterators();

            // ============ ПРОВЕРКА 2: ФАБРИКИ ============
            System.out.println("\n--- ПРОВЕРКА 2: ФАБРИКИ TABULATED FUNCTION ---");
            testFactories();

            // ============ ПРОВЕРКА 3: РЕФЛЕКСИВНОЕ СОЗДАНИЕ ============
            System.out.println("\n--- ПРОВЕРКА 3: РЕФЛЕКСИВНОЕ СОЗДАНИЕ ---");
            testReflection();

            // ============ ПРОВЕРКА 4: ПЕРЕГРУЖЕННЫЕ МЕТОДЫ СЕРИАЛИЗАЦИИ ============
            System.out.println("\n--- ПРОВЕРКА 4: ПЕРЕГРУЖЕННЫЕ МЕТОДЫ СЕРИАЛИЗАЦИИ ---");
            testSerialization();

        } catch (Exception e) {
            System.out.println("\n✗ ОШИБКА ВЫПОЛНЕНИЯ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== ПРОВЕРКА 1: ИТЕРАТОРЫ ====================

    private static void testIterators() {
        System.out.println("\n1. Тест ArrayTabulatedFunction:");
        double[] values1 = {1.0, 4.0, 9.0, 16.0};
        TabulatedFunction arrayFunc = TabulatedFunctions.createTabulatedFunction(0, 3, values1);

        System.out.println("Итерация по ArrayTabulatedFunction:");
        for (FunctionPoint p : arrayFunc) {
            System.out.println(p);
        }

        System.out.println("\n2. Тест LinkedListTabulatedFunction:");
        double[] values2 = {0.5, 1.0, 1.5, 2.0};
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        TabulatedFunction linkedListFunc = TabulatedFunctions.createTabulatedFunction(1, 4, values2);

        System.out.println("Итерация по LinkedListTabulatedFunction:");
        for (FunctionPoint p : linkedListFunc) {
            System.out.println(p);
        }

        TabulatedFunctions.setTabulatedFunctionFactory(
                new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory()
        );
    }

    // ==================== ПРОВЕРКА 2: ФАБРИКИ ====================

    private static void testFactories() {
        Function f = new Cos();
        TabulatedFunction tf;

        System.out.println("\n1. TabulatedFunctions.tabulate(f, 0, Math.PI, 11)");
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        System.out.println("\n2. После установки LinkedListTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, f, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        System.out.println("\n3. После установки ArrayTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(ArrayTabulatedFunction.class, f, 0, Math.PI, 11);
        System.out.println(tf.getClass());
    }

    // ==================== ПРОВЕРКА 3: РЕФЛЕКСИЯ ====================

    private static void testReflection() {
        TabulatedFunction f;

        System.out.println("\n1. TabulatedFunctions.createTabulatedFunction(" + "ArrayTabulatedFunction.class, 0, 10, 3)");
        f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(f.getClass());
        System.out.println(f);

        System.out.println("\n2. TabulatedFunctions.createTabulatedFunction(" + "ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10})");
        f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(f.getClass());
        System.out.println(f);

        System.out.println("\n3. TabulatedFunctions.createTabulatedFunction(" + "LinkedListTabulatedFunction.class, new FunctionPoint[] {...})");
        f = TabulatedFunctions.createTabulatedFunction(LinkedListTabulatedFunction.class, new FunctionPoint[] {new FunctionPoint(0, 0), new FunctionPoint(10, 10)});
        System.out.println(f.getClass());
        System.out.println(f);

        System.out.println("\n4. TabulatedFunctions.tabulate(" + "LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11)");
        f = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println(f.getClass());
        System.out.println(f);
    }

    private static void testSerialization() {

        functions.TabulatedFunctionFactory arrayFactory =
                new functions.tabulated.ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
        functions.TabulatedFunctionFactory linkedFactory =
                new functions.tabulated.LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory();

        try {
            // Создаем тестовые точки
            functions.FunctionPoint[] points = {
                    new functions.FunctionPoint(0.0, 0.0),
                    new functions.FunctionPoint(1.0, 1.0),
                    new functions.FunctionPoint(2.0, 4.0),
                    new functions.FunctionPoint(3.0, 9.0),
                    new functions.FunctionPoint(4.0, 16.0)
            };

            // ============ ТЕСТ 1: СУЩЕСТВУЮЩИЙ МЕТОД ============

            System.out.println("1. ТЕСТ ОБРАТНОЙ СОВМЕСТИМОСТИ:");
            System.out.println("================================================");

            functions.TabulatedFunction arrayFunc = new functions.tabulated.ArrayTabulatedFunction(points);

            java.io.StringWriter sw1 = new java.io.StringWriter();
            functions.tabulated.TabulatedFunctions.writeTabulatedFunction(arrayFunc, sw1);
            String serialized1 = sw1.toString().trim();
            System.out.println("Записано (старый формат): " + serialized1);

            java.io.StringReader sr1 = new java.io.StringReader(serialized1);
            functions.TabulatedFunction restored1 = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr1);

            System.out.println("Восстановлен класс: " + restored1.getClass().getSimpleName());
            System.out.println("Функции равны: " + arrayFunc.equals(restored1));
            System.out.println("Количество точек: " + restored1.getPointsCount());

            // ============ ТЕСТ 2: С ФАБРИКОЙ ============
            System.out.println("\n2. ТЕСТ С ФАБРИКОЙ:");
            System.out.println("==================");

            functions.TabulatedFunction linkedFunc = linkedFactory.createTabulatedFunction(points);

            java.io.StringWriter sw2 = new java.io.StringWriter();
            functions.tabulated.TabulatedFunctions.writeTabulatedFunction(linkedFunc, sw2, linkedFactory);
            String serialized2 = sw2.toString().trim();
            System.out.println("Записано (с фабрикой): " + serialized2);

            java.io.StringReader sr2 = new java.io.StringReader(serialized2);
            functions.TabulatedFunction restored2 = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr2, linkedFactory);

            System.out.println("Восстановлен класс: " + restored2.getClass().getSimpleName());
            System.out.println("Функции равны: " + linkedFunc.equals(restored2));

            if (restored2 instanceof functions.tabulated.LinkedListTabulatedFunction) {
                System.out.println("✓ Это действительно LinkedListTabulatedFunction!");
            }

            // ============ ТЕСТ 3: С УКАЗАНИЕМ КЛАССА (РЕФЛЕКСИЯ) ============
            System.out.println("\n3. Array С УКАЗАНИЕМ КЛАССА:");
            System.out.println("========================================");

            functions.TabulatedFunction arrayFunc2 = new functions.tabulated.ArrayTabulatedFunction(points);

            java.io.StringWriter sw3 = new java.io.StringWriter();
            functions.tabulated.TabulatedFunctions.writeTabulatedFunction(arrayFunc2, sw3, functions.tabulated.ArrayTabulatedFunction.class);
            String serialized3 = sw3.toString().trim();
            System.out.println("Записано (с классом): " + serialized3);

            if (serialized3.startsWith("functions.tabulated.ArrayTabulatedFunction")) {
                System.out.println("✓ Формат содержит полное имя класса!");
            }

            java.io.StringReader sr3 = new java.io.StringReader(serialized3);
            functions.TabulatedFunction restored3 = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr3, functions.tabulated.ArrayTabulatedFunction.class);

            System.out.println("Восстановлен класс: " + restored3.getClass().getName());
            System.out.println("Функции равны: " + arrayFunc2.equals(restored3));

            // ============ ТЕСТ 4: LinkedList С УКАЗАНИЕМ КЛАССА ============
            System.out.println("\n4. LinkedList С УКАЗАНИЕМ КЛАССА:");
            System.out.println("==================================");

            functions.TabulatedFunction linkedFunc2 = new functions.tabulated.LinkedListTabulatedFunction(points);

            java.io.StringWriter sw4 = new java.io.StringWriter();
            functions.tabulated.TabulatedFunctions.writeTabulatedFunction(linkedFunc2, sw4, functions.tabulated.LinkedListTabulatedFunction.class);
            String serialized4 = sw4.toString().trim();
            System.out.println("Записано LinkedList (с классом): " + serialized4);

            if (serialized4.startsWith("functions.tabulated.LinkedListTabulatedFunction")) {
                System.out.println("✓ Формат содержит полное имя класса LinkedListTabulatedFunction!");
            }

            java.io.StringReader sr4 = new java.io.StringReader(serialized4);
            functions.TabulatedFunction restored4 = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr4, functions.tabulated.LinkedListTabulatedFunction.class);

            System.out.println("Восстановлен класс: " + restored4.getClass().getName());
            System.out.println("Функции равны: " + linkedFunc2.equals(restored4));

            // ============ ТЕСТ 5: ПРОВЕРКА ОСОБЕННОСТЕЙ LinkedList ============
            System.out.println("\n5. ПРОВЕРКА ОСОБЕННОСТЕЙ LinkedList:");
            System.out.println("====================================");

            // Проверяем, что это действительно разные реализации
            System.out.println("ArrayTabulatedFunction toString(): " + arrayFunc2.toString());
            System.out.println("LinkedListTabulatedFunction toString(): " + linkedFunc2.toString());

            // Проверяем метод getFunctionValue
            System.out.println("\nПроверка интерполяции:");
            double testX = 2.5;
            System.out.printf("ArrayTabulatedFunction.getFunctionValue(%.1f) = %.4f%n", testX, arrayFunc2.getFunctionValue(testX));
            System.out.printf("LinkedListTabulatedFunction.getFunctionValue(%.1f) = %.4f%n", testX, linkedFunc2.getFunctionValue(testX));

            // Проверяем, что значения совпадают
            double arrayValue = arrayFunc2.getFunctionValue(testX);
            double linkedValue = linkedFunc2.getFunctionValue(testX);
            System.out.printf("Значения %s (должны быть равны)%n", Math.abs(arrayValue - linkedValue) < 0.0001 ? "равны ✓" : "различаются ✗");

            // ============ ТЕСТ 6: ПРОВЕРКА СОДЕРЖИМОГО ============
            System.out.println("\n6. ПРОВЕРКА СОДЕРЖИМОГО:");
            System.out.println("========================");

            System.out.println("Исходная функция (Array):");
            for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
                System.out.printf("  [%d] (%.1f, %.1f)%n", i, arrayFunc.getPointX(i), arrayFunc.getPointY(i));
            }

            System.out.println("\nВосстановленная функция (Array):");
            for (int i = 0; i < restored1.getPointsCount(); i++) {
                System.out.printf("  [%d] (%.1f, %.1f)%n", i, restored1.getPointX(i), restored1.getPointY(i));
            }

            System.out.println("\nИсходная функция (LinkedList):");
            for (int i = 0; i < linkedFunc2.getPointsCount(); i++) {
                System.out.printf("  [%d] (%.1f, %.1f)%n", i, linkedFunc2.getPointX(i), linkedFunc2.getPointY(i));
            }

            System.out.println("\nВосстановленная функция (LinkedList):");
            for (int i = 0; i < restored4.getPointsCount(); i++) {
                System.out.printf("  [%d] (%.1f, %.1f)%n", i, restored4.getPointX(i), restored4.getPointY(i));
            }

            // ============ ТЕСТ 7: ТЕСТ РАЗНЫХ ФАБРИК ============
            System.out.println("\n7. ТЕСТ РАЗНЫХ ФАБРИК:");
            System.out.println("=======================================");

            System.out.print("1. Array → Array фабрика: ");
            try {
                java.io.StringWriter sw5 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(arrayFunc, sw5, arrayFactory);
                java.io.StringReader sr5 = new java.io.StringReader(sw5.toString());
                functions.TabulatedFunction restoredArray = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr5, arrayFactory);

                boolean equalsResult = arrayFunc.equals(restoredArray);
                boolean correctType = restoredArray instanceof functions.tabulated.ArrayTabulatedFunction;

                if (equalsResult && correctType) {
                    System.out.println("✓ Успешно");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            System.out.print("2. LinkedList → LinkedList фабрика: ");
            try {
                java.io.StringWriter sw6 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(linkedFunc, sw6, linkedFactory);
                java.io.StringReader sr6 = new java.io.StringReader(sw6.toString());
                functions.TabulatedFunction restoredLinkedList = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr6, linkedFactory);

                boolean equalsResult = linkedFunc.equals(restoredLinkedList);
                boolean correctType = restoredLinkedList instanceof functions.tabulated.LinkedListTabulatedFunction;

                if (equalsResult && correctType) {
                    System.out.println("✓ Успешно");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            System.out.print("3. Array → LinkedList фабрика: ");
            try {
                java.io.StringWriter sw7 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(arrayFunc, sw7, arrayFactory);
                String arrayData = sw7.toString();

                java.io.StringReader sr7 = new java.io.StringReader(arrayData);
                functions.TabulatedFunction restoredAsLinkedList = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr7, linkedFactory);

                boolean dataPreserved = arrayFunc.equals(restoredAsLinkedList);
                boolean correctType = restoredAsLinkedList instanceof functions.tabulated.LinkedListTabulatedFunction;

                if (dataPreserved && correctType) {
                    System.out.println("✓ Успешно (преобразовано в LinkedList)");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            System.out.print("4. LinkedList → Array фабрика: ");
            try {
                java.io.StringWriter sw8 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(linkedFunc, sw8, linkedFactory);
                String linkedListData = sw8.toString();

                java.io.StringReader sr8 = new java.io.StringReader(linkedListData);
                functions.TabulatedFunction restoredAsArray = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr8, arrayFactory);

                boolean dataPreserved = linkedFunc.equals(restoredAsArray);
                boolean correctType = restoredAsArray instanceof functions.tabulated.ArrayTabulatedFunction;

                if (dataPreserved && correctType) {
                    System.out.println("✓ Успешно (преобразовано в Array)");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            System.out.print("5. LinkedList → запись через Array фабрику → чтение через Array фабрику: ");
            try {
                java.io.StringWriter sw9 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(linkedFunc, sw9, arrayFactory);

                java.io.StringReader sr9 = new java.io.StringReader(sw9.toString());
                functions.TabulatedFunction restoredDirect = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr9, arrayFactory);

                boolean dataPreserved = linkedFunc.equals(restoredDirect);
                boolean correctType = restoredDirect instanceof functions.tabulated.ArrayTabulatedFunction;

                if (dataPreserved && correctType) {
                    System.out.println("✓ Успешно");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            System.out.print("6. Array → запись через LinkedList фабрику → чтение через LinkedList фабрику: ");
            try {
                java.io.StringWriter sw10 = new java.io.StringWriter();
                functions.tabulated.TabulatedFunctions.writeTabulatedFunction(arrayFunc, sw10, linkedFactory);

                java.io.StringReader sr10 = new java.io.StringReader(sw10.toString());
                functions.TabulatedFunction restoredDirect2 = functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr10, linkedFactory);

                boolean dataPreserved = arrayFunc.equals(restoredDirect2);
                boolean correctType = restoredDirect2 instanceof functions.tabulated.LinkedListTabulatedFunction;

                if (dataPreserved && correctType) {
                    System.out.println("✓ Успешно");
                } else {
                    System.out.println("✗ Ошибка");
                }
            } catch (Exception e) {
                System.out.println("✗ Исключение: " + e.getMessage());
            }

            // ============ ТЕСТ 8: ПРОВЕРКА НЕСОВМЕСТИМЫХ ФОРМАТОВ ============
            System.out.println("\n8. ПРОВЕРКА НЕСОВМЕСТИМЫХ ФОРМАТОВ:");
            System.out.println("===================================");

            try {
                java.io.StringReader sr11 = new java.io.StringReader(serialized4);
                functions.tabulated.TabulatedFunctions.readTabulatedFunction(sr11, arrayFactory);
                System.out.println("✗ ОШИБКА: Должна была быть ошибка!");
            } catch (java.io.IOException e) {
                System.out.println("✓ Ожидаемая ошибка при несовместимом формате: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("\n✗ ОШИБКА В ТЕСТЕ: " + e.getMessage());
            e.printStackTrace();
        }
    }}