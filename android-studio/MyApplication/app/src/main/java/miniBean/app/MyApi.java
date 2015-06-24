package miniBean.app;

import java.util.List;

import miniBean.viewmodel.BookmarkSummaryVM;
import miniBean.viewmodel.CommentPost;
import miniBean.viewmodel.CommentResponse;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostCommentVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.CommunityVM;
import miniBean.viewmodel.ConversationVM;
import miniBean.viewmodel.EmoticonVM;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.GameTransactionVM;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.LocationVM;
import miniBean.viewmodel.MessagePostVM;
import miniBean.viewmodel.MessageVM;
import miniBean.viewmodel.NewPost;
import miniBean.viewmodel.NotificationsParentVM;
import miniBean.viewmodel.PostArray;
import miniBean.viewmodel.PostResponse;
import miniBean.viewmodel.PreNurseryVM;
import miniBean.viewmodel.ProfileVM;
import miniBean.viewmodel.ResponseConversationVM;
import miniBean.viewmodel.UserProfileDataVM;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface MyApi {

    @POST("/signup")
    public void signUp(@Query("lname") String lanme,@Query("fname") String fname,@Query("email") String email,@Query("password") String password,@Query("repeatPassword") String repeatPassword,Callback<Response> cb);
    //http://localhost:9000/signup?lname=asd&fname=dsa&email=shwashank12@gmail.com&password=qwerty&repeatPassword=qwerty

    @FormUrlEncoded
    @POST("/saveSignupInfo")
    public void signUpInfo(@Field("parent_displayname") String parent_displayname,
                           @Field("parent_birth_year") Integer parent_birth_year,
                           @Field("parent_location") Integer parent_location,
                           @Field("parent_type") String parent_type,
                           @Field("num_children") String num_children,
                           @Field("bb_gender1") String bb_gender1,
                           @Field("bb_gender2") String bb_gender2,
                           @Field("bb_gender3") String bb_gender3,
                           @Field("bb_birth_year1") String bb_birth_year1,
                           @Field("bb_birth_month1") String bb_birth_month1,
                           @Field("bb_birth_day1") String bb_birth_day1,
                           @Field("bb_birth_year2") String bb_birth_year2,
                           @Field("bb_birth_month2") String bb_birth_month2,
                           @Field("bb_birth_day2") String bb_birth_day2,
                           @Field("bb_birth_year3") String bb_birth_year3,
                           @Field("bb_birth_month3") String bb_birth_month3,
                           @Field("bb_birth_day3") String bb_birth_day3,
                           @Query("key") String key,
                           Callback<Response> cb);

    @GET("/get-all-districts")
    public void getAllDistricts(@Query("key") String key, Callback<List<LocationVM>> cb);

    @POST("/mobile/login") //your login function in your api
    public void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook") //your facebook login function in your api
    public void loginByFacebook(@Query("access_token") String access_token, Callback<Response> cb);

    @GET("/init-new-user")
    public void initNewUser(@Query("key") String key, Callback<UserVM> cb);

    @GET("/get-user-info") //a function in your api get User all Information
    public void getUserInfo(@Query("key") String key, Callback<UserVM> cb);

    @GET("/get-newsfeeds/{offset}") //a function in your api to get all the Newsfeed list
    public void getNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-my-communities") //a function in your api to get all the joined communities list
    public void getMyCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/get-social-community-categories-map")
    public void getTopicCommunityCategoriesMap(@Query("indexOnly") Boolean indexOnly, @Query("key") String key, Callback<List<CommunityCategoryMapVM>> callback);

    @GET("/get-zodiac-year-communities")
    public void getZodiacYearCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/community/join/{id}") //a function in your api send join request to Community
    public void sendJoinRequest(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/community/leave/{id}") //a function in your api leave community.
    public void sendLeaveRequest(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/community/{id}")
    public void getCommunity(@Path("id")Long comm_id, @Query("key") String key, Callback<CommunityVM> cb);

    @GET("/communityQnA/questions/{id}")
    public void getCommunityInitialPosts(@Path("id") Long id, @Query("key") String key, Callback<PostArray> callback);

    @GET("/communityQnA/questions/next/{id}/{time}")
    public void getCommunityNextPosts(@Path("id") Long id, @Path("time") String time, @Query("key") String key, Callback<List<CommunityPostVM>> callback);

    @GET("/qna-landing/{qnaId}/{communityId}")  //a function in your api to get one post
    public void qnaLanding(@Path("qnaId") Long qnaId, @Path("communityId") Long communityId, @Query("key") String key, Callback<CommunityPostVM> callback);

    @GET("/comments/{id}/{offset}")
    public void getComments(@Path("id")Long post_id,@Path("offset") int offset, @Query("key") String key, Callback<List<CommunityPostCommentVM>> cb);

    @POST("/communityQnA/question/post")
    public void newCommunityPost(@Body NewPost newPost, @Query("key") String key, Callback<PostResponse> cb);

    @Multipart
    @POST("/image/uploadPostPhoto") //a function in your api upload image for comment
    public void uploadPostPhoto(@Part("postId") String id, @Part("post-photo0") TypedFile photo, Callback<Response> cb);

    @POST("/communityQnA/question/answer")
    public void answerOnQuestion(@Body CommentPost commentPost, @Query("key") String key, Callback<CommentResponse> cb);

    @Multipart
    @POST("/image/uploadCommentPhoto") //a function in your api upload image for comment
    public void uploadCommentPhoto(@Part("commentId") String id, @Part("comment-photo0") TypedFile photo, Callback<Response> cb);

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

    @GET("/delete-post/{post_id}")
    public void deletePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/delete-comment/{comment_id}")
    public void deleteComment(@Path("comment_id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    //
    // Profile APIs
    //

    @GET("/profile/{id}")
    public void getUserProfile(@Path("id") Long id, @Query("key") String key, Callback<ProfileVM> cb);

    @Multipart
    @POST("/image/upload-cover-photo")
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo,@Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo,@Query("key") String key, Callback<Response> cb);

    @GET("/get-bookmark-summary") //a function in your api get bookmark summary
    public void getBookmarkSummary(@Query("key") String key, Callback<BookmarkSummaryVM> cb);

    @GET("/get-headerBar-data")
    //a function in your api to get all header meta data (notifications and requests).
    public void getHeaderBarData(@Query("key") String key, Callback<NotificationsParentVM> cb);

    @GET("/mark-as-read/{ids}")
    public void markAsRead(@Path("ids")String id, @Query("key") String key, Callback<Response> cb);

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

    @GET("/get-user-newsfeeds-posts/{offset}/{id}")
    public void getUserPosts(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-user-newsfeeds-comments/{offset}/{id}")
    public void getUserComments(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-bookmarked-posts/{offset}")
    public void getBookmarkedPosts(@Path("offset") Long offset,@Query("key") String key, Callback<List<CommunityPostVM>> cb);

    @GET("/image/getEmoticons")
    public void getEmoticons(@Query("key") String key, Callback<List<EmoticonVM>> cb);

    @POST("/updateUserProfileData")
    public void updateUserProfileData(@Body UserProfileDataVM userProfileDataVM, @Query("key") String key, Callback<UserVM> cb);

    //
    // PN APIs
    //
    @GET("/get-pns-by-district/{id}")
    public void getPNsByDistricts(@Path("id") Long id,@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/search-pns-by-name/{query}")
    public void searchPNsByName(@Path("query")String query,@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/get-bookmarked-pns")
    public void getBookmarkedPNs(@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/get-bookmarked-pn-communities")
    public void getBookmarkedPNCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/get-pnnewsfeeds/{offset}")
    public void getPNNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-pn-info/{id}")
    public void getPNInfo(@Path("id") Long post_id,@Query("key") String key, Callback<PreNurseryVM> cb);

    @GET("/bookmark-pn/{id}")
    public void bookmarkPN(@Path("id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unbookmark-pn/{id}")
    public void unbookmarkPN(@Path("id") Long post_id, @Query("key") String key, Callback<Response> cb);

    //
    // KG APIs
    //

    @GET("/get-kgs-by-district/{id}")
    public void getKGsByDistricts(@Path("id") Long id,@Query("key") String key, Callback<List<KindergartenVM>> cb);

    @GET("/search-kgs-by-name/{query}")
    public void searchKGsByName(@Path("query")String query,@Query("key") String key, Callback<List<KindergartenVM>> cb);

    @GET("/get-bookmarked-kgs")
    public void getBookmarkedKGs(@Query("key") String key, Callback<List<KindergartenVM>> cb);

    @GET("/get-bookmarked-kg-communities")
    public void getBookmarkedKGCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/get-kgnewsfeeds/{offset}")
    public void getKGNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-kg-info/{id}")
    public void getKGInfo(@Path("id") Long post_id,@Query("key") String key, Callback<KindergartenVM> cb);

    @GET("/bookmark-kg/{id}")
    public void bookmarkKG(@Path("id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unbookmark-kg/{id}")
    public void unbookmarkKG(@Path("id") Long post_id, @Query("key") String key, Callback<Response> cb);

    //
    // Top schools APIs
    //

    @GET("/get-top-viewed-pns")
    public void getTopViewedPNs(@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/get-top-discussed-pns")
    public void getTopDiscussedPNs(@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/get-top-bookmarked-pns")
    public void getTopBookmarkedPNs(@Query("key") String key, Callback<List<PreNurseryVM>> cb);

    @GET("/get-top-viewed-kgs")
    public void getTopViewedKGs(@Query("key") String key, Callback<List<KindergartenVM>> cb);

    @GET("/get-top-discussed-kgs")
    public void getTopDiscussedKGs(@Query("key") String key, Callback<List<KindergartenVM>> cb);

    @GET("/get-top-bookmarked-kgs")
    public void getTopBookmarkedKGs(@Query("key") String key, Callback<List<KindergartenVM>> cb);

    //
    // Messages APIs
    //

    @GET("/get-all-conversations")
    public void getAllConversation(@Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/get-messages/{id}/{offset}")
    public void getMessages(@Path("id") Long id,@Path("offset") Long offset,@Query("key") String key, Callback<Response> cb);

    @GET("/start-conversation/{id}")
    public void startConversation(@Path("id") Long id,@Query("key") String key, Callback<List<ResponseConversationVM>> cb);

    @GET("/delete-conversation/{id}")
    public void deleteConversation(@Path("id") Long id,@Query("key") String key, Callback<Response> cb);

    @POST("/message/sendMsg")
    public void sendMessage(@Body MessagePostVM message,@Query("key") String key, Callback<Response> cb);

    @GET("/get-unread-msg-count")
    public void getUnreadMessageCount(@Query("key") String key, Callback<MessageVM> cb);

    @GET("/image/get-message-image-by-id/{id} ")
    public void getMessageImage(@Query("key") String key,@Part("messageId") long id, Callback<MessageVM> cb);

    @Multipart
    @POST("/image/sendMessagePhoto") //a function in your api upload image for message
    public void uploadMessagePhoto(@Query("key") String key,@Part("messageId") long id, @Part("send-photo0") TypedFile photo, Callback<Response> cb);

    //
    // Game APIs
    //

    @POST("/sign-in-for-today")
    public void signInForToday(@Query("key") String key, Callback<Response> cb);

    @GET("/get-gameaccount")
    public void getGameAccount(@Query("key") String key, Callback<GameAccountVM> cb);

    @GET("/get-game-transactions/{offset}")
    public void getGameTransactions(@Path("offset") Long offset, @Query("key") String key, Callback<List<GameTransactionVM>> cb);

    @GET("/get-latest-game-transactions")
    public void getLatestGameTransactions(@Query("key") String key, Callback<List<GameTransactionVM>> cb);

    @GET("/get-signup-referrals")
    public void getSignupReferrals(@Query("key") String key, Callback<List<UserVM>> cb);

    //
    // GCM key APIs
    //

    @POST("/saveGCMKey/{GCMkey}")
    public void saveGCMkey(@Path("GCMkey") String GCMkey,@Query("key") String key,Callback<Response> cb);


}


