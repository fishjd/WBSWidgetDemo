package com.wholebeansoftware.wbswidgetdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wholebeansoftware.wbswidgetdemo.widget.ValueChangeListener;
import com.wholebeansoftware.wbswidgetdemo.widget.WBSNotchView;

/**
 * Created by James Haring on 2018-01-19.
 * Copyright 2017 Whole Bean Software Limited
 */

public class NotchViewFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	public NotchViewFragment() {
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static NotchViewFragment newInstance(int sectionNumber) {
		NotchViewFragment fragment = new NotchViewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	WBSNotchView notchView;
	TextView notchViewValue1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notchview, container, false);

		notchView = (WBSNotchView) rootView.findViewById(R.id.notchView1);
		notchView.setValueChangeListener(new listenerFromNotchView());

		notchViewValue1 = (TextView) rootView.findViewById(R.id.notchViewValue1);

		return rootView;
	}

	/**
	 * Listen to changes in the Notch View
	 **/
	private class listenerFromNotchView implements ValueChangeListener {

		public listenerFromNotchView() {
			super();
		}

		@Override
		public void valueChanged(Number value, String valueText) {
			notchViewValue1.setText(valueText);
		}
	}
}


