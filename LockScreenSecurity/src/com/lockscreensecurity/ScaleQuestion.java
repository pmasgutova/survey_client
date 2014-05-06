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

public class ScaleQuestion extends Question implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_scale_question);
		
		ScrollView sv = (ScrollView) findViewById(R.id.main_view);
		sv.fullScroll(ScrollView.FOCUS_UP);
		
		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);
		
		TextView question_text = (TextView) findViewById(R.id.question_text_view);
		question_text.setText(super.text);
		
		//adding radio buttons with answers
		RadioGroup answersGroup = (RadioGroup) findViewById(R.id.scale_question_answers);
		for(String answer : super.answers) {
			RadioButton sb = new RadioButton(this);
			//final float scale = getResources().getDisplayMetrics().density;
			//int padding_5dp = (int) (10 * scale + 0.5f);
			//sb.setPadding(0, 0, padding_5dp, 0);
			sb.setTextSize(25);
			sb.setTextColor(Color.parseColor("#1A5A72"));
			sb.setText(answer);
			answersGroup.addView(sb);
		}
	}

	@Override
	public void onClick(View v) {

		int clickedItemId = v.getId();
		if (clickedItemId == R.id.next_button) {
			RadioGroup answersGroup = (RadioGroup) findViewById(R.id.scale_question_answers);
			int selected = answersGroup.getCheckedRadioButtonId();
			if (selected == -1) {
				// No Value Selected, Throw Error
				return;
			}
			RadioButton sb = (RadioButton) findViewById(selected);
			String answer = sb.getText().toString();
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			AnswerValue av = new AnswerValue(this.id, answer);
			answersArray.add(av);
			super.startNewQuestion();
		} 
	}
}