package functions;
import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint() {
        this(0, 0);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%.2f; %.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;

        final double EPS = 1e-10;
        return Math.abs(x - that.x) < EPS && Math.abs(y - that.y) < EPS;
    }

    @Override
    public int hashCode() {
        long xScaled = Math.round(x * 1e8);
        long yScaled = Math.round(y * 1e8);

        int result = Long.hashCode(xScaled);
        result = 31 * result + Long.hashCode(yScaled);
        return result;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public FunctionPoint copy() {
        return new FunctionPoint(this);
    }
}