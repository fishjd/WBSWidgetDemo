/*
 * Copyright (C) 2017 Arsenal
 * All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.wholebeansoftware.wbswidgetdemo.widget;

import android.view.MotionEvent;
import android.view.View;

/**
 * This Touch Listener simply consumes the touch event and does nothing.  Thus the user can
 * not modify the widget.
 */
public class DoNothingTouchListener implements View.OnTouchListener {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// report that we have handled his event.
		return true;
	}
}
