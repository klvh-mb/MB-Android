package miniBean.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import miniBean.viewmodel.MessageVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class BroadcastService extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "miniBean.displayevent";
	private final Handler handler = new Handler();
    boolean isFileChanged;
	Intent intent;
	int counter = 0;
    int rowNo;
    long size;
    List<MessageVM> messageVMList;



	@Override
	public void onCreate() {
		super.onCreate();
    	intent = new Intent(BROADCAST_ACTION);
        messageVMList=new ArrayList<>();
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
        System.out.println("service start::::");
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
   
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		System.out.println("Calling DisplayLoggingInfo");
            DisplayLoggingInfo();
    	    handler.postDelayed(this, 1000); // 10 seconds
    	}
    };    
    
    private void DisplayLoggingInfo() {
    	Log.d(TAG, "entered DisplayLoggingInfo");

        if((Long)AppController.getInstance().getcId() != null) {
            AppController.getApi().getMessages(AppController.getInstance().getcId(), 0l, AppController.getInstance().getSessionId(), new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    System.out.println("service success::::");
                    String responseVm = "";
                    TypedInput body = response.getBody();
                    messageVMList.clear();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("ResponseVm::::" + line);
                            responseVm = responseVm + line;
                        }

                        JSONObject obj = new JSONObject(responseVm);

                        JSONArray userGroupArray = obj.getJSONArray("message");

                        for (int i = 0; i < userGroupArray.length(); i++) {
                            JSONObject object1 = userGroupArray.getJSONObject(i);
                            MessageVM vm = new MessageVM();
                            vm.setId(object1.getLong("id"));
                            vm.setHasImage(object1.getBoolean("hasImage"));
                            vm.setSnm(object1.getString("snm"));
                            vm.setSuid(object1.getLong("suid"));
                            vm.setCd(object1.getLong("cd"));
                            vm.setTxt(object1.getString("txt"));

                            if (!object1.isNull("imgs")) {
                                System.out.println("fill image:::" + object1.getLong("imgs"));
                                vm.setImgs(object1.getLong("imgs"));
                            }
                            messageVMList.add(vm);
                        }
                        Collections.sort(messageVMList, new Comparator<MessageVM>() {
                            public int compare(MessageVM m1, MessageVM m2) {

                                return Long.compare(m1.getCd(), m2.getCd());
                            }
                        });

                        AppController.getInstance().messageVMList = messageVMList;
                        System.out.println("message size:::" + messageVMList.size());

                        intent.putExtra("Bundle","key");
                        sendBroadcast(intent);
                        //adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
                        //listView.setAdapter(adapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
    }


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}



}
