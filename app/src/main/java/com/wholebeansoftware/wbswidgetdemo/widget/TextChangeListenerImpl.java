package com.wholebeansoftware.wbswidgetdemo.widget;

import android.util.Log;

import com.wholebeansoftware.wbswidgetdemo.misc.WBSMath;
import com.wholebeansoftware.wbswidgetdemo.widget.TextChangeListener;

import java.util.List;

public class TextChangeListenerImpl implements TextChangeListener {
	private List<String> stringAll;

	private TextChangeListenerImpl() {
	}

	public TextChangeListenerImpl(List<String> stringAll) {
		this.stringAll = stringAll;
	}

	@Override
	public CharSequence getText(Number progress) {
		String value = "unknown";
		Float fProgress = progress.floatValue();
		fProgress = (float) Math.round(fProgress);

		fProgress = WBSMath.clipToRange(0F, fProgress, (float) stringAll.size() - 1);
		Integer iProgress = fProgress.intValue();
		if (0 <= iProgress && iProgress < stringAll.size()) {
			try {
				value = stringAll.get(iProgress);
			} catch (Exception exception) {
				Log.e("Error", "Unknown Value  " + progress);
			}
		}

		return value;
	}
}