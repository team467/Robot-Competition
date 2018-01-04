package org.usfirst.frc.team467.robot;

/*
 * @author Shashvat
 * LookUp table for 
 * sine, cosine, arctangent
 *
 *internally works in degrees
 *input/output is in radians
 */
public class LookUpTable {

	private static double[] sinVals, tanVals;

	public static double RadsToDegrees(double rad) {
		return 180 * rad / Math.PI;
	}

	public static double DegreesToRads(double tableunit) {
		return tableunit * Math.PI / 180;
	}

	public static void init() {
		sinVals = new double[360];
		tanVals = new double[181];
		for (int i = 0; i < 360; ++i) {
			double angle = DegreesToRads(i);
			sinVals[i] = Math.sin(angle);
		}
		tanVals[0] = Double.NEGATIVE_INFINITY;
		for (int i = 1; i < 180; ++i) {
			double angle = DegreesToRads(i - 90);
			tanVals[i] = Math.tan(angle);
		}
		tanVals[180] = Double.POSITIVE_INFINITY;
	}

	/* input in radians */
	public static double getSin(double a) {
		int angle = (int) Math.round(RadsToDegrees(a));
		angle %= 360;
		if (angle < 0) {
			angle += 360;
		}
		return sinVals[angle];
	}

	public static double getCos(double a) {
		return getSin(Math.PI / 2 - a);
	}

	/* uses binary search plus look up table to find arcTan */
	public static double getArcTan(double a) {
		int low = 0;
		int high = 180;
		while (low < high) {
			int mid = (low + high) / 2;
			if (tanVals[mid] == a) {
				// actually found value
				return DegreesToRads(mid - 90);
			}
			if (tanVals[mid] < a) {
				low = mid + 1;
			} else if (tanVals[mid] > a) {
				high = mid - 1;
			}
		}
		double degreesangle = (low + high) / 2 - 90;
		return DegreesToRads(degreesangle);
	}
}
