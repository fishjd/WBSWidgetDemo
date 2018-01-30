package com.wholebeansoftware.wbsseekbar;


import com.wholebeansoftware.wbsseekbar.Util.ValueBase;

import java.util.HashMap;

/**
 * A extension of Enum.  This type holds the four states of the vertical bar on the WBSSeekBar.
 */
public class VerticalBarDrawType extends ValueBase {

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
		ValueBase.mapAll = new HashMap<>();
		ValueBase.mapAll.put(0x00, none);
		ValueBase.mapAll.put(0x01, top);
		ValueBase.mapAll.put(0x02, bottom);
		ValueBase.mapAll.put(0x03, both);
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
