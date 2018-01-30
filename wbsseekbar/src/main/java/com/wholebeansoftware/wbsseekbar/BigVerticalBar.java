package com.wholebeansoftware.wbsseekbar;

import java.util.HashSet;
import java.util.Set;


/**
 * This class groups all the WBS Seekbars that are joined by vertical bar.
 */
public class BigVerticalBar implements ValueChangeListener {
	// the value of the seek bar.
	Integer value;

	/**
	 * The master seekbar, the one on top with the big green arrow
	 */
	private WBSSeekBar master;

	/**
	 * Any slave seekbars.  the master may not be included.
	 */
	private Set<WBSSeekBar> slaveAll;

	/**
	 * The combination of master and slave
	 */
	private Set<WBSSeekBar> seekBarAll;


	private BigVerticalBar() {
		slaveAll = new HashSet<>();
		seekBarAll = new HashSet<>();
	}


	/**
	 * @param master master seek bar
	 */
	public BigVerticalBar(WBSSeekBar master) {
		this();
		this.master = master;
		seekBarAll.add(master);
	}


	/**
	 * Call this in the onCreate() or onCreateVeiw()
	 */
	public void onCreate() {
		setThumbWidthOnSlaves();
		setTouchListenerOnSlaves();

		// when the user changes the value
		master.addValueChangeListener(this);

		setInitialValue();


	}

	/**
	 * Set the value for all ui elements.
	 */
	private void setInitialValue() {
		Integer index = 0;
		for (WBSSeekBar seekBar : seekBarAll) {
			seekBar.setMinValue(index);
		}
	}

	/**
	 * Set the slaves to not handle any touches.
	 */
	private void setTouchListenerOnSlaves() {
		for (WBSSeekBar slave : slaveAll) {
			slave.setOnTouchListener(new DoNothingTouchListener());
		}
	}

	/**
	 * Set the thumb width on all the slaves to match the master.
	 */
	private void setThumbWidthOnSlaves() {
		final float thumbWidth = master.getThumbWidth();
		for (WBSSeekBar slave : slaveAll) {
			slave.setThumbWidth(thumbWidth);
		}
	}

	/**
	 * This does two things.
	 * <ol>
	 * <li>
	 * Updates the value in settings
	 * </li>
	 * <li>
	 * Sets the position of the slave SeekBars.
	 * </li>
	 * </ol>
	 */
	@Override
	public void valueChanged(Number value, String valueText) {
		for (WBSSeekBar seekbar : slaveAll) {
			seekbar.setMinValue(value.floatValue());
		}
		this.value = value.intValue();
	}

	// getters and setters below
	public void addSlave(WBSSeekBar slave) {
		slaveAll.add(slave);
		seekBarAll.add(slave);
	}


	public WBSSeekBar getMaster() {
		return master;
	}

	public Set<WBSSeekBar> getSlaveAll() {
		return slaveAll;
	}
}
