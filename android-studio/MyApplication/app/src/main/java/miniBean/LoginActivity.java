/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package miniBean;

import URLParsing.JSONParser;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginActivity extends FragmentActivity {

	// Your Facebook APP ID
	private static String APP_ID = "827816483951229"; // Replace with your App ID

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);

	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

   /* private ProfilePictureView profilePictureView;
    private TextView greeting;*/
    
    private EditText username=null;
    private EditText  password=null;
    private Button login;
    private Button btnFbLogin;
    public String pankaj;
    public SharedPreferences session = null;
    public SharedPreferences mPref = null;

    public AsyncFacebookRunner mAsyncRunner = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = getSharedPreferences("prefs", 0);
        //printKeyHash(this);
        APP_ID = getResources().getString(R.string.app_id);


        setContentView(R.layout.login);
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        btnFbLogin = (Button) findViewById(R.id.btn_fblogin);
        
        login = (Button)findViewById(R.id.button1);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doLogin();
            }
        });
        
        /**
		 * Login button Click event
		 * */
		btnFbLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Image Button", "button Clicked");
				loginToFacebook();
			}
		});
    }



    private void doLogin() { 
    	String base = getResources().getString(R.string.base_url);
    	String url1=base+"mobile/loginTest?email="+username.getText().toString()+"&password="+password.getText().toString();
    	GetSessionID down = new GetSessionID();
    	down.execute(url1);
    } 
    
    private void doLoginUsingAccessToken(String access_token) { 
    	String base = getResources().getString(R.string.base_url);
    	String url1=base+"mobile/facebook?access_token="+access_token;
    	GetSessionID down = new GetSessionID();
    	down.execute(url1);
    } 
    
    public void loginToFacebook() {

		String access_token = session.getString("access_token", null);
		long expires = session.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			doLoginUsingAccessToken(access_token);
			btnFbLogin.setVisibility(View.INVISIBLE);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onComplete(Bundle values) {
							doLoginUsingAccessToken(facebook.getAccessToken());
						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							
						}

					});
		}
	}
    
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}



    class GetSessionID extends AsyncTask<String, Integer, Long> {
    	@Override
    	protected Long doInBackground(String... params) {
    		// TODO Auto-generated method stub
    		JSONParser jsonParser = new JSONParser();
    		String json = jsonParser.getJSONFromUrl(params[0], null);
    		if (json != null || !json.isEmpty()) {
    			session.edit().putString("sessionID", json).apply();
    			System.out.println("JSON :: "+json);
    		}
    		return null;
    	}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(i);
		}
    	
    	
    }
    
/*    public static String printKeyHash(Activity context) {
		PackageInfo packageInfo;
		String key = null;
		try {

			//getting application package name, as defined in manifest
			String packageName = context.getApplicationContext().getPackageName();

			//Retriving package info
			packageInfo = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			
			Log.e("Package Name=", context.getApplicationContext().getPackageName());
			
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				key = new String(Base64.encode(md.digest(), 0));
			
				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.e("Key Hash=", key);
				System.out.println("Key Hash :::::: ="+ key);

			} 
		} catch (NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}

		return key;
	}
*/
}

