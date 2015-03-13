package miniBean.app;


import java.util.List;

import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.CommentResponse;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.HeaderDataVM;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.PostArray;
import miniBean.viewmodel.PostResponse;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface MyApi {
    @POST("/mobile/login") //your login function in your api
    public void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook") //your facebook login function in your api
    public void loginByFacebbok(@Query("access_token") String access_token, Callback<Response> cb);

    @GET("/get-newsfeeds/{offset}") //a function in your api to get all the Newsfeed list
    public void getNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-my-communities") //a function in your api to get all the joined communities list
    public void getMyCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/communityQnA/questions/{id}")
    //a function in your api to get all the Newsfeed list of specific community
    public void getCommNewsfeed(@Path("id") Long id, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-social-community-categories-map")
    //a function in your api to get all the community categories (Topic Vise Communities ) list
    public void getSocialCommunityCategoriesMap(@Query("indexOnly") Boolean indexOnly, @Query("key") String key, Callback<List<CommunityCategoryMapVM>> callback);

    @GET("/qna-landing/{qnaId}/{communityId}")  //a function in your api to get one post
    public void qnaLanding(@Path("qnaId") Long qnaId, @Path("communityId") Long communityId, Callback<CommunityPostVM> callback);

    @GET("/community/join/{id}") //a function in your api send join request to Community
    public void sendJoinRequest(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/community/leave/{id}") //a function in your api leave community.
    public void sendLeaveRequest(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/get-user-info") //a function in your api get User all Information
    public void getUserInfo(@Query("key") String key, Callback<UserVM> cb);

    @POST("/communityQnA/question/answer") //a function in your api answer on question.
    public void answerOnQuestion(@Body CommentPost commentPost, @Query("key") String key, Callback<CommentResponse> cb);

    @POST("/communityQnA/question/post")
    public void setQuestion(@Body NewPost newPost, @Query("key") String key, Callback<PostResponse> cb);

    @Multipart
    @POST("/image/uploadCommentPhoto") //a function in your api upload image for comment
    public void uploadCommentPhoto(@Part("commentId") String id,
                                   @Part("comment-photo0") TypedFile photo, Callback<Response> cb);

    @Multipart
    @POST("/image/uploadPostPhoto") //a function in your api upload image for comment
    public void uploadPostPhoto(@Part("postId") String id,
                                   @Part("post-photo0") TypedFile photo, Callback<Response> cb);

    @GET("/get-headerBar-data")
    //a function in your api to get all header meta data (notifications and requests).
    public void getHeaderBaeData(@Query("key") String key, Callback<HeaderDataVM> cb);


    //'/accept-friend-request?friend_id=:id&notify_id=:notify_id'
    @GET("/accept-friend-request") //a function in your api accept friend request.
    public void acceptFriendRequest(@Query("friend_id") Long friend_id, @Query("notify_id") Long notify_id, @Query("key") String key, Callback<Response> cb);

    //'/accept-join-request/:member_id/:group_id/:notify_id'
    @GET("/accept-join-request/{member_id}/{group_id}/{notify_id}")
    //a function in your api accept join request to Community
    public void acceptCommJoinRequest(@Path("member_id") Long member_id, @Path("group_id") Long group_id, @Path("notify_id") Long notify_id, @Query("key") String key, Callback<Response> cb);

    //'/accept-invite-request/:member_id/:group_id/:notify_id'
    @GET("/accept-invite-request/{member_id}/{group_id}/{notify_id}")
    //a function in your api accept invite request to Community
    public void acceptCommInviteRequest(@Path("member_id") Long member_id, @Path("group_id") Long group_id, @Path("notify_id") Long notify_id, @Query("key") String key, Callback<Response> cb);

    //'/ignore-it/:notify_id'
    @GET("/ignore-it/{notify_id}") //a function in your api accept invite request to Community
    public void ignoreIt(@Path("notify_id") Long notify_id, @Query("key") String key, Callback<Response> cb);

    @GET("/bookmark-post/{post_id}")
    public void setBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unbookmark-post/{post_id}")
    public void setUnBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/like-comment/{comment_id}")
    public void setLikeComment(@Path("comment_id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unlike-comment/{comment_id}")
    public void setUnLikeComment(@Path("comment_id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @GET("/like-post/{post_id}")
    public void setLikePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unlike-post/{post_id}")
    public void setUnLikePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/get-user-newsfeeds-posts/{offset}/{id}")
    public void getUserPost(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-user-newsfeeds-comments/{offset}/{id}")
    public void getUserComment(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-bookmarked-posts/{offset}")
    public void getBookmark( @Path("offset") Long offset,@Query("key") String key, Callback<List<CommunityPostVM>> cb);

    @GET("/comments/{id}/{offset}")
    public void getComments( @Path("id")Long post_id,@Path("offset") int offset, Callback<List<CommunityPostCommentVM>> cb);
}