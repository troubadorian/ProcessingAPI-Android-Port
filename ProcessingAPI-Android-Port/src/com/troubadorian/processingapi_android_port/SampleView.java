package com.troubadorian.processingapi_android_port;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;


public class SampleView extends View
{

	private Paint[] mPaints;
	private Paint mFramePaint;
	private boolean[] mUseCenters;
	private RectF[] mShapes;
	private RectF mBigShape;
	private float mStart;
	private float mSweep;
	private int mBigIndex;

	private static final float SWEEP_INC = 2;
	private static final float START_INC = 30;

	public SampleView(Context context)
	{
		super(context);

		mPaints = new Paint[4];
		mUseCenters = new boolean[4];
		mShapes = new RectF[4];

		mPaints[0] = new Paint();
		mPaints[0].setAntiAlias(true);
		mPaints[0].setStyle(Paint.Style.FILL);
		mPaints[0].setColor(0x88FF0000);
		mUseCenters[0] = false;

		mPaints[1] = new Paint(mPaints[0]);
		mPaints[1].setColor(0x8840DFDD);
		mUseCenters[1] = true;

		mPaints[2] = new Paint(mPaints[0]);
		mPaints[2].setStyle(Paint.Style.STROKE);
		mPaints[2].setStrokeWidth(4);
		mPaints[2].setColor(0x880000FF);
		mUseCenters[2] = false;

		mPaints[3] = new Paint(mPaints[2]);
		mPaints[3].setColor(0x88888888);
		mUseCenters[3] = true;

		mBigShape = new RectF(40, 10, 280, 250);

		// mShapes[0] = new RectF( 10, 270, 70, 330);
		mShapes[1] = new RectF(90, 270, 150, 330);
		mShapes[2] = new RectF(170, 270, 230, 330);
		mShapes[3] = new RectF(250, 270, 310, 330);

		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		// mFramePaint.setStyle(Paint.Style.STROKE);
		mFramePaint.setStyle(Paint.Style.FILL);
		mFramePaint.setStrokeWidth(0);
	}

	private void drawArcs(Canvas canvas, RectF shape, boolean useCenter,
			Paint paint)
	{
		canvas.drawRect(shape, paint);

		// canvas.drawRect(shape, paint);

		// canvas.drawArc(shape, mStart, mSweep, useCenter, paint);

	}

	private void drawRectangle(Canvas canvas, RectF shape,
			boolean useCenter, Paint paint)
	{
		canvas.drawRect(shape, paint);

	}


	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.WHITE);

		// drawArcs(canvas, mBigShape, mUseCenters[1], mPaints[1]);

		drawRectangle(canvas, mBigShape, mUseCenters[1], mPaints[1]);
		/*
		for (int i = 0; i < 2; i++)
		{
			drawArcs(canvas, mShapes[i], mUseCenters[i], mPaints[i]);
			drawArcs(canvas, mShapes[1], mUseCenters[1], mPaints[1]);
		}
		*/

		drawArcs(canvas, mShapes[1], mUseCenters[1], mPaints[1]);
		mSweep += SWEEP_INC;
		if (mSweep > 360)
		{
			mSweep -= 360;
			mStart += START_INC;
			if (mStart >= 360)
			{
				mStart -= 360;
			}
			mBigIndex = (mBigIndex + 1) % mShapes.length;
		}
		invalidate();
	}
}