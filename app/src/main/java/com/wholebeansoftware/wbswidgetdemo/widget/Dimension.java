package com.wholebeansoftware.wbswidgetdemo.widget;

public class Dimension<NumberType extends Number> {
	public NumberType width;
	public NumberType height;

	protected Dimension(NumberType width, NumberType height) {
		this.width = width;
		this.height = height;
	}

	public void set(NumberType width, NumberType height) {
		this.width = width;
		this.height = height;
	}

	public NumberType getWidth() {
		return width;
	}

	public void setWidth(NumberType width) {
		this.width = width;
	}

	public NumberType getHeight() {
		return height;
	}

	public void setHeight(NumberType height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "Dimension{" + "width=" + width + ", height=" + height + '}';
	}
}
