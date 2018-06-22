package com.mitnick.zoomcoloruniversal.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by benjamin on 1/20/15.
 */
public class ResultImageView extends ImageView {
    private static final String TAG = ResultImageView.class.getSimpleName();
    private final Paint mTextLinePaint = new Paint();
    private final Paint mParagraphPaint = new Paint();

    private RectF textLineRectF = new RectF();
    private RectF paragraphRectF = new RectF();
    private int progressValue = 0;
    
    private float scaleX = 0;
    private float scaleY = 0;
    private float pivotX = 0;
    private float pivotY = 0;
    private float translationX = 0;
    private float translationY = 0;
    private int imageViewWidth = 0;
    private int imageViewHeight = 0;

    private static float scaleValue = 0.1f;

    private RectF screenRectF;
    private RectF textLineOnScreenRectF = new RectF();

    public ResultImageView(Context context) {
        super(context);
        init();
    }

    public ResultImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getDrawable() != null) {
            if (!textLineRectF.isEmpty()) {
                canvas.drawRect(textLineRectF, mTextLinePaint);
                float dscaleValue = scaleX - 1;
                textLineOnScreenRectF.left = textLineRectF.left + (textLineRectF.left - pivotX) * dscaleValue;
                textLineOnScreenRectF.right = textLineRectF.right + (textLineRectF.right - pivotX) * dscaleValue;
                textLineOnScreenRectF.top = textLineRectF.top + (textLineRectF.top - pivotY) * dscaleValue;
                textLineOnScreenRectF.bottom = textLineRectF.bottom + (textLineRectF.bottom - pivotY) * dscaleValue;
                performMove();
            } else {
                canvas.drawColor(0);
            }

            if (!paragraphRectF.isEmpty()) {
                paragraphRectF.top += (paragraphRectF.bottom - paragraphRectF.top) * (progressValue / 100f);
                canvas.drawRect(paragraphRectF, mParagraphPaint);
            } else {
                //Clear the paragraph rect
                canvas.drawColor(0);
            }
        }
    }
    
    @SuppressLint("DrawAllocation")
	@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        pivotX = imageViewWidth / 2;
        pivotY = imageViewHeight / 2;
        setPivotX(pivotX);
        setPivotY(pivotY);
        scaleX = getScaleX();
        scaleY = getScaleY();
        translationX = getTranslationX();
        translationY = getTranslationY();
        imageViewWidth = getWidth();
        imageViewHeight = getHeight();
        screenRectF = new RectF(0, 0, imageViewWidth, imageViewHeight);
    }

    void init() {
        mTextLinePaint.setColor(Color.BLUE);
        mTextLinePaint.setAlpha(128);
        mTextLinePaint.setAntiAlias(true);
        mTextLinePaint.setStyle(Paint.Style.FILL);

        mParagraphPaint.setColor(Color.GRAY);
        mParagraphPaint.setAlpha(128);
        mParagraphPaint.setAntiAlias(true);
        mParagraphPaint.setStyle(Paint.Style.FILL);
    }

    public void setTextLineRectF(RectF rect) {
        this.textLineRectF = rect;
        invalidate();
    }

    public void setParagrapRectF(RectF rect) {
        progressValue = 0;
        this.paragraphRectF = rect;
        invalidate();
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
        invalidate();
    }
    
    /**
     * Zoom In or Zoom Out
     * @param zoomIn if zoomIn is ture, then zoom in the imageview, else zoom out
     */

    public boolean performScale(boolean zoomIn) {
    	boolean isZoomable = false;
        if (zoomIn) {
            //Caculate the scaled TextLineRect on screen
            float dscaleValue = scaleX + scaleValue - 1;
            textLineOnScreenRectF.left = textLineRectF.left + (textLineRectF.left - pivotX) * dscaleValue;
            textLineOnScreenRectF.right = textLineRectF.right + (textLineRectF.right - pivotX) * dscaleValue;
            textLineOnScreenRectF.top = textLineRectF.top + (textLineRectF.top - pivotY) * dscaleValue;
            textLineOnScreenRectF.bottom = textLineRectF.bottom + (textLineRectF.bottom - pivotY) * dscaleValue;

            if (screenRectF.contains(textLineOnScreenRectF)) {
                Log.d(TAG, "Zoom In");
                scaleX += scaleValue;
                scaleY += scaleValue;
                setScaleX(scaleX);
                setScaleY(scaleY);
            } else {
                if (textLineOnScreenRectF.width() < imageViewWidth && textLineOnScreenRectF.height() < imageViewHeight) {
                    scaleX += scaleValue;
                    scaleY += scaleValue;
                    setScaleX(scaleX);
                    setScaleY(scaleY);
                    performMove();
                } else {
					isZoomable = true;
				}
            }
        } else {
            float dscaleValue = scaleX - scaleValue - 1;
            if (dscaleValue > 0 && dscaleValue > 0) {
                textLineOnScreenRectF.left = textLineRectF.left + (textLineRectF.left - pivotX) * dscaleValue;
                textLineOnScreenRectF.right = textLineRectF.right + (textLineRectF.right - pivotX) * dscaleValue;
                textLineOnScreenRectF.top = textLineRectF.top + (textLineRectF.top - pivotY) * dscaleValue;
                textLineOnScreenRectF.bottom = textLineRectF.bottom + (textLineRectF.bottom - pivotY) * dscaleValue;
                if (screenRectF.contains(textLineOnScreenRectF)) {
                    scaleX -= scaleValue;
                    scaleY -= scaleValue;
                    setScaleX(scaleX);
                    setScaleY(scaleY);
                } else {
                    scaleX -= scaleValue;
                    scaleY -= scaleValue;
                    setScaleX(scaleX);
                    setScaleY(scaleY);
                    performMove();
                }
            } else {
                translationX = translationY = 0;
                scaleX = scaleY = 1;
                setTranslationX(translationX);
                setTranslationY(translationY);
                setScaleX(scaleX);
                setScaleY(scaleY);
            }
        }
        
        return isZoomable;
    }

    public void performMove() {

    	if (screenRectF.contains(textLineOnScreenRectF) || textLineRectF.isEmpty()) {
			return;
		}

        if (textLineOnScreenRectF.left < 0) {
            translationX = -textLineOnScreenRectF.left;
        }
        if (textLineOnScreenRectF.top < 0) {
            translationY = -textLineOnScreenRectF.top;
        }
        if (textLineOnScreenRectF.right > imageViewWidth) {
            translationX = -(textLineOnScreenRectF.right - imageViewWidth);
        }
        if (textLineOnScreenRectF.bottom > imageViewHeight) {
            translationY = -(textLineOnScreenRectF.bottom - imageViewHeight);
        }

        setTranslationX(translationX);
        setTranslationY(translationY);
    }

}
