package miniBean;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface MyApi {
    /*LOGIN*/
    @POST("/mobile/login") //your login function in your api
    public void login(@Query("email") String email,@Query("password") String password, Callback<Response> cb);


    /*LOGIN*/
    @POST("/authenticate/mobile/facebook") //your login function in your api
    public void loginByFacebbok(@Query("access_token") String access_token, Callback<Response> cb);

    /*//*GET LIST*//*
    @GET("/api_reciever/getlist") //a function in your api to get all the list
    public void getTaskList(@Query("user_uuid") String user_uuid,Callback<ArrayList<Task>> callback);
    //this is an example of response POJO - make sure your variable name is the same with your json tagging

    *//*GET LIST*//*
    @GET("/api_reciever/getlistdetails") //a function in your api to get all the list
    public void getTaskDetail(@Query("task_uuid") String task_uuid,Callback<Task> callback);
    //this is an example of response POJO - make sure your variable name is the same with your json tagging*/

}