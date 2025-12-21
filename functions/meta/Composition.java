package functions.meta;

import functions.Function;

public class Composition implements Function {
    private Function outer, inner;

    public Composition(Function outer, Function inner) {
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public double getLeftDomainBorder() {
        return inner.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return inner.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double innerValue = inner.getFunctionValue(x);

        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }

        if (innerValue < outer.getLeftDomainBorder() ||
                innerValue > outer.getRightDomainBorder()) {
            return Double.NaN;
        }

        return outer.getFunctionValue(innerValue);
    }
}