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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends FragmentActivity {

	// Your Facebook APP ID
	private static String APP_ID = "827816483951229"; // Replace with your App ID

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);

	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

   /* private ProfilePictureView profilePictureView;
    private TextView greeting;*/
    public String pankaj;
    public SharedPreferences session = null;
    public SharedPreferences mPref = null;
    public MyApi yourUsersApi;
    public AsyncFacebookRunner mAsyncRunner = null;
    private EditText username = null;
    private EditText password = null;
    private Button login;
    private Button btnFbLogin;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = getSharedPreferences("prefs", 0);
        //printKeyHash(this);
        APP_ID = getResources().getString(R.string.app_id);


        setContentView(R.layout.login_view);
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .build();

        yourUsersApi = restAdapter.create(MyApi.class);
        username = (EditText)findViewById(R.id.userName);
        password = (EditText)findViewById(R.id.password);
        btnFbLogin = (Button) findViewById(R.id.buttonFbLogin);
        progressBar=(ProgressBar)findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        
        login = (Button)findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
               yourUsersApi.login(username.getText().toString(),password.getText().toString(),new Callback<Response>() {
                   @Override
                   public void success(Response response, Response response2) {
                       saveToSession(response);
                       Intent i = new Intent(LoginActivity.this, MainActivity.class);
                       startActivity(i);
                   }

                   @Override
                    public void failure(RetrofitError retrofitError) {
        //               System.out.println("traceis::"+retrofitError.getResponse().getStatus());
                       if(retrofitError.getResponse().getStatus()==400)
                       {
                           Toast.makeText(getApplicationContext(),"You have entered wrong User Id or Password",Toast.LENGTH_LONG).show();
                           progressBar.setVisibility(View.INVISIBLE);
                       }
                        retrofitError.printStackTrace(); //to see if you have errors

                    }
                });
            }
        });

        /*
		 * Login mycomm Click event
		 * */
		btnFbLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginToFacebook();
			}
		});
    }


    public void saveToSession(Response result){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        session.edit().putString("sessionID", sb.toString()).apply();
    }

    private void doLoginUsingAccessToken(String access_token) {
        yourUsersApi.loginByFacebbok(access_token ,new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                saveToSession(response);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors


            }
        });
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

