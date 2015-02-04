package URLParsing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import android.util.Log;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject json = null;
    static String outPut = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    public String getJSONFromUrl(String url, List<NameValuePair> params) {
 
        // Making the HTTP request
        try {
             
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            //httpPost.setEntity(new UrlEncodedFormEntity(params));
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()!=200){
                return null;
            }
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            outPut = sb.toString();
            Log.e("JSON", outPut);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return outPut;
 
    }
    
    public String getJSONFromUrlGET(String url, List<NameValuePair> params) {
    	 
        // Making the HTTP request
        try {
             
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);
            //httpPost.setEntity(new UrlEncodedFormEntity(params));
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            outPut = sb.toString();
            Log.e("JSON", outPut);
            JSONObject jsonObj = new JSONObject(outPut);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return outPut;
 
    }
}