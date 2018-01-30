package com.wholebeansoftware.wbsseekbar;

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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wholebeansoftware.wbsseekbar.Util.AssertAndroid;

import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A custom seekbar with a suggested range.
 */
public class WBSSeekBar extends View {

	/* This class has two X coordinate systems.
	   The user coordinates with values between RangeMin RangeMax.  There is no User Y coordinate
	   The View object coordinates. Labeled 'Raw'  Values between 0  and the width of the view.

	   Use  RawToUser() and UserToRaw() to convert.
	   All x-coordinates are in View/Raw coordinates.
	 */


	String logTag = this.getClass().getSimpleName();

	private static final int INVALID_POINTER_ID = 255;


	private final float NO_STEP = -1f;


	private Set<ValueChangeListener> valueChangeListener = new LinkedHashSet<>();

	private TextChangeListener textChangeListener;

	/**
	 * The current value of the seekBar.   This is most likely what your are looking for.
	 **/
	private float minValue;

	/**
	 * The minimum of the seek bar
	 **/
	private Float minRange;

	/**
	 * the maximum or the seek bar
	 */
	private Float maxRange;


	private int mActivePointerId = INVALID_POINTER_ID;

	private float cornerRadius = 0F;
	private int barColor;
	private int barHighlightColor;
	private int thumbColor;
	private Boolean drawThumbText;
	private Float thumbTextSize = 12F;
	private Integer thumbTextColor = Color.BLACK;
	private String thumbTextFont = "sans-serif-condensed";
	private Float barPadding;
	private Integer barHeight;
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

	private float thumbDiameter;
	private Integer thumbDrawableId;
	private Integer thumbHighlightDrawableId;
	private Boolean drawBar = true;
	private Integer verticalBarWidth;
	private VerticalBarDrawType drawVerticalBar = new VerticalBarDrawType(VerticalBarDrawType
																			  .none);
	private Float suggestedRangeMin = 0F;
	private Float suggestedRangeMax = 0F;

	private Drawable thumbDrawable;
	private Bitmap thumb;
	private Bitmap thumbPressed;
	private Thumb pressedThumb;

	private Paint _paint;

	private RectF rectThumb;

	private boolean mIsDragging;
	private final DecimalFormat oneDec = new DecimalFormat("0.0");


	protected enum Thumb {
		MIN
	}


	public WBSSeekBar(Context context) {
		this(context, null);
	}

