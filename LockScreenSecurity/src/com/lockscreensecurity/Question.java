package com.lockscreensecurity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Question extends Activity {

	/**
	 * Unique identifier of a question in database, comes from json.
	 */
	protected int id;
	
	/**
	 * Question itself, comes from json
	 */
	protected String text;
	
	/**
	 * Answers's text for this particular question, comes from json
	 */
	protected ArrayList<String> answers;
	
	int question_num = 0;
	ArrayList<AnswerValue> answersArray;

	private boolean setTag;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.activity_question);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			answersArray = (ArrayList<AnswerValue>) bundle.get("answerArray");

		SharedPreferences settings = getSharedPreferences(getString(
				R.string.shared_preference),0);
		String json = settings.getString("questionSet", "");
		question_num = settings.getInt("currentQuestion", 0);

		try {
			JSONArray questionsJson = new JSONArray(json);
			JSONObject question = questionsJson.getJSONObject(question_num);
			
			this.id = question.getInt("_id");
			this.text = question.getString("text");
			
			try {
				setTag = question.getBoolean("setTag");
			} catch(JSONException e) {
				setTag = false;
			}
			
			JSONArray answersJSON = question.getJSONArray("answers");
			this.answers = new ArrayList<String>(answersJSON.length());
			
			for(int i = 0; i<answersJSON.length(); i++) {
				this.answers.add(answersJSON.getString(i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private int getNextQuestion(String json, int questionCount, String tag) {
		try {
			JSONArray questionsJson = new JSONArray(json);			
			for (int i = question_num+1; i<questionCount; i++) {
				JSONObject question = questionsJson.getJSONObject(i);
				Log.d("question", question.toString());
				String qTag = question.getString("tag");
				Log.d("tag", qTag);
				if(qTag.equals(tag) || qTag.equals("#all")) {
					return i;
				}
			}
		}
		catch(JSONException je) {};
		
		return questionCount;
	}

	public void startNewQuestion() {

		SharedPreferences settings = 
				getSharedPreferences(getString(R.string.shared_preference),0);
		SharedPreferences.Editor editor = settings.edit();
		
		
		if(setTag) {
			String tag = "#" + this.id + answersArray.get(answersArray.size()-1)
					.answer.toLowerCase().replaceAll("\\s+", "");
			editor.putString("tag", tag);
			editor.commit();
		}
		
		String tag = settings.getString("tag", "#all");
		String json = settings.getString("questionSet", "");
		question_num = getNextQuestion(json, settings.getInt("questionCount", 0), tag);
		editor.putInt("currentQuestion", question_num);
		editor.commit();

		// Get Saved Question JSON String
		//
		if (question_num >= settings.getInt("questionCount", 0)) {
			Intent sendData = new Intent(this, SendActivity.class);
			sendData.putExtra("answerArray",
					(ArrayList<AnswerValue>) answersArray);
			startActivity(sendData);
			return;
		}
		
		try {
			JSONArray questionsJson = new JSONArray(json);
			JSONObject question = questionsJson.getJSONObject(question_num);
			
			String questionType = question.getString("type");
			
			if (questionType.equals("radio")) {
				Intent intent = new Intent(this, MultiChoiceQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
			} else if(questionType.equals("pattern")) {
				Intent intent = new Intent(this, LockPatternQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
			} else if(questionType.equals("scale")) {
				Intent intent = new Intent(this, ScaleQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
			}
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_multi_choice_question, menu);
	// return true;
	// }

	@Override
	public void onBackPressed() {
		// do whatever you want the 'Back' button to do
		// as an example the 'Back' button is set to start a new Activity named
		// 'NewActivity'
		this.startActivity(new Intent(this, StartActivity.class));

		return;
	}
}
