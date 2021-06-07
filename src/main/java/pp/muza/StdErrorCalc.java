package pp.muza;

public final class StdErrorCalc {

    private static final int DEPTH = 4;

    private final int items;
    private final double[][] measures;
    private int depthPtr = 0;
    private double errorSum = 0.0;
    private long reports = 0;

    public StdErrorCalc(final int items) {
        if (items <= 0) {
            throw new IllegalArgumentException("Value " + items + " should be greater than 0");
        }
        this.items = items;
        measures = new double[items][DEPTH];
    }

    private static double standardDeviation(final double[] numArray) {
        double s = 0.0, sd = 0.0;
        int length = numArray.length;
        for (double num : numArray) {
            s += num;
        }
        double mean = s / length;
        for (double num : numArray) {
            sd += Math.pow(num - mean, 2);
        }
        return Math.sqrt(sd / (length - 1));
    }

    public boolean isReady() {
        return reports >= DEPTH;
    }

    public double report(final double[] numArray) {
        reports++;
        if (depthPtr >= DEPTH) {
            depthPtr = 0;
        }
        for (int i = 0; i < items && i < numArray.length; i++) {
            measures[i][depthPtr] = numArray[i];
        }
        double s = 0.0;
        for (int i = 0; i < items; i++) {
            s = s + standardDeviation(measures[i]);
        }
        errorSum = s;
        depthPtr++;
        return s;
    }

    public double getErrorSum() {
        return errorSum;
    }
}