	public WBSSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WBSSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// prevent render is in edit mode
		if (isInEditMode()) {
			return;
		}

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WBSSeekBar);
		try {
			cornerRadius = array.getFloat(R.styleable.WBSSeekBar_corner_radius, 10F);
			minValue = array.getFloat(R.styleable.WBSSeekBar_min_value, 0f);
			barColor = array.getColor(R.styleable.WBSSeekBar_bar_color, Color.GRAY);
			drawBar = array.getBoolean(R.styleable.WBSSeekBar_draw_bar, true);
			minRange = array.getFloat(R.styleable.WBSSeekBar_min_range, 0F);
			maxRange = array.getFloat(R.styleable.WBSSeekBar_max_range, 100F);
			suggestedRangeMin = array.getFloat(R.styleable.WBSSeekBar_suggested_range_min, 0F);
			suggestedRangeMax = array.getFloat(R.styleable.WBSSeekBar_suggested_range_max, 0F);
			barHighlightColor = array.getColor(R.styleable.WBSSeekBar_bar_highlight_color,
											   Color.RED);
			barHeight = array.getInteger(R.styleable.WBSSeekBar_bar_height, 6);
			thumbColor = array.getColor(R.styleable.WBSSeekBar_thumb_color, Color.BLACK);
			setThumbDrawable(
				array.getResourceId(R.styleable.WBSSeekBar_thumb_image, R.drawable.arrow_label));
			drawThumbText = array.getBoolean(R.styleable.WBSSeekBar_draw_thumb_text, true);
			thumbTextSize = array.getFloat(R.styleable.WBSSeekBar_thumb_text_size, 12F);
			thumbTextColor = array.getColor(R.styleable.WBSSeekBar_thumb_text_color, Color.BLACK);
			thumbTextFont = array.getString(R.styleable.WBSSeekBar_thumb_text_font);

			verticalBarWidth = array.getInteger(R.styleable.WBSSeekBar_vertical_bar_width, 6);
			drawVerticalBar = new VerticalBarDrawType(
				array.getInt(R.styleable.WBSSeekBar_draw_vertical_bar, 0));

			thumbDiameter = (float) array.getDimensionPixelSize(
				R.styleable.WBSSeekBar_thumb_diameter, 30);
		} finally {
			array.recycle();
		}
		init();
	}

	protected void init() {
		thumb = getBitmap(thumbDrawable);
		thumbPressed = (thumbPressed == null) ? thumb : thumbPressed;

		barPadding = calcBarPadding();

		calcThumbSize();

		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectThumb = new RectF();

		pressedThumb = null;

		setWillNotDraw(false);
	}

	//////////////////////////////////////////
	// PUBLIC METHODS
	//////////////////////////////////////////

	public WBSSeekBar setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
		return this;
	}

	public WBSSeekBar setMinValue(float minValue) {
		this.minValue = cropToRange(minValue);
		invalidate();
		return this;
	}

	/**
	 * ensure that the value is between min and max
	 */
	public float cropToRange(Float value) {
		value = Math.max(value, getMinRange());
		value = Math.min(value, getMaxRange());
		return value;
	}

	public WBSSeekBar setBarColor(int barColor) {
		this.barColor = barColor;
		return this;
	}

	public WBSSeekBar setBarHighlightColor(int barHighlightColor) {
		this.barHighlightColor = barHighlightColor;
		return this;
	}

	public WBSSeekBar setThumbColor(int thumbColor) {
		this.thumbColor = thumbColor;
		return this;
	}

	public WBSSeekBar setThumbHighlightDrawable(int resId) {
		thumbHighlightDrawableId = resId;
		return this;
	}

	public void addValueChangeListener(ValueChangeListener valueChangeListener) {
		this.valueChangeListener.add(valueChangeListener);
	}

	public void setTextChangeListener(TextChangeListener onSeekbarTextChangeListener) {
		this.textChangeListener = onSeekbarTextChangeListener;
		invalidate();
	}

	public Thumb getPressedThumb() {
		return pressedThumb;
	}

	public RectF getThumbRect() {
		return rectThumb;
	}

	public float getCornerRadius() {
		return cornerRadius;
	}

	public Float getMinValue() {
		return minValue;
	}

	public int getBarColor() {
		return barColor;
	}

	public int getBarHighlightColor() {
		return barHighlightColor;
	}

	public Integer getThumbColor() {
		return thumbColor;
	}

	public Drawable getThumbDrawable() {
		return thumbDrawable;
	}

	public float getThumbWidth() {
		return thumbWidth;
	}

	public void setThumbWidth(Float thumbWidth) {
		calcThumbSize(thumbWidth);
		barPadding = calcBarPadding();
		invalidate();
	}

	public float getThumbHeight() {
		return thumbHeight;
	}

	public WBSSeekBar setThumbDrawable(int resId) {
		thumbDrawableId = resId;
		calcThumbSize();
		invalidate();
		return this;
	}

	/**
	 * Set thumbWidth and thumbHeight
	 */
	protected void calcThumbSize() {
		if (thumbDrawableId != null) {
			Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), thumbDrawableId);
			thumbHeight = myBitmap.getHeight();
			thumbWidth = myBitmap.getWidth();
		} else {
			thumbHeight = 30F;
			thumbWidth = 30F;
		}
		thumbMin = 0 + thumbWidth / 2;
		thumbMax = getWidth() - (thumbWidth / 2);
	}

	protected void calcThumbSize(Float thumbWidthInput) {
		if (thumbDrawableId != null) {
			Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), thumbDrawableId);
			thumbHeight = myBitmap.getHeight();
		} else {
			thumbHeight = 30F;
		}
		this.thumbWidth = thumbWidthInput;
		thumbMin = 0 + thumbWidthInput / 2;
		thumbMax = getWidth() - (thumbWidthInput / 2);
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
	protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/*
		 * contains the size of the bar that is drawn.   Highlight Bar uses the top and bottom.
		 **/
		RectF rectBar = new RectF();

		// prevent render is in edit mode
		if (isInEditMode()) {
			return;
		}

		if (drawBar) {
			drawBar(canvas, rectBar);
		}

		// only used in debugging.
		// drawInRangeBar(canvas);

		// draw thumb
		drawThumb(canvas, _paint, getMinValue());

		if (drawVerticalBar.getValue().equals(VerticalBarDrawType.none) == false) {
			drawThumbVerticalBar(canvas, _paint, getMinValue());
		}
		// draw thumb text
		if (drawThumbText) {
			drawThumbText(canvas, _paint, getMinValue());
		}
	}

	/**
	 * Draw the main bar
	 **/
	protected void drawBar(final Canvas canvas, final RectF rect) {

		rect.left = 0F + barPadding;
		rect.right = getWidth() - barPadding;

		calcThumbSize();
		Float yCoordinate = getThumbHeight() / 2F;

		final float barHeightScaled = convertSPtoDp((float) getBarHeight());
		rect.top = (yCoordinate - (barHeightScaled / 2));
		rect.bottom = (yCoordinate + (barHeightScaled / 2));

		Paint paintBar = new Paint();
		paintBar.setStyle(Paint.Style.FILL);
		paintBar.setAntiAlias(true);
		canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paintBar);

		// draw seek bar active range line
		drawHighlightBar(canvas, paintBar, rect);
	}

	/**
	 * Draw the Highlight Bar
	 *
	 * @param canvas    Canvas
	 * @param paint     The same paint as the bar.
	 * @param rectInput The same paint as the bar.
	 */
	protected void drawHighlightBar(final Canvas canvas, final Paint paint, final RectF
		rectInput) {

		if (Math.abs(suggestedRangeMax - suggestedRangeMin) <= 0.001) {
			return;
		}

		// All we have to do is set the right and left and the highlight color.

		RectF rectOutput = new RectF(rectInput);
		// the Length of the bar.
		Float fLength = rectInput.right - rectInput.left;

		// The left side of the highlight bar.
		rectOutput.left = convertUserToRaw(suggestedRangeMin, fLength.intValue(), barPadding);
		// Make sure it is in range.
		rectOutput.left = Math.max(rectOutput.left, barPadding);

		// The  Right side of the highlight bar.
		rectOutput.right = convertUserToRaw(suggestedRangeMax, fLength.intValue(), barPadding);
		// Make sure it is in range.
		rectOutput.right = Math.min(rectOutput.right, getWidth() - barPadding);

		paint.setColor(getBarHighlightColor());
		canvas.drawRoundRect(rectOutput, cornerRadius, cornerRadius, paint);
	}

	/**
	 * This will draw a box for the 'inRange'.  The xCoordinates where the user can press to press
	 * the thumb.
	 * <p>
	 * Used mainly for debugging.
	 *
	 * @param canvas The canvas to draw on.
	 */
	protected void drawInRangeBar(final Canvas canvas) {

		Paint paintBar = new Paint();
		paintBar.setStyle(Paint.Style.FILL);
		paintBar.setAntiAlias(true);
		paintBar.setColor(Color.WHITE);

		rectInRange.top = 0;
		rectInRange.bottom = getHeight();
		canvas.drawRoundRect(rectInRange, cornerRadius, cornerRadius, paintBar);
	}

	protected void drawThumb(final Canvas canvas, final Paint paint, Float value) {

		paint.setColor(thumbColor);
		calcThumbSize();

		rectThumb = calcThumbRect(convertUserToRaw(value)
								  //, getThumbWidth(), getThumbHeight()
		);

		Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), thumbDrawableId);
		if (thumb != null && thumbPressed != null) {
			// Bitmap thumb = calcThumb(thumb, thumbPressed);
			draThumbWithImage(canvas, paint, rectThumb, myBitmap);
		} else if (myBitmap != null && thumbPressed == null) {
			draThumbWithImage(canvas, paint, rectThumb, myBitmap);
		} else {
			drawThumbWithColor(canvas, paint, rectThumb);
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
	 *               <code>getMinValue();</code>
	 */
	protected void drawThumbText(final Canvas canvas, final Paint paint, Float value) {

		String thumbText = getValueText(value);

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(thumbTextColor);

		float fTextSize = convertSPtoDp(thumbTextSize);
		paint.setTextSize(fTextSize);


		Typeface typeface = Typeface.create(thumbTextFont, Typeface.BOLD);
		paint.setTypeface(typeface);

		float fYPosition = fTextSize + 4;
		canvas.drawText(thumbText, convertUserToRaw(value), fYPosition, paint);
	}


	/**
	 * Draw the Vertical Bar.
	 *
	 * @param canvas canvas from onDraw();
	 * @param paint  paint
	 * @param value  the current value of the x-coordinate of the thumb usually
	 *               <code>getMinValue();</code>
	 */
	protected void drawThumbVerticalBar(final Canvas canvas, final Paint paint, Float value) {

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(getThumbColor());

		RectF rectVerticalBar = new RectF();
		Float rawValue = convertUserToRaw(value);

		final float verticalBarWidthScaled = convertSPtoDp((float) getVerticalBarWidth());
		rectVerticalBar.left = rawValue - verticalBarWidthScaled / 2;
		rectVerticalBar.right = rawValue + verticalBarWidthScaled / 2;
		if (drawVerticalBar.getValue().equals(VerticalBarDrawType.both)) {
			rectVerticalBar.top = 0;
			rectVerticalBar.bottom = getHeight();
		}
		if (drawVerticalBar.getValue().equals(VerticalBarDrawType.top)) {
			rectVerticalBar.top = 0;
			rectVerticalBar.bottom = getHeight() / 2;
		}
		if (drawVerticalBar.getValue().equals(VerticalBarDrawType.bottom)) {
			rectVerticalBar.top = getHeight() / 2;
			rectVerticalBar.bottom = getHeight();
		}

		canvas.drawRoundRect(rectVerticalBar, value, 0F, paint);
	}


	/**
	 * Convert from sp to dp.
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
	private float convertSPtoDp(Float valueInSP) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, valueInSP,
										 getResources().getDisplayMetrics());
	}


	/**
	 * <p>
	 * Source:  <a href="http://developer.android.com/guide/practices/screens_support.html"
	 * target="_top"> http://developer.android.com/guide/practices/screens_support.html </a>
	 *
	 * @param context
	 * @param dps
	 * @return
	 */
	public Float convertDpsToPixels(Context context, Float dps) {
		// http://developer.android.com/guide/practices/screens_support.html

		// Convert the dps to pixels
		float scale = context.getResources().getDisplayMetrics().density;
		scale = scale / 2F;
		scale = Math.max(1, scale);
		return (dps * scale);

	}

	protected void drawThumbWithColor(final Canvas canvas, final Paint paint, final RectF rect) {
		canvas.drawOval(rect, paint);
	}

	protected void draThumbWithImage(final Canvas canvas, final Paint paint, final RectF rect,
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

	public Integer getHighlightBarLength() {
		return getWidth() - (barPadding.intValue() * 2);
	}


	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
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
		return Math.round(thumbHeight) + 5;
	}

	/**
	 * Handles thumb selection and movement. Notifies listener callback on certain events.
	 */
	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {
		int pointerIndex;

		if (!isEnabled()) {
			return false;
		}
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
				pointerIndex = event.findPointerIndex(mActivePointerId);
				float mDownMotionX = event.getX(pointerIndex);
				if (rawX < 0 || getWidth() < rawX) {
					trackTouchEvent(mDownMotionX);
				}


				pressedThumb = evalPressedThumb(mDownMotionX);
				if (pressedThumb == null) {
					return super.onTouchEvent(event);
				}

				setPressed(true);
				mIsDragging = true;
				trackTouchEvent(event);
				invalidate();
				attemptClaimDrag();
				break;
			case MotionEvent.ACTION_MOVE:
				if (pressedThumb != null) {
					if (mIsDragging) {
						trackTouchEvent(event);
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
				pointerIndex = event.findPointerIndex(mActivePointerId);
				mDownMotionX = event.getX(pointerIndex);

				if (mIsDragging) {
					trackTouchEvent(event);
					mIsDragging = false;
					setPressed(false);
				} else {
					mIsDragging = true;
					trackTouchEvent(event);
					valueChangedNotifyAll();
					mIsDragging = false;
					setPressed(false);
				}

				pressedThumb = null;

				if (BuildConfig.DEBUG) {
					// used only  in drawInRangeBar();
					isInThumbRange(mDownMotionX, getMinValue());
					invalidate();
				}
				valueChangedNotifyAll();
				break;
			case MotionEvent.ACTION_CANCEL:
				pressedThumb = null;
				if (mIsDragging) {
					mIsDragging = false;
					setPressed(false);
				}
				invalidate();
				break;
		}

		return true;

	}

	private void valueChangedNotifyAll() {
		final Float minValue = getMinValue();
		final String valueText = getValueText(getMinValue());
		for (ValueChangeListener changeListener : valueChangeListener) {
			changeListener.valueChanged(minValue, valueText);
		}
	}

	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;

		boolean minThumbPressed = isInThumbRange(touchX, getMinValue());
		if (minThumbPressed) {
			result = Thumb.MIN;
		}
		return result;
	}

	RectF rectInRange = new RectF(0, 0, 0, 10);

	/**
	 * @param touchX current Touch X in Raw
	 * @param value  the current value in user Coordinates.
	 * @return true if touchX is is range.
	 */
	public boolean isInThumbRange(Float touchX, Float value) {

		calcThumbSize();
		Float rawValue = convertUserToRaw(value);

		Float left = rawValue - thumbWidth / 2;
		left = Math.max(left, 0);
		left = Math.min(left, getWidth() - thumbWidth);
		Float right = left + thumbWidth;

		rectInRange = new RectF(left, 0, right, 10);

		return left <= touchX && touchX <= right;
	}

	/**
	 * The x-coordinate  of the thumb in raw coordinates.
	 */
	public float rawX = -1;

	protected void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		trackTouchEvent(event.getX(pointerIndex));
	}

	protected void trackTouchEvent(Float xValueRaw) {
		Float xUser = convertRawToUser(xValueRaw);

		Boolean inRange = true;
		if (xUser < minRange) {
			inRange = false;
			rawX = convertUserToRaw(minRange);
			setMinValue(minRange);
		}
		if (maxRange < xUser) {
			inRange = false;
			rawX = convertUserToRaw(maxRange);
			setMinValue(maxRange);
		}
		if (inRange) {
			// value in range
			rawX = xValueRaw;
			setMinValue(xUser);
		}
		valueChangedNotifyAll();
	}

	public Float getMinRange() {
		return minRange;
	}

	public void setMinRange(Float minRange) {
		this.minRange = minRange;
	}

	public Float getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Float maxRange) {
		this.maxRange = maxRange;
	}

	public Boolean getDrawBar() {
		return drawBar;
	}

	public void setDrawBar(Boolean drawBar) {
		this.drawBar = drawBar;
	}


	/**
	 * Set the suggested range.
	 * <p>
	 * The suggested range will be drawn in the <code>barHighlightColor</code>.
	 * <p>
	 * Max should be greater than Min.  This method will swap if required.
	 * </p>
	 * <p>
	 * The default is (0D,100D).
	 * </p>
	 * <p>
	 * Usage: <br>
	 * Set both min an max:
	 * <code>suggestedRange(10D,50D)</code><br>
	 * Use default max:
	 * <code>setSuggestedRange(10D, getSuggestedMaxRange());</code><br>
	 * Use default min:
	 * <code>setSuggestedRange( getSuggestedMinRange(), 50D);</code><br>
	 * </p>
	 *
	 * @param suggestedRangeMin minimum
	 * @param suggestedRangeMax maximum
	 */
	@SuppressWarnings("ParameterHidesMemberVariable")
	public WBSSeekBar setSuggestedRange(Float suggestedRangeMin, Float suggestedRangeMax) {
		if (suggestedRangeMax < suggestedRangeMin) {
			// swap min and max
			Float temp = suggestedRangeMin;
			suggestedRangeMin = suggestedRangeMax;
			suggestedRangeMax = temp;
		}

		this.suggestedRangeMin = cropToRange(suggestedRangeMin);
		this.suggestedRangeMax = cropToRange(suggestedRangeMax);
		invalidate();
		return this;
	}

	public Float getSuggestedRangeMin() {
		return suggestedRangeMin;
	}

	public Float getSuggestedRangeMax() {
		return suggestedRangeMax;
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

	/**
	 * The height or thickness of the horizontal bar.
	 *
	 * @return height or thickness of the horizontal bar.
	 */
	public Integer getBarHeight() {
		return barHeight;
	}

	/**
	 * The height or thickness of the horizontal bar.
	 *
	 * @param barHeight
	 */
	public void setBarHeight(Integer barHeight) {
		this.barHeight = barHeight;
	}

	/**
	 * The width or thickness of the vertical
	 *
	 * @return width or thickness of the vertical
	 */
	public Integer getVerticalBarWidth() {
		return verticalBarWidth;
	}

	/**
	 * The width or thickness of the vertical
	 *
	 * @param verticalBarWidth width or thickness of the vertical
	 */
	public void setVerticalBarWidth(Integer verticalBarWidth) {
		this.verticalBarWidth = verticalBarWidth;
	}
}
