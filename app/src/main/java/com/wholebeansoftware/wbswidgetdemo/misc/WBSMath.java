package com.wholebeansoftware.wbswidgetdemo.misc;

public class WBSMath {

	/**
	 * Returns a value that is between min and max.  If value is between min and max returns value.
	 *
	 * @param min   The minimum allowable value.
	 * @param value The value
	 * @param max   The maximum allowable value.
	 * @return a value which is min <= value <= max;
	 */
	public static Float clipToRange(Float min, Float value, Float max) {
		if (max < min) {
			Float temp = min;
			min = max;
			max = temp;
		}

		Float result = value;
		if (value < min) {
			result = min;
		}
		if (max < value) {
			result = max;
		}
		return result;
	}


}
