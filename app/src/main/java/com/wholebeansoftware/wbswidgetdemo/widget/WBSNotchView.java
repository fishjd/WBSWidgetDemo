package com.wholebeansoftware.wbswidgetdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.wholebeansoftware.wbswidgetdemo.R;
import com.wholebeansoftware.wbswidgetdemo.misc.AssertAndroid;
import com.wholebeansoftware.wbswidgetdemo.misc.WBSMath;

import java.text.DecimalFormat;

/**
 * A custom Notch view.  contains the thumb in the center and vertical bars glide left and right.
 */
public class WBSNotchView extends View {

	/* This class has two X coordinate systems.
	   The user coordinates with values between RangeMin RangeMax.  There is no User Y coordinate
	   The View object coordinates. Labeled 'Raw'  Values between -width/2  and the +width/2 of
	   the view.

	   All x-coordinates are in View/Raw coordinates.
	 */


	private String logTag = this.getClass().getSimpleName();

	private String DEBUG_TAG = logTag + " Gestures";
	private GestureDetectorCompat mGestureDetector;


	private com.wholebeansoftware.wbswidgetdemo.widget.ValueChangeListener valueChangeListener;
	private com.wholebeansoftware.wbswidgetdemo.widget.TextChangeListener textChangeListener;


	private Integer notchBarWidth;
	private Integer notchBarWidthScreenMultiple;

	/**
	 * The current value of the seekBar.   This is most likely what your are looking for.
	 **/
	private float valueCurrent;

	/**
	 * The minimum of the seek bar
	 **/
	private Float minRange;

	/**
	 * the maximum or the seek bar
	 */
	private Float maxRange;


	private Integer majorCount;
	/**
	 * The number of minor / small notch bars between every major notch mark
	 */
	private Integer minorPerMajorCount;

	private Integer majorNotchColor;
	private Integer minorNotchColor;
	private Float notchTextSize = 12F;
	private Integer notchTextColor = Color.RED;
	private String notchTextFont = "sans-serif-condensed";

	private Integer leftThumbColor;
	private Integer leftThumbColorDimmed;
	private Float barPadding;
	private float barHeight;
	private float thumbWidth;
	private float thumbHeight;
	/**
	 * The minimum value in x Raw coordinates allowed
	 **/
	private Float thumbMin;
	/**
	 * The maximum value in x Raw coordinates allowed
	 **/
	private Float thumbMax;

	private Integer leftThumbDrawableId;
	private Integer leftThumbHighlightDrawableId;


	private Drawable leftDrawable;
	private Bitmap leftThumb;
	private Bitmap leftThumbPressed;
	private Thumb pressedThumb;

	private Paint _paint;

	/**
	 * Do we draw the thumb text
	 */
	private Boolean drawThumbText;
	private Float thumbTextSize = 12F;
	private Integer thumbTextColor = Color.RED;
	private String thumbTextFont = "sans-serif-condensed";

	private RectF rectLeftThumb;

	private boolean mIsDragging;
	private final DecimalFormat oneDec = new DecimalFormat("0.0");


	protected enum Thumb {
		MIN
	}


	public WBSNotchView(Context context) {
		this(context, null);
	}

	public WBSNotchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WBSNotchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// prevent render is in edit mode
		if (isInEditMode()) {
			return;
		}

		// Instantiate the gesture detector with the application context and an implementation of
		// GestureDetector.OnGestureListener
		mGestureDetector = new GestureDetectorCompat(context, new GestureListener());

