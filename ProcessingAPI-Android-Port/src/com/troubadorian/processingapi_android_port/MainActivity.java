package com.troubadorian.processingapi_android_port;

import java.util.ArrayList;
import java.util.List;

import com.troubadorian.processingapi_android_port.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity
{

	private List<Sketch> mListThings = new ArrayList<Sketch>();

	private static final String TAG = "MainActivity";

	private ListView listView1;

	protected class CustomAdapter extends ArrayAdapter<Sketch>
	{
		public CustomAdapter(Context context, int textViewResourceId,
				List<Sketch> mListOfPhotos)
		{
			super(context, textViewResourceId, mListOfPhotos);
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent)
		{
			final int rownumber = position;
			View row = view;

			if (row == null)
			{
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row_item, parent, false);

			}

			ImageView leftImage = (ImageView) row.findViewById(R.id.leftImage);
			leftImage.setImageDrawable(MainActivity.this.getResources()
					.getDrawable(R.drawable.processing_logo_72x72));

			TextView topTextView = (TextView) row
					.findViewById(R.id.topTextView);

			topTextView.setText(mListThings.get(position).name);
			TextView bottomTextView = (TextView) row
					.findViewById(R.id.bottomTextView);

			bottomTextView.setText(mListThings.get(position).description);

			row.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(MainActivity.this,
							RootController.class);
					intent.putExtra("index", position);
					startActivity(intent);

				}
			});

			return row;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView1 = (ListView) this.findViewById(R.id.listView1);

		Sketch t1 = new Sketch("Sketch0", "Drag your finger to create a sketch");
		mListThings.add(t1);

		Sketch t2 = new Sketch("Sketch1", "Tap and drag");
		mListThings.add(t2);
		
		Sketch t3 = new Sketch("Sketch2", "Tap and drag");
		mListThings.add(t3);
		
		Sketch t4 = new Sketch("Sketch3", "Multiple Particle System");
		mListThings.add(t4);

		CustomAdapter adapter = new CustomAdapter(MainActivity.this,
				R.layout.row_item, mListThings);

		listView1.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
