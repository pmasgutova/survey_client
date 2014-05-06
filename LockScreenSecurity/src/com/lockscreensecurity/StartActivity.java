package com.lockscreensecurity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class StartActivity extends FragmentActivity implements OnClickListener {

	public static class StartDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(R.string.dialog_message).setTitle(
					R.string.dialog_title);

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((StartActivity) getActivity()).startNewQuestion();
						}
					});
			builder.setNegativeButton(R.string.more_info,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((StartActivity) getActivity()).moreInfo();
						}
					});

			// Create the AlertDialog
			return builder.create();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.);
		
		setContentView(R.layout.activity_start);
		Button b = (Button) findViewById(R.id.start_button);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.start_button) {
			try {
				String questionsInJSON = getQuestionsFromServer();
				Log.d("json", questionsInJSON);
			// Startup First Question
				Log.d("json", "about to start new intent");
				startNewQuestion();
				Log.d("json", "about to start new intent");
			/*} else {
				Toast toast = Toast.makeText(this,R.string.no_connection,
								Toast.LENGTH_LONG);
				toast.show();
			}*/
			} catch(IOException io) {
				io.printStackTrace();
			} catch(JSONException json) {
				json.printStackTrace();
			}
		}
	}

	public void startSurvey() {
		DialogFragment newFragment = new StartDialog();
		newFragment.show(getSupportFragmentManager(), "Info");
	}

	public void moreInfo() {
		Intent intent = new Intent(this, MoreInfo.class);
		startActivity(intent);
	}

	public void startNewQuestion() {
		// Get Saved Question JSON String
		SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preference),
				0);
		String json = settings.getString("questionSet", "");
		int currentQuestion = settings.getInt("currentQuestion", 0);
		if (currentQuestion >= settings.getInt("questionCount", 0))
			return;
		
		if (json.length() == 0) {
			Log.e("Error", "Error Getitng Json, no string in Prefs");
			return;
		}
		
		try {
			JSONArray questionsJson = new JSONArray(json);
			JSONObject question = questionsJson.getJSONObject(currentQuestion);
			
			String questionType = question.getString("type");
			
			if (questionType.equals("radio")) {
				Intent intent = new Intent(this, MultiChoiceQuestion.class);
				startActivity(intent);
			} else if(questionType.equals("pattern")) {
				Intent intent = new Intent(this, LockPatternQuestion.class);
				startActivity(intent);
			} else if(questionType.equals("scale")) {
				Intent intent = new Intent(this, ScaleQuestion.class);
				startActivity(intent);
			}
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
			


	}

	/*public Boolean checkQuestions() {

		// Get Saved Question JSON String
		SharedPreferences settings = getSharedPreferences("",
				0);
		String json = settings.getString("questionSet", "");

		if (json == null || json.length() == 0) {
			// First Run, get from json on file
			json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]},{\"question\":{\"question_id\":\"9\",\"question_type\":\"1\",\"question_text\":\"What is your age?\",\"question_order\":\"1\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"11\",\"answer_text\":\"Under 16\"},{\"answer_id\":\"12\",\"answer_text\":\"16-24\"},{\"answer_id\":\"13\",\"answer_text\":\"25-39\"},{\"answer_id\":\"14\",\"answer_text\":\"40-64\"},{\"answer_id\":\"15\",\"answer_text\":\"Over 65\"}]},{\"question\":{\"question_id\":\"19\",\"question_type\":\"1\",\"question_text\":\"What is your Handedness for writing?\",\"question_order\":\"2\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"48\",\"answer_text\":\"Left-Handed\"},{\"answer_id\":\"49\",\"answer_text\":\"Right-Handed\"},{\"answer_id\":\"50\",\"answer_text\":\"Ambidexterity\"}]},{\"question\":{\"question_id\":\"10\",\"question_type\":\"3\",\"question_text\":\"How would you describe the writing of your native language?\",\"question_order\":\"3\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"16\",\"answer_text\":\"Left-To-Right Latin Alphabet (e.g. English, Spanish, German)\"},{\"answer_id\":\"17\",\"answer_text\":\"Top-To-Bottom Logographic (e.g. Chinese, Japanese, Korean)\"},{\"answer_id\":\"18\",\"answer_text\":\"Right-To-Left Abjad Script (e.g. Arabic, Hebrew, Farsi, Urdu)\"},{\"answer_id\":\"19\",\"answer_text\":\"Left-To-Right Cyrillic Script (e.g. Russian, Serbian, Ukrainian)\"},{\"answer_id\":\"20\",\"answer_text\":\"Left-To-Right Abugida Script (e.g. Hindi, Bengali, Thai)\"}]},{\"question\":{\"question_id\":\"11\",\"question_type\":\"1\",\"question_text\":\"How would you describe your education level?\",\"question_order\":\"4\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"22\",\"answer_text\":\"None or very little\"},{\"answer_id\":\"23\",\"answer_text\":\"Secondary School or Highschool Level (GCSE, O-Level)\"},{\"answer_id\":\"24\",\"answer_text\":\"Further Education (A-Level, Sixth Form, Twelfth Grade)\"},{\"answer_id\":\"25\",\"answer_text\":\"Degree Level (Bachelor's)\"},{\"answer_id\":\"26\",\"answer_text\":\"Postgraduate Education (Master's, Doctorate)\"}]},{\"question\":{\"question_id\":\"12\",\"question_type\":\"1\",\"question_text\":\"How would you describe your knowledge of computer security?\",\"question_order\":\"5\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"27\",\"answer_text\":\"Very little knowledge\"},{\"answer_id\":\"28\",\"answer_text\":\"Some fundamental knowledge\"},{\"answer_id\":\"29\",\"answer_text\":\"Fair good understanding\"},{\"answer_id\":\"30\",\"answer_text\":\"Highly experienced expert in area\"}]},{\"question\":{\"question_id\":\"14\",\"question_type\":\"3\",\"question_text\":\"What form of lockscreen security do you currently use?\",\"question_order\":\"6\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"34\",\"answer_text\":\"Don't own a mobile device\"},{\"answer_id\":\"35\",\"answer_text\":\"Own a device but don't use any lockscreen security\"},{\"answer_id\":\"36\",\"answer_text\":\"Use a PIN authentication on device\"},{\"answer_id\":\"37\",\"answer_text\":\"Use password on device\"},{\"answer_id\":\"38\",\"answer_text\":\"Use a pattern recognition on device\"}]},{\"question\":{\"question_id\":\"18\",\"question_type\":\"3\",\"question_text\":\"Why do you use your current lockscreen?\",\"question_order\":\"7\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"42\",\"answer_text\":\"Don't own device or use lockscreen security\"},{\"answer_id\":\"43\",\"answer_text\":\"To Protect Personal Data\"},{\"answer_id\":\"44\",\"answer_text\":\"Due to Sensitive Business or Organisation Data\"},{\"answer_id\":\"45\",\"answer_text\":\"To stop friends or family making calls, emails and texts on my phone\"},{\"answer_id\":\"46\",\"answer_text\":\"To avoid device theft issues\"},{\"answer_id\":\"47\",\"answer_text\":\"To stop people fiddling with my phone\"}]},{\"question\":{\"question_id\":\"15\",\"question_type\":\"4\",\"question_text\":\"Please draw a simple, easy to remember lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"8\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"16\",\"question_type\":\"4\",\"question_text\":\"Next please draw a more complicated lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"9\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"17\",\"question_type\":\"1\",\"question_text\":\"Would you prefer to use a simple or more complicated pattern?\",\"question_order\":\"10\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"39\",\"answer_text\":\"Use the simplest, easy-to-remember pattern\"},{\"answer_id\":\"40\",\"answer_text\":\"Use a pattern somewhere between simple and complicated\"},{\"answer_id\":\"41\",\"answer_text\":\"Use a complicated, harder-to-remember pattern\"}]}]";
			//json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]},{\"question\":{\"question_id\":\"9\",\"question_type\":\"1\",\"question_text\":\"What is your age?\",\"question_order\":\"1\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"11\",\"answer_text\":\"Under 16\"},{\"answer_id\":\"12\",\"answer_text\":\"16-24\"},{\"answer_id\":\"13\",\"answer_text\":\"25-39\"},{\"answer_id\":\"14\",\"answer_text\":\"40-64\"},{\"answer_id\":\"15\",\"answer_text\":\"Over 65\"}]},{\"question\":{\"question_id\":\"10\",\"question_type\":\"3\",\"question_text\":\"How would you describe the writing of your native language?\",\"question_order\":\"2\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"16\",\"answer_text\":\"Left-To-Right Latin Alphabet (e.g. English, Spanish, German)\"},{\"answer_id\":\"17\",\"answer_text\":\"Top-To-Bottom Logographic (e.g. Chinese, Japanese, Korean)\"},{\"answer_id\":\"18\",\"answer_text\":\"Right-To-Left Abjad Script (e.g. Arabic, Hebrew, Farsi, Urdu)\"},{\"answer_id\":\"19\",\"answer_text\":\"Left-To-Right Cyrillic Script (e.g. Russian, Serbian, Ukrainian)\"},{\"answer_id\":\"20\",\"answer_text\":\"Left-To-Right Abugida Script (e.g. Hindi, Bengali, Thai)\"}]},{\"question\":{\"question_id\":\"11\",\"question_type\":\"1\",\"question_text\":\"How would you describe your education level?\",\"question_order\":\"3\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"22\",\"answer_text\":\"None or very little\"},{\"answer_id\":\"23\",\"answer_text\":\"Secondary School or Highschool Level (GCSE, O-Level)\"},{\"answer_id\":\"24\",\"answer_text\":\"Further Education (A-Level, Sixth Form, Twelfth Grade)\"},{\"answer_id\":\"25\",\"answer_text\":\"Degree Level (Bachelor's)\"},{\"answer_id\":\"26\",\"answer_text\":\"Postgraduate Education (Master's, Doctorate)\"}]},{\"question\":{\"question_id\":\"12\",\"question_type\":\"1\",\"question_text\":\"How would you describe your knowledge of computer security?\",\"question_order\":\"4\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"27\",\"answer_text\":\"Very little knowledge\"},{\"answer_id\":\"28\",\"answer_text\":\"Some fundamental knowledge\"},{\"answer_id\":\"29\",\"answer_text\":\"Fair good understanding\"},{\"answer_id\":\"30\",\"answer_text\":\"Highly experienced expert in area\"}]},{\"question\":{\"question_id\":\"14\",\"question_type\":\"3\",\"question_text\":\"What form of lockscreen security do you currently use?\",\"question_order\":\"5\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"34\",\"answer_text\":\"Don't own a mobile device\"},{\"answer_id\":\"35\",\"answer_text\":\"Own a device but don't use any lockscreen security\"},{\"answer_id\":\"36\",\"answer_text\":\"Use a PIN authentication on device\"},{\"answer_id\":\"37\",\"answer_text\":\"Use password on device\"},{\"answer_id\":\"38\",\"answer_text\":\"Use a pattern recognition on device\"}]},{\"question\":{\"question_id\":\"18\",\"question_type\":\"3\",\"question_text\":\"Why do you use your current lockscreen?\",\"question_order\":\"6\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"42\",\"answer_text\":\"Don't own device or use lockscreen security\"},{\"answer_id\":\"43\",\"answer_text\":\"To Protect Personal Data\"},{\"answer_id\":\"44\",\"answer_text\":\"Due to Sensitive Business or Organisation Data\"},{\"answer_id\":\"45\",\"answer_text\":\"To stop friends or family making calls, emails and texts on my phone\"},{\"answer_id\":\"46\",\"answer_text\":\"To avoid device theft issues\"},{\"answer_id\":\"47\",\"answer_text\":\"To stop people fiddling with my phone\"}]},{\"question\":{\"question_id\":\"15\",\"question_type\":\"4\",\"question_text\":\"Please draw a simple, easy to remember lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"7\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"16\",\"question_type\":\"4\",\"question_text\":\"Next please draw a more complicated lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"8\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"17\",\"question_type\":\"1\",\"question_text\":\"Would you prefer to use a simple or more complicated pattern?\",\"question_order\":\"9\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"39\",\"answer_text\":\"Use the simplest, easy-to-remember pattern\"},{\"answer_id\":\"40\",\"answer_text\":\"Use a pattern somewhere between simple and complicated\"},{\"answer_id\":\"41\",\"answer_text\":\"Use a complicated, harder-to-remember pattern\"}]}]";
			//json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]}]";
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("questionSet", json);
			editor.commit();
		}
		try {
			// Parse jSon
			JSONArray questionsJson = new JSONArray(json);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("currentQuestion", 0);
			editor.putInt("questionCount", questionsJson.length());
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}

		return false;
	}*/
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public String getQuestionsFromServer() throws IOException, JSONException{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		String address = getString(R.string.server) + "questions";
		HttpGet httpGet = new HttpGet(address);

		/*try {*/
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				
				String json = builder.toString();
				
				JSONArray questionsJson = new JSONArray(json);
				SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preference),
						0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("currentQuestion", 0);
				editor.putInt("questionCount", questionsJson.length());
				editor.putString("questionSet", json);
				editor.commit();
				
				return json;

			} else {
				//Log.e("Server eror", "Response status code is not 200, response code is " + statusCode);
				throw new IOException("Response status code is not 200, response code is " + statusCode);
			}
		/*} catch (IOException e) {
			e.printStackTrace();
			Toast toast = Toast.makeText(this,R.string.connection_error,
					Toast.LENGTH_LONG);
			toast.show();
			throw e;
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	/*public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		/*switch (item.getItemId()) {
		case R.id.download_button:
			getQuestionsFromServer();
			break;
		default:
			break;
		}
		return true;
	}*/
}