		// Set the gesture detector as the double tap listener.
		// mDetector.setOnDoubleTapListener(new DoubleTapListener());

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WBSNotchView);
		try {
			minRange = array.getFloat(R.styleable.WBSNotchView_min_range, 0F);
			maxRange = array.getFloat(R.styleable.WBSNotchView_max_range, 100F);

			//			Float valueCurrentDefault = (getMaxRange() - getMinRange()) / 2F;
			//			valueCurrentDefault += getMinRange();
			Float valueCurrentDefault = getMinRange();
			this.valueCurrent = array.getFloat(R.styleable.WBSNotchView_value_current,
				valueCurrentDefault);

			setMajorCount(
				array.getInteger(R.styleable.WBSNotchView_major_count, calcMajorCount()));
			minorPerMajorCount = array.getInteger(
				R.styleable.WBSNotchView_minor_per_major_count, 3);


			notchBarWidth = array.getInteger(R.styleable.WBSNotchView_notch_bar_width, -1);
			notchBarWidthScreenMultiple = array.getInteger(
				R.styleable.WBSNotchView_notch_bar_width_screen_multiple, -1);

			majorNotchColor = array.getColor(R.styleable.WBSNotchView_major_notch_color,
				Color.BLUE);
			minorNotchColor = array.getColor(R.styleable.WBSNotchView_minor_notch_color,
				Color.YELLOW);

			notchTextSize = array.getFloat(R.styleable.WBSNotchView_notch_text_size, 14);
			notchTextColor = array.getColor(R.styleable.WBSNotchView_notch_text_color,
				Color.RED);
			notchTextFont = array.getString(R.styleable.WBSNotchView_notch_text_font);

			leftThumbColor = array.getColor(R.styleable.WBSNotchView_thumb_color, Color.BLACK);
			leftThumbColorDimmed = array.getColor(
				R.styleable.WBSNotchView_thumb_color_dimmed, Color.GREEN);
			setLeftThumbDrawable(array.getResourceId(R.styleable.WBSNotchView_thumb_image,
				R.drawable.circle_label));

			drawThumbText = array.getBoolean(R.styleable.WBSNotchView_draw_thumb_text, true);
			thumbTextSize = array.getFloat(R.styleable.WBSNotchView_thumb_text_size, 12F);
			thumbTextColor = array.getColor(R.styleable.WBSNotchView_thumb_text_color,
				Color.RED);
			thumbTextFont = array.getString(R.styleable.WBSNotchView_thumb_text_font);


		} finally {
			array.recycle();
		}
		init();
	}

	protected void init() {
		leftThumb = getBitmap(leftDrawable);
		leftThumbPressed = (leftThumbPressed == null) ? leftThumb : leftThumbPressed;


		barHeight = calcBarHeight();
		barPadding = calcBarPadding();

		calcThumbSize();

		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectLeftThumb = new RectF();

		pressedThumb = null;

		setWillNotDraw(false);
	}

	public WBSNotchView setValueCurrent(float valueCurrent) {
		this.valueCurrent = cropToRange(valueCurrent);
		setXDistance(convertValueToDistance(valueCurrent));
		invalidate();
		return this;
	}

	private void callValueChanged(Float value) {
		if (valueChangeListener != null) {
			valueChangeListener.valueChanged(value, getValueText(value));
		}
	}

	/**
	 * ensure that the value is between min and max
	 */
	public float cropToRange(Float value) {
		value = Math.max(value, getMinRange());
		value = Math.min(value, getMaxRange());
		return value;
	}

	public WBSNotchView setLeftThumbColor(int leftThumbColor) {
		this.leftThumbColor = leftThumbColor;
		return this;
	}

	public WBSNotchView setLeftThumbHighlightDrawable(int resId) {
		leftThumbHighlightDrawableId = resId;
		return this;
	}


	public void setValueChangeListener(
		com.wholebeansoftware.wbswidgetdemo.widget.ValueChangeListener valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	public void setTextChangeListener(
		com.wholebeansoftware.wbswidgetdemo.widget.TextChangeListener textChangeListener) {
		this.textChangeListener = textChangeListener;
	}

	public Thumb getPressedThumb() {
		return pressedThumb;
	}

	public RectF getLeftThumbRect() {
		return rectLeftThumb;
	}

	public Float getValueCurrent() {
		return valueCurrent;
	}

	public Integer getLeftThumbColor() {
		return leftThumbColor;
	}

	public Drawable getLeftDrawable() {
		return leftDrawable;
	}

	public float getThumbWidth() {
		return thumbWidth;
	}

	public void setThumbWidth(Float thumbWidth) {
		calcThumbSize(thumbWidth);
		barHeight = calcBarHeight();
		barPadding = calcBarPadding();
		invalidate();
	}

	public float getThumbHeight() {
		return thumbHeight;
	}

	public WBSNotchView setLeftThumbDrawable(int resId) {
		leftThumbDrawableId = resId;
		calcThumbSize();
		invalidate();
		return this;
	}

	/**
	 * Set thumbWidth and thumbHeight
	 */
	protected void calcThumbSize() {
		if (leftThumbDrawableId != null) {
			Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), leftThumbDrawableId);
			thumbHeight = myBitmap.getHeight();
			thumbWidth = myBitmap.getWidth();
		} else {
			thumbHeight = getResources().getDimension(R.dimen.thumb_height);
			thumbWidth = getResources().getDimension(R.dimen.thumb_width);
		}
		thumbMin = 0 + thumbWidth / 2;
		thumbMax = getWidth() - (thumbWidth / 2);
	}

	protected void calcThumbSize(Float thumbWidthInput) {
		if (leftThumbDrawableId != null) {
			Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), leftThumbDrawableId);
			thumbHeight = myBitmap.getHeight();
		} else {
			thumbHeight = getResources().getDimension(R.dimen.thumb_height);
		}
		this.thumbWidth = thumbWidthInput;
		thumbMin = 0 + thumbWidthInput / 2;
		thumbMax = getWidth() - (thumbWidthInput / 2);
	}


	public float calcBarHeight() {
		return (thumbHeight * 0.5f) * 0.3f;
	}

	/**
	 * The right and left padding for the bar and highlightBar.
	 * The padding should be large enough so the thumb can 'hang over' the bar.
	 *
	 * @return padding in pixels.
	 */
	public float calcBarPadding() {
		return thumbWidth / 2;
	}


	protected Bitmap getBitmap(Drawable drawable) {
		return (drawable != null) ? ((BitmapDrawable) drawable).getBitmap() : null;
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		// This is the first we can access width and height.  We place code that requires  width
		// or height  here that we might want to place in the constructor.
		if (width != 0) {
			// ¿ Did the use set the notchBarWidthScreenMultiple
			if (-1 != notchBarWidthScreenMultiple) {
				setNotchBarWidth(notchBarWidthScreenMultiple * getWidth());
			} else {
				if (-1 == getNotchBarWidth()) {
					// set notch bar width to default.
					setNotchBarWidth(2 * getWidth());
				}
			}
			setXDistance(convertValueToDistance(getValueCurrent()));
		}
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		calcThumbSize();

		/*
		 * contains the size of the bar that is drawn.   Highlight Bar uses the top and bottom.
		 **/
		RectF rectBar = new RectF();

		// prevent render is in edit mode
		if (isInEditMode()) {
			return;
		}

		//drawBar(canvas, rectBar);

		// drawBottom(canvas);
		// only used in debugging.
		// drawInRangeBar(canvas);
		drawNotchBars(canvas, majorCount, minorPerMajorCount);
		drawNotchMarkText(canvas, majorCount);

		// draw left thumb
		Float center = convertRawToUser(getWidth() / 2F);
		drawThumb(canvas, _paint, center);

		drawVerticalBar(canvas, _paint, center);

		// draw left thumb text
		if (drawThumbText) {
			drawThumbText(canvas, _paint, getValueCurrent());
		}
	}

	/**
	 * Draw the Major (aka Big) and minor (aka small) notch bars.
	 *
	 * @param canvas        The canvas to draw on.
	 * @param countMajor    The number of major notch bars.
	 * @param minorPerMajor The number of minor notch bars per major notch mark. Usually an odd
	 *                      number.
	 */
	private void drawNotchBars(Canvas canvas, Integer countMajor, Integer minorPerMajor) {
		if (countMajor < 2) {
			return;
		}
		RectF notchBarRect = new RectF();
		countMajor -= 1;

		// Width of minor notches;
		Integer widthMinor = 1;
		Integer widthMajor = 4 * widthMinor;

		widthMinor = convertSPtoDp((float) widthMinor).intValue();
		widthMajor = convertSPtoDp((float) widthMajor).intValue();
		final int height = getHeight() / 2;

		notchBarRect.top = 0;
		notchBarRect.bottom = height;
		notchBarRect.left = 0;
		notchBarRect.right = widthMajor;

		Paint paintBar = new Paint();
		paintBar.setStyle(Paint.Style.FILL);
		paintBar.setColor(majorNotchColor);
		paintBar.setAntiAlias(true);

		int saveCount = canvas.save();
		canvas.translate(getXDistance() - calcXOffset(), getHeight() / 2);

		// Draw Major/big notches
		Float spacing = calcSpacing(countMajor);
		for (int i = 0; i <= countMajor; i++) {
			canvas.drawRoundRect(notchBarRect, 0, 0, paintBar);
			canvas.translate(spacing, 0);
		}
		canvas.restoreToCount(saveCount);

		// Draw Minor/small notches
		saveCount = canvas.save();
		canvas.translate(getXDistance() - calcXOffset(), getHeight() / 2);

		// The user specifics the number of minor notches between the major notches.   This
		// function needs to add one(1) to skip the space of the major notch.
		minorPerMajor += 1;
		Integer countMinor = countMajor * minorPerMajor;
		notchBarRect.top = height / 2F;
		notchBarRect.right = widthMinor;
		paintBar.setColor(minorNotchColor);
		spacing = calcSpacing(countMinor);
		for (int i = 0; i <= countMinor; i++) {
			// skip the major notch
			if (i % minorPerMajor != 0) {
				canvas.drawRoundRect(notchBarRect, 0, 0, paintBar);
			}
			canvas.translate(spacing, 0);
		}
		canvas.restoreToCount(saveCount);
	}

	private void drawNotchMarkText(Canvas canvas, Integer countMajor) {
		countMajor -= 1;

		int saveCount = canvas.save();
		canvas.translate(getXDistance() - calcXOffset(), 0);

		// Draw Major/big notches
		Float spacing = calcSpacing(countMajor);
		for (Integer i = 0; i <= countMajor; i++) {
			String text;
			if (textChangeListener != null) {
				text = textChangeListener.getText(getMinRange().intValue() + i).toString();
			} else {
				text = Integer.toString(getMinRange().intValue() + i);
			}
			drawText(canvas, text);
			canvas.translate(spacing, 0);
		}
		canvas.restoreToCount(saveCount);
	}

	/**
	 * The  X offset between the zero point of the Notch Bars and the zero point of the width of
	 * the screen.
	 *
	 * @return xOffset.  In Screen / Raw coordinates.
	 */
	private Float calcXOffset() {
		return ((float) getNotchBarWidth() / 2F) - ((float) getWidth() / 2F);
	}


	/**
	 * get the user representation of the current value
	 *
	 * @param value current Value
	 * @return the text the user should see.
	 */
	public String getValueText(Float value) {
		String text;

		if (textChangeListener != null) {
			text = textChangeListener.getText(value).toString();
		} else {
			text = oneDec.format(value);
		}
		AssertAndroid.assertDebugNotNull("text may not be null", text);
		return text;
	}

	/**
	 * Draw the text on top of the notch bars.
	 *
	 * @param canvas The canvas translated on the x axis.
	 * @param text   The text to draw.
	 */
	protected void drawText(final Canvas canvas, String text) {
		if (text == null || text.trim().length() <= 0) {
			return;
		}
		// x Axis is zero as the canvas is translated.
		Float xPosition = 0F;

		// Place the bottom on of the text exactly in the middle of the yAxis.
		Float yPosition = ((float) getHeight()) / 2F;

		// Move the text up a bit so there is some space between the top of the notch mark and the
		// bottom of the text.
		yPosition += -20;

		final Paint paint = new Paint();

		// set the attributes of paint.
		paint.setTextAlign(Paint.Align.CENTER);

		paint.setColor(notchTextColor);

		float fTextSize = convertSPtoDp(notchTextSize);
		paint.setTextSize(fTextSize);

		Typeface typeface = Typeface.create(notchTextFont, Typeface.BOLD);
		paint.setTypeface(typeface);

		//  finally, draw the text.
		canvas.drawText(text, xPosition, yPosition, paint);
	}


	/**
	 * Calculate the space between notch bars.
	 *
	 * @param count usually getMajorCount or minorCount
	 * @return space between notch bars.
	 */
	protected Float calcSpacing(Integer count) {
		return getNotchBarWidth() / count.floatValue();
	}

	/**
	 * Draw the main bar
	 **/
	protected void drawBar(final Canvas canvas, final RectF rect) {

		rect.left = 0F + barPadding;
		rect.right = getWidth() - barPadding;

		Float yCoordinate = getThumbHeight() / 2F;
		rect.top = (yCoordinate - (barHeight / 2));
		rect.bottom = (yCoordinate + (barHeight / 2));

		Paint paintBar = new Paint();
		paintBar.setStyle(Paint.Style.FILL);
		paintBar.setAntiAlias(true);
		canvas.drawRoundRect(rect, 0, 0, paintBar);

		//		// draw seek bar active range line
		//		drawHighlightBar(canvas, paintBar, rect);
	}

	protected void drawBottom(final Canvas canvas) {
		Paint paintBar = new Paint();
		paintBar.setStyle(Paint.Style.FILL);
		paintBar.setAntiAlias(true);
		paintBar.setColor(Color.CYAN);

		RectF rectBottom = new RectF();
		rectBottom.top = getHeight() / 2F;

		rectBottom.bottom = getHeight();
		rectBottom.left = 0;
		rectBottom.right = getWidth();
		canvas.drawRoundRect(rectBottom, 0, 0, paintBar);
	}


	protected void drawThumb(final Canvas canvas, final Paint paint, Float value) {

		paint.setColor(leftThumbColor);

		rectLeftThumb = calcThumbRect(convertUserToRaw(value)
			//, getThumbWidth(), getThumbHeight()
		);

		Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), leftThumbDrawableId);
		if (leftThumb != null && leftThumbPressed != null) {
			// Bitmap lThumb = calcLeftThumb(leftThumb, leftThumbPressed);
			drawLeftThumbWithImage(canvas, paint, rectLeftThumb, myBitmap);
		} else if (myBitmap != null && leftThumbPressed == null) {
			drawLeftThumbWithImage(canvas, paint, rectLeftThumb, myBitmap);
		} else {
			drawLeftThumbWithColor(canvas, paint, rectLeftThumb);
		}


	}

	/**
	 * Calculate thumb position rectangle  in raw coordinates of the thumb Image.
	 *
	 * @param xValue the current xPosition in Raw Coordinates.
	 * @return rectangle in raw coordinates to draw the thumb.
	 */
	public RectF calcThumbRect(Float xValue) {
		// left is current position  - half of thumb width
		Float thumbLeft = Math.max(xValue - thumbWidth / 2, 0);

		// right is left + thumbWidth.
		Float thumbRight = thumbLeft + thumbWidth;
		thumbRight = Math.min(thumbRight, getWidth());

		return new RectF(thumbLeft, 0, thumbRight, thumbHeight);
	}


	/**
	 * Draw the text on top of the thumb icon.
	 *
	 * @param canvas canvas from onDraw();
	 * @param paint  paint
	 * @param value  the current value of the x-coordinate of the thumb   usually
	 *               <code>getValueCurrent();</code>
	 */
	protected void drawThumbText(final Canvas canvas, final Paint paint, Float value) {
		String thumbText = getValueText(value);
		if (thumbText == null) {
			return;
		}
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(thumbTextColor);

		float fTextSize = convertSPtoDp(thumbTextSize);
		paint.setTextSize(fTextSize);


		Typeface typeface = Typeface.create(thumbTextFont, Typeface.BOLD);
		paint.setTypeface(typeface);

		float fYPosition = fTextSize + 4;
		canvas.drawText(thumbText, getWidth() / 2, fYPosition, paint);
	}


	/**
	 * Draw the Vertical Bar.
	 *
	 * @param canvas canvas from onDraw();
	 * @param paint  paint
	 * @param value  the current value of the x-coordinate of the thumb usually
	 *               <code>getValueCurrent();</code>
	 */
	protected void drawVerticalBar(final Canvas canvas, final Paint paint, Float value) {

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(getLeftThumbColorDimmed());

		RectF rectVerticalBar = new RectF();
		Float rawValue = convertUserToRaw(value);

		rectVerticalBar.left = rawValue - barHeight / 2;
		rectVerticalBar.right = rawValue + barHeight / 2;
		rectVerticalBar.top = thumbHeight;
		rectVerticalBar.bottom = getHeight();

		canvas.drawRoundRect(rectVerticalBar, value, 0F, paint);
	}


	/**
	 * Convert from sp scaled pixels for text to dp device pixels .
	 * <p>
	 * Source:  <a href=
	 * "https://stackoverflow.com/questions/3061930/how-to-set-unit-for-paint-settextsize"
	 * target="_top">
	 * https://stackoverflow.com/questions/3061930/how-to-set-unit-for-paint-settextsize
	 * </a>
	 *
	 * @param valueInSP
	 * @return value in DP
	 */
	private Float convertSPtoDp(Float valueInSP) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, valueInSP,
			getResources().getDisplayMetrics());
	}


	public Float convertDpsToPixels(Context context, Float dps) {
		// http://developer.android.com/guide/practices/screens_support.html

		// Convert the dps to pixels
		float scale = context.getResources().getDisplayMetrics().density;
		scale = scale / 2F;
		scale = Math.max(1, scale);
		return (dps * scale);

	}

	protected void drawLeftThumbWithColor(final Canvas canvas, final Paint paint,
		final RectF rect) {
		canvas.drawOval(rect, paint);
	}

	protected void drawLeftThumbWithImage(final Canvas canvas, final Paint paint, final RectF rect,
		final Bitmap image) {
		canvas.drawBitmap(image, rect.left, rect.top, paint);
	}


	/**
	 * convert from user value  to raw value X axis
	 */
	public float convertUserToRaw(double userX) {
		return convertUserToRaw(userX, getHighlightBarLength(), barPadding);
	}

	/**
	 * @param userX              rawX value
	 * @param highlightBarLength
	 * @return user value between minRange and maxRange;
	 */
	public Float convertUserToRaw(double userX, Integer highlightBarLength, Float padding) {
		Float result;
		result = (float) (userX - getMinRange()) * (highlightBarLength / (getMaxRange()
			- getMinRange()));
		result += padding;

		return result;
	}

	public Float convertRawToUser(double rawValue) {
		Integer highlightBarLength = getHighlightBarLength();
		return convertRawToUser(rawValue, highlightBarLength, barPadding);
	}

	/**
	 * Convert raw the coordinates from View object to user coordinates.
	 *
	 * @param raw
	 * @param highlightBarLength
	 * @return
	 */
	public Float convertRawToUser(double raw, Integer highlightBarLength, Float padding) {
		Float result;
		raw = raw - padding;
		float userLength = getMaxRange() - getMinRange();
		result = (float) ((raw / highlightBarLength) * userLength) + getMinRange();
		return result;
	}

	public Integer getHighlightBarLength() {
		return getWidth() - (barPadding.intValue() * 2);
	}


	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// do not call super()
		Integer height = calcViewHeightSize(heightMeasureSpec);
		Integer width = calcViewWidthSize(widthMeasureSpec);

		setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
			resolveSizeAndState(height, heightMeasureSpec, 0));
	}

	protected int calcViewWidthSize(int widthMeasureSpec) {
		Integer width;
		width = (3 * Math.round(thumbWidth)) + 5;
		return width;
	}


	protected int calcViewHeightSize(int heightMeasureSpec) {
		return Math.round((thumbHeight * 2)) + 5;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		interceptAllEvents(event);
		Boolean detectorRetVal = this.mGestureDetector.onTouchEvent(event);
		return detectorRetVal;
	}

	/**
	 * This signals to the parent {@link View}/{@link android.view.ViewGroup} to not steal or
	 * intercept touch events for the duration of the touch.
	 * <p>
	 * <p> This is required when this view is
	 * in a {@link android.support.v4.view.ViewPager} as it will steal the motion events on {@link
	 * GestureDetector} flings, {@link GestureDetector} scrolls and simple local drags.
	 * </p>
	 * <p>
	 * See:
	 * {@link android.view.ViewParent#requestDisallowInterceptTouchEvent(boolean)}
	 * </p>
	 * <p>
	 * Factored out of and should only be called from
	 * {@link WBSNotchView#onTouchEvent(MotionEvent)}.
	 * </p>
	 *
	 * @param event the current event from onTouchEvent()
	 */
	public void interceptAllEvents(MotionEvent event) {
		if (getParent() == null) {
			return;
		}

		Integer action = event.getAction();
		action = action & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}


	/**
	 * The distance from zero in the X-axis of the notch bars on the bottom half of this view.  It
	 * is positive and negative.  Accumulated of all motion events.  Clipped between a calculated
	 * <code>-getWidth()/2</code> to <code>+getWidth/2</code>.
	 **/
	private Float mXDistance = 0F;

	private void setXDistance(Float xDistance) {
		Float mXDistanceMax = (float) getNotchBarWidth() / 2F;
		Float mXDistanceMin = -1F * mXDistanceMax;
		mXDistance = WBSMath.clipToRange(mXDistanceMin, xDistance, mXDistanceMax);
		invalidate();
	}

	public Float getXDistance() {
		return mXDistance;
	}

	/**
	 * Add the scroll values to xDistance.
	 */
	public class GestureListener implements GestureDetector.OnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
			float velocityY) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
		}

		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
			float distanceY) {

			// Don't ask me why it is subtraction here.  To me dragging to the right on a left to
			// right screen should be positive,  but is is not.  :-(
			setXDistance(getXDistance() - distanceX);
			setValueCurrent(convertDistanceToValue(getXDistance()));

			// call any listeners
			callValueChanged(valueCurrent);

			// Redraw the screen.
			invalidate();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent event) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return true;
		}
	}

	/**
	 * Convert the distance to a user value.
	 * <p>
	 * Complement to {@link WBSNotchView#convertValueToDistance(Float)}
	 *
	 * @param distance the distance on the x axis in screen/raw coordinates.
	 * @return value the value in user coordinates.
	 */
	protected Float convertDistanceToValue(Float distance) {
		Float value;

		// the length of the range set by the user.
		Float userLength = getMaxRange() - getMinRange();
		userLength -= 1;

		// the length of the range on the screen.
		Integer rawLength = getNotchBarWidth();

		// change the sign
		value = -1 * distance;

		// add offset so it is zero to max.  The values on the screen range from -rawLength/2 to +
		// rawLength/2;
		value += rawLength / 2F;

		// scale to the correct value.
		value = (value / rawLength) * userLength;

		// add the offset.
		value += getMinRange();
		return value;
	}

	/**
	 * Convert user value to distance.
	 * <p>
	 * Complement to {@link WBSNotchView#convertDistanceToValue(Float)}
	 *
	 * @param value the value in user coordinates.
	 * @return distance the distance on the x axis in screen/raw coordinates.
	 */
	protected Float convertValueToDistance(Float value) {
		// the length of the range set by the user.
		Float userLength = getMaxRange() - getMinRange();
		userLength -= 1;

		// the length of the range on the screen.
		Integer rawLength = getNotchBarWidth();

		// subtract the user offset.
		value = value - getMinRange();

		// scale
		Float distance = (value * rawLength) / userLength;

		// subtract the raw offset.
		distance -= rawLength / 2;

		// change the sign.
		distance = -1 * distance;

		return distance;
	}

	public class DoubleTapListener implements GestureDetector.OnDoubleTapListener {

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {
			return true;
		}
	}

	public Float getMinRange() {
		return minRange;
	}

	public void setMinRange(Float minRange) {
		this.minRange = minRange;
		setMajorCount(calcMajorCount());
		invalidate();
	}

	public Float getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Float maxRange) {
		this.maxRange = maxRange;
		setMajorCount(calcMajorCount());
		invalidate();
	}

	public Integer getMajorCount() {
		return majorCount;
	}

	/**
	 * Set Major Count.
	 * <p>
	 * This method should be avoided.  This is calculated using minRange and maxRange.
	 * <p>
	 * In client code use instead :
	 * <pre>
	 *     myNotchBar.setMinRange( 0 );
	 *     myNotchBar.setMaxRange( myList.size() );
	 * </pre>
	 * <p>
	 * Example in this and descendants:
	 * <pre>
	 *     setMajorCount(calcMajorCount());
	 * </pre>
	 *
	 * @param majorCount The number of major / big notch bars.  Must be two or larger
	 */
	protected void setMajorCount(Integer majorCount) {
		if (majorCount < 2) {
			throw new IllegalArgumentException("Major Count too small. Must be two or Larger");
		}
		this.majorCount = majorCount;
		invalidate();
	}

	/**
	 * Calculate the number of Major Notch Bars
	 *
	 * @return Number of Major Notch Bars.
	 */
	protected Integer calcMajorCount() {
		Integer result = new Double(Math.ceil(maxRange)).intValue() - new Double(
			Math.floor(minRange)).intValue();
		return result;
	}

	public Integer getMinorPerMajorCount() {
		return minorPerMajorCount;
	}

	public void setMinorPerMajorCount(Integer minorPerMajorCount) {
		this.minorPerMajorCount = minorPerMajorCount;
		invalidate();
	}

	/**
	 * Get Notch Bar Width.
	 *
	 * @return Notch Bar Width.
	 */
	public Integer getNotchBarWidth() {
		return notchBarWidth;
	}

	/**
	 * Set notch bar width.
	 * <p>
	 * The length, in screen coordinates, of the x Axis of the notches. Zero (0) will be the
	 * minRange and notchBarWidth will be the maxRange right point.  The use will scroll the whole
	 * notch bar width to go from minRange to maxRange.
	 */
	public void setNotchBarWidth(Integer notchBarWidth) {
		this.notchBarWidth = notchBarWidth;
		invalidate();
	}

	/**
	 * Draw the thumb text,  True thumb text is drawn.  False thumb text is not drawn.
	 *
	 * @return Draw thumb Text.
	 */
	public Boolean getDrawThumbText() {
		return drawThumbText;
	}


	/**
	 * Draw the thumb text,   True thumb text is drawn.  False thumb text is not drawn.
	 *
	 * @param drawThumbText Draw thumb Text.
	 */
	public void setDrawThumbText(Boolean drawThumbText) {
		this.drawThumbText = drawThumbText;
	}

	public Integer getLeftThumbColorDimmed() {
		return leftThumbColorDimmed;
	}

	public void setLeftThumbColorDimmed(Integer leftThumbColorDimmed) {
		this.leftThumbColorDimmed = leftThumbColorDimmed;
	}
}
