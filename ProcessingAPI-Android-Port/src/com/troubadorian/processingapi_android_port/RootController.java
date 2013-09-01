package com.troubadorian.processingapi_android_port;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

public class RootController extends Activity
{
	private View mView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		int position = this.getIntent().getIntExtra("index", 0);

		switch (position)
		{
		case 0:
		{
			mView = new SketchView(this);
	        setContentView(mView);
	        mView.requestFocus();
			break;
		}
		case 1:
		{
			mView = new Sketch1(this);
			setContentView(mView);
			mView.requestFocus();
			break;
		}
		case 2:
		{
			mView = new Sketch2(this);
			setContentView(mView);
			mView.requestFocus();
			break;
		}
		case 3:
		{
			mView = new SketchView(this);
	        setContentView(mView);
	        mView.requestFocus();
			break;
		}
		default:
		{
			break;
		}
		}

	}

};