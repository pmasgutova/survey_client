package com.lockscreensecurity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MultiChoiceQuestion extends Question implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ScrollView sv = (ScrollView) findViewById(R.id.main_view);
		sv.fullScroll(ScrollView.FOCUS_UP);
		
		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);
		
		TextView question_text = (TextView) findViewById(R.id.question_text_view);
		question_text.setText(super.text);
		
		//adding radio buttons with answers
		RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
		for(String answer : super.answers) {
			RadioButton rb = new RadioButton(this);
			rb.setTextSize(20);
			rb.setTextColor(Color.parseColor("#1A5A72"));
			rb.setText(answer);
			answersGroup.addView(rb);
		}
	}

	@Override
	public void onClick(View v) {

		int clickedItemId = v.getId();
		if (clickedItemId == R.id.next_button) {
			RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
			int selected = answersGroup.getCheckedRadioButtonId();
			if (selected == -1) {
				// No Value Selected, Throw Error
				return;
			}
			RadioButton rb = (RadioButton) findViewById(selected);
			String answer = rb.getText().toString();
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			AnswerValue av = new AnswerValue(this.id, answer);
			answersArray.add(av);
			super.startNewQuestion();
		} 
	}
}