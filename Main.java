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
}