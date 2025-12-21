package functions.basic;

public class Tan extends TrigonometricFunction {

    @Override
    public double getFunctionValue(double x) {
        double remainder = Math.abs(x % Math.PI);
        if (Math.abs(remainder - Math.PI/2) < 1e-10) {
            return Double.NaN;
        }
        return Math.tan(x);
    }
}