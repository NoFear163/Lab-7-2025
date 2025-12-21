package functions.meta;

import functions.Function;

public class Mult implements Function {
    private Function f1, f2;

    public Mult(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    @Override
    public double getLeftDomainBorder() {
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }

    @Override
    public double getRightDomainBorder() {
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double val1 = f1.getFunctionValue(x);
        double val2 = f2.getFunctionValue(x);

        if (Double.isNaN(val1) || Double.isNaN(val2)) {
            return Double.NaN;
        }

        return val1 * val2;
    }
}