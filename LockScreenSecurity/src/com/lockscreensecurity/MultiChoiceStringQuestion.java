package com.lockscreensecurity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MultiChoiceStringQuestion extends Question implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_multi_string_question);
		
		ScrollView sv = (ScrollView) findViewById(R.id.main_view);
		sv.fullScroll(ScrollView.FOCUS_UP);
		
		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);

		TextView question_text = (TextView) findViewById(R.id.question_text_view);
		question_text.setText(super.text);
		
		
		RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
		
		for(String answerText : super.answers) {
			RadioButton b = new RadioButton(this);
			b.setTextSize(20);
			b.setTextColor(Color.parseColor("#1A5A72"));
			b.setText(answerText);
			answersGroup.addView(b);
		}
		
		
		RadioButton b = new RadioButton(this);
		b.setTextSize(20);
		b.setTextColor(Color.parseColor("#1A5A72"));
		b.setText("Other (please specify):");
		b.setTag(-1);
		answersGroup.addView(b);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.next_button) {
			RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
			int selected = answersGroup.getCheckedRadioButtonId();
			if (selected == -1) {
				// No Value Selected, Throw Error
				return;
			}
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			RadioButton b = (RadioButton) findViewById(selected);
			int answer_id = (Integer) b.getTag();
			/*AnswerValue av = (answer_id == -1 ? new AnswerValue(question_id,
					((EditText) findViewById(R.id.otherEntryText)).getText()
							.toString()) : new AnswerValue(question_id,
					answer_id));
			answersArray.add(av);*/
			super.startNewQuestion();
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

	    View v = getCurrentFocus();
	    boolean ret = super.dispatchTouchEvent(event);

	    if (v instanceof EditText) {
	        View w = getCurrentFocus();
	        int scrcoords[] = new int[2];
	        w.getLocationOnScreen(scrcoords);
	        float x = event.getRawX() + w.getLeft() - scrcoords[0];
	        float y = event.getRawY() + w.getTop() - scrcoords[1];

	        //Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
	        if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) { 

	            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	        }
	    }
	return ret;
	}
}