package miniBean;


import java.util.List;

import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MyApi {
    @POST("/mobile/login") //your login function in your api
    public void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook") //your login function in your api
    public void loginByFacebbok(@Query("access_token") String access_token, Callback<Response> cb);

    @GET("/get-newsfeeds/{offset}") //a function in your api to get all the list
    public void getNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-my-communities")
    public void getMyCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/get-all-social-community-categories")
    public void getSocialCommunityCategoriesMap(Callback<List<CommunityCategoryMapVM>> callback);

}