package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function f;
    private double scaleX, scaleY;

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        if (scaleX > 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getRightDomainBorder() / scaleX;
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public double getRightDomainBorder() {
        if (scaleX > 0) {
            return f.getRightDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double scaledX = x * scaleX;
        double val = f.getFunctionValue(scaledX);

        if (Double.isNaN(val)) {
            return Double.NaN;
        }

        return val * scaleY;
    }
}