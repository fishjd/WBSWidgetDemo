package com.wholebeansoftware.wbswidgetdemo.widget;


import java.util.HashMap;

/**
 * A extension of Enum.  This type holds the four states of the vertical bar on the WBSSeekBar.
 */
public class VerticalBarDrawType extends com.wholebeansoftware.wbswidgetdemo.misc.ValueBase {

	// note this should match attr.xml

	/**
	 * Do not draw the bar
	 */
	public static final String none = "none";

	/**
	 * Draw the top half of the bar.
	 */
	public static final String top = "top";

	/**
	 * Draw the bottom half of the bar.
	 */
	public static final String bottom = "bottom";

	/**
	 * Draw the top and bottom of the bar.
	 */
	public static final String both = "both";

	private void setup() {
		mapAll = new HashMap<>();
		mapAll.put(0x00, none);
		mapAll.put(0x01, top);
		mapAll.put(0x02, bottom);
		mapAll.put(0x03, both);
	}

	private VerticalBarDrawType() {
		className = VerticalBarDrawType.class.getName();
		setup();
	}

	public VerticalBarDrawType(Integer type) {
		this();
		this.type = type;
		this.value = calcValue(type);
	}

	public VerticalBarDrawType(String value) {
		this();
		this.value = value;
		this.type = calcType(value);
	}


}
