package com.lockscreensecurity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LockPatternQuestion extends Question implements OnClickListener {

	public static final String PREFS_NAME = "LockScreenQuestions";
	private static final int _ReqCreatePattern = 0;
	String pattern;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pattern_question);

		TextView question_text = (TextView) findViewById(R.id.question_text_view);
		question_text.setText(super.text);
		Log.d("frompattern", "line 31");
		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);
		Log.d("frompattern", "line 35");
		button = (Button) findViewById(R.id.pattern_button);
		button.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case _ReqCreatePattern:
			if (resultCode == RESULT_OK) {
				pattern = data.getStringExtra(LockPatternActivity._Pattern);
				if (pattern == null || pattern.length() == 0) {
					// No Value Selected, Throw Error
					return;
				}
				if (answersArray == null)
					answersArray = new ArrayList<AnswerValue>();
				answersArray.add(new AnswerValue(super.id, pattern));
				super.startNewQuestion();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		
		int clickedViewId = v.getId();
		if (clickedViewId == R.id.pattern_button) {
			Intent intent = new Intent(this, LockPatternActivity.class);
			Log.d("lockpattern", LockPatternActivity.class.toString());
			intent.putExtra(LockPatternActivity._Mode,
					LockPatternActivity.LPMode.CreatePattern);
			startActivityForResult(intent, _ReqCreatePattern);
		} else if (clickedViewId == R.id.next_button) {
			if (pattern == null || pattern.length() == 0) {
				// No Value Selected
				return;
			}
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			
			answersArray.add(new AnswerValue(super.id, pattern));
			super.startNewQuestion();
		}
	}
}
