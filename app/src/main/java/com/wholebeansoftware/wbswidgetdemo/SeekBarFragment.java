package com.wholebeansoftware.wbswidgetdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wholebeansoftware.wbswidgetdemo.widget.BigVerticalBar;
import com.wholebeansoftware.wbswidgetdemo.widget.ValueChangeListener;
import com.wholebeansoftware.wbswidgetdemo.widget.WBSSeekBar;

/**
 * Created by James Haring on 2018-01-19.
 * Copyright 2017 Whole Bean Software Limited
 */

public class SeekBarFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	public SeekBarFragment() {
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static SeekBarFragment newInstance(int sectionNumber) {
		SeekBarFragment fragment = new SeekBarFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	TextView seekBarValue1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_seekbar, container, false);
		seekBarValue1 = (TextView) rootView.findViewById(R.id.seekBarValue1);

		// grab all the seekbars from the xml.
		WBSSeekBar seekBarMaster = (WBSSeekBar) rootView.findViewById(R.id.seekBarMaster);
		WBSSeekBar seekBarSlave1 = (WBSSeekBar) rootView.findViewById(R.id.seekBarSlave1);
		WBSSeekBar seekBarSlave2 = (WBSSeekBar) rootView.findViewById(R.id.seekBarSlave2);

		seekBarMaster.setValueChangeListener(new ListenerFromSeekBar());

		// create the object and add slaves.
		BigVerticalBar seekBarkGroup = new BigVerticalBar(seekBarMaster);
		seekBarkGroup.addSlave(seekBarSlave1);
		seekBarkGroup.addSlave(seekBarSlave2);
		seekBarkGroup.onCreate();

		return rootView;
	}

	/**
	 * Listen to changes in the Notch View
	 **/
	private class ListenerFromSeekBar implements ValueChangeListener {

		public ListenerFromSeekBar() {
			super();
		}

		@Override
		public void valueChanged(Number value, String valueText) {
			seekBarValue1.setText(valueText);
		}
	}
}


