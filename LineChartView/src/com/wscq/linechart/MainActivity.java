package com.wscq.linechart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	private LineChartSurfaceView llcv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		llcv = (LineChartSurfaceView) findViewById(R.id.llcv);
		List<List<Integer>> mLinesList = getData();
		llcv.setBloodPressure(mLinesList.get(0));
	}

	private List<List<Integer>> getData() {
		List<List<Integer>> mLineList = new ArrayList<List<Integer>>();
		List<Integer> line1 = new ArrayList<Integer>();
		List<Integer> line2 = new ArrayList<Integer>();
		for (int i = 0; i < 200; i++) {
			line1.add(550 + (int) (Math.random() * 200));
			line2.add(550 - (int) (Math.random() * 200));
		}
		mLineList.add(line1);
		mLineList.add(line2);
		return mLineList;
	}
}
