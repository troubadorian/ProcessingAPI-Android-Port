package com.troubadorian.processingapi_android_port;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

public class Sketch3 extends View
{
	private ScaleGestureDetector mScaleDetector;
	private GestureDetectorCompat mGestureDetector;

	private static final int INVALID_POINTER_ID = 0;
	private float mScaleFactor = 1.f;

	private RectF mOldRect;
	private RectF mNewRect;
	private Paint mPaint;
	private int mActivePointerId = INVALID_POINTER_ID;
	private float mLastTouchX;
	private float mLastTouchY;

	private float mPosX;
	private float mPosY;

	private int mWidth;
	private int mHeight;
	
	public Sketch3(Context context)
	{
		super(context);
		mOldRect = new RectF(40, 10, 280, 250);
		mNewRect = new RectF(40, 10, 280, 250);
		mPaint = new Paint();
		mPaint.setColor(0x88888888);
		mWidth = this.getMeasuredWidth();
		mHeight = this.getMeasuredHeight();
		
		this.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent ev)
			{

				final int action = MotionEventCompat.getActionMasked(ev);

				switch (action)
				{
				case MotionEvent.ACTION_DOWN:
				{
					final int pointerIndex = MotionEventCompat
							.getActionIndex(ev);
					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);

					// Remember where we started (for dragging)
					mLastTouchX = x;
					mLastTouchY = y;
					// Save the ID of this pointer (for dragging)
					mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
					invalidate();
					mOldRect.set(mLastTouchX-240, mLastTouchY-240, mLastTouchX, mLastTouchY);
					break;
				}

				case MotionEvent.ACTION_MOVE:
				{
					// Find the index of the active pointer and fetch its
					// position
					final int pointerIndex = MotionEventCompat
							.findPointerIndex(ev, mActivePointerId);

					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);

					// Calculate the distance moved
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					mPosX += dx;
					mPosY += dy;

					invalidate();

					// Remember this touch position for the next move event
					mLastTouchX = x;
					mLastTouchY = y;
					mOldRect.set(mLastTouchX-240, mLastTouchY-240, mLastTouchX, mLastTouchY);
					break;
				}

				case MotionEvent.ACTION_UP:
				{
					mActivePointerId = INVALID_POINTER_ID;
					break;
				}

				case MotionEvent.ACTION_CANCEL:
				{
					mActivePointerId = INVALID_POINTER_ID;
					break;
				}

				case MotionEvent.ACTION_POINTER_UP:
				{

					final int pointerIndex = MotionEventCompat
							.getActionIndex(ev);
					final int pointerId = MotionEventCompat.getPointerId(ev,
							pointerIndex);

					if (pointerId == mActivePointerId)
					{
						// This was our active pointer going up. Choose a new
						// active pointer and adjust accordingly.
						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
						mLastTouchX = MotionEventCompat.getX(ev,
								newPointerIndex);
						mLastTouchY = MotionEventCompat.getY(ev,
								newPointerIndex);
						invalidate();
						mOldRect.set(mLastTouchX-((mLastTouchX/mWidth)*240), mLastTouchY-((mLastTouchY/mHeight)*240), mLastTouchX, mLastTouchY);
						mActivePointerId = MotionEventCompat.getPointerId(ev,
								newPointerIndex);
					}
					break;
				}
				}
				return true;
			}
		});
	}


	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(this.getResources().getColor(R.color.android_robot));
		canvas.save();
		canvas.scale(mScaleFactor, mScaleFactor);
		
		canvas.drawRect(mOldRect, mPaint);
		
		canvas.restore();

	}

}
