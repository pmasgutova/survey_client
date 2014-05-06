package com.lockscreensecurity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SendActivity extends Activity implements OnClickListener {

	ArrayList<AnswerValue> answersArray;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.activity_send);

		Button b = (Button) findViewById(R.id.send_button);
		b.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			answersArray = (ArrayList<AnswerValue>) bundle.get("answerArray");

	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.send_button) {
			// Try Send Data
			attemptSend();
		} else {
		}
	}

	public void doRequest() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// Make Json From Answers
					JSONArray json = new JSONArray();

					try {

						for(AnswerValue answer:answersArray){
							JSONObject a_json = new JSONObject();
							a_json.put("question_id", answer.questionId);
							a_json.put("answer", answer.answer);
							json.put(a_json);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					// Send Json Data
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(
							client.getParams(), 10000);

					String address = getString(R.string.server) + "answers";
					try {
						HttpPost post = new HttpPost(address);
						StringEntity se = new StringEntity(json.toString());
						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
								"application/json"));
						post.setEntity(se);

						final HttpResponse response = client.execute(post);
						
						int res = 0;
						if (response != null) {
							InputStream in;
							try {
								in = response.getEntity().getContent();
								res = response.getStatusLine().getStatusCode();
								//res = convertStreamToString(in);

							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						final int resout = res;
						//final String out = res.toString();
						
						Handler h = new Handler(
								SendActivity.this.getMainLooper());
						h.post(new Runnable() {
							@Override
							public void run() {
								handleResponse(resout);
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
						Handler h = new Handler(
								SendActivity.this.getMainLooper());
						h.post(new Runnable() {
							@Override
							public void run() {
								showInternetError();
							}
						});
						return;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
	}

	public void showInternetError() {
		Toast toast = Toast
				.makeText(
						this,
						"Could not currently connect to the server, please check your Wifi or 3G settings",
						Toast.LENGTH_LONG);
		toast.show();
	}

	public void handleResponse(int responseCode) {
		/* Checking response */
		Log.i("responseCode", responseCode+"");
		if (Integer.valueOf(responseCode) == 201) {
			// Success Response
			Toast toast = Toast.makeText(this,
					"Server Successfully Recieved Data", Toast.LENGTH_LONG);
			toast.show();
			Intent intent = new Intent(this, SurveyEnd.class);
			startActivity(intent);
			this.finish();
		} else {
			// Wrong Response
			Toast toast = Toast.makeText(this, "Server Error Recieving Data",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	public void attemptSend() {
		doRequest();
	}
}
