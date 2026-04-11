public class Assignment3two {

    // 原函數 f(x) = x^2
    public static double f(double x) {
        return x * x;
    }

    // 真正導數 f'(x) = 2x
    public static double exactDerivative(double x) {
        return 2 * x;
    }

    // Forward Difference
    public static double forwardDifference(double x, double h) {
        return (f(x + h) - f(x)) / h;
    }

    // Central Difference
    public static double centralDifference(double x, double h) {
        return (f(x + h) - f(x - h)) / (2 * h);
    }

    public static void main(String[] args) {
        double x = 2.0;
        double h = 0.001;

        double fx = f(x);
        double fxh = f(x + h);
        double fxMinusH = f(x - h);

        double forward = forwardDifference(x, h);
        double central = centralDifference(x, h);
        double exact = exactDerivative(x);

        System.out.println("=== Finite Difference Derivative Result ===");
        System.out.println("Function: f(x) = x^2");
        System.out.println("x = " + x);
        System.out.println("h = " + h);
        System.out.println();

        System.out.println("f(x)   = " + fx);
        System.out.println("f(x+h) = " + fxh);
        System.out.println("f(x-h) = " + fxMinusH);
        System.out.println();

        System.out.println("Forward Difference:");
        System.out.println("f'(x) approx (f(x+h) - f(x)) / h");
        System.out.println("f'(x) = " + forward);
        System.out.println();

        System.out.println("Central Difference:");
        System.out.println("f'(x) approx (f(x+h) - f(x-h)) / (2h)");
        System.out.println("f'(x) = " + central);
        System.out.println();

        System.out.println("Exact Derivative:");
        System.out.println("f'(x) = 2x = " + exact);
        System.out.println();

        System.out.println("Error Comparison:");
        System.out.println("Forward Difference Error = " + Math.abs(exact - forward));
        System.out.println("Central Difference Error = " + Math.abs(exact - central));
    }
}