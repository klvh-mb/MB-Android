package miniBean.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import miniBean.R;
import miniBean.activity.UserProfileActivity;
import miniBean.app.AppController;
import miniBean.app.UserInfoCache;
import miniBean.util.DateTimeUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.util.MessageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.CommunityPostCommentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailListAdapter extends BaseAdapter {
    private TextView ownerName, postBodyText, postTime;
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostCommentVM> postComments;
    private LinearLayout likeLayout;
    private ImageView userPic, like;
    private TextView deleteText, likeText, numLike, postIndex;
    private FrameLayout frameLayout;
    private Button messageButton;
    private LinearLayout postImagesLayout;
    private int page;

    public DetailListAdapter(Activity activity, List<CommunityPostCommentVM> postComments, int page) {
        this.activity = activity;
        this.postComments = postComments;
        this.page = page;
    }

    @Override
    public int getCount() {
        if (postComments == null)
            return 0;
        return postComments.size();
    }

    @Override
    public CommunityPostCommentVM getItem(int location) {
        if (postComments == null || location > postComments.size()-1)
            return null;
        return postComments.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public synchronized View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.detail_list_item, null);

        ownerName = (TextView) convertView.findViewById(R.id.postedBy);
        postTime = (TextView) convertView.findViewById(R.id.postedOn);
        postBodyText = (TextView) convertView.findViewById(R.id.postBodyText);
        userPic = (ImageView) convertView.findViewById(R.id.userPic);
        like = (ImageView) convertView.findViewById(R.id.likeImage);
        likeText = (TextView) convertView.findViewById(R.id.likeText);
        deleteText = (TextView) convertView.findViewById(R.id.deleteText);
        likeLayout = (LinearLayout) convertView.findViewById(R.id.likeComponent);
        numLike = (TextView) convertView.findViewById(R.id.numLike);
        postIndex = (TextView) convertView.findViewById(R.id.postIndex);
        frameLayout = (FrameLayout) convertView.findViewById(R.id.mainFrameLayout);
        messageButton = (Button) convertView.findViewById(R.id.messageButton);

        // images
        postImagesLayout = (LinearLayout) convertView.findViewById(R.id.postImages);

        final CommunityPostCommentVM item = postComments.get(position);

        // admin fields
        LinearLayout adminLayout = (LinearLayout) convertView.findViewById(R.id.adminLayout);
        if (AppController.isUserAdmin()) {
            ImageView androidIcon = (ImageView) convertView.findViewById(R.id.androidIcon);
            ImageView iosIcon = (ImageView) convertView.findViewById(R.id.iosIcon);
            ImageView mobileIcon = (ImageView) convertView.findViewById(R.id.mobileIcon);
            androidIcon.setVisibility(item.isAndroid()? View.VISIBLE : View.GONE);
            iosIcon.setVisibility(item.isIOS()? View.VISIBLE : View.GONE);
            mobileIcon.setVisibility(item.isMobile()? View.VISIBLE : View.GONE);

            adminLayout.setVisibility(View.VISIBLE);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        // like
        if (item.isLike()) {
            like.setImageResource(R.drawable.liked);
            likeText.setTextColor(activity.getResources().getColor(R.color.like_blue));
        } else {
            like.setImageResource(R.drawable.like);
            likeText.setTextColor(activity.getResources().getColor(R.color.gray));
        }
        if (item.getNol() >= 0) {
            numLike.setText(item.getNol()+"");
        }

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeText = (TextView) v.findViewById(R.id.likeText);
                like = (ImageView) v.findViewById(R.id.likeImage);
                numLike = (TextView) v.findViewById(R.id.numLike);

                if (item.isLike()) {
                    if (item.isPost()) {
                        unLikePost(item.getId());
                    } else {
                        unLikeComment(item.getId());
                    }
                    likeText.setTextColor(activity.getResources().getColor(R.color.gray));
                    like.setImageResource(R.drawable.like);
                    int total = item.getNol() - 1;
                    item.setNol(total);
                    numLike.setText(total + "");
                    item.setLike(false);
                } else {
                    if (item.isPost()) {
                        likePost(item.getId());
                    } else {
                        likeComment(item.getId());
                    }
                    likeText.setTextColor(activity.getResources().getColor(R.color.like_blue));
                    like.setImageResource(R.drawable.liked);
                    int total = item.getNol() + 1;
                    item.setNol(total);
                    numLike.setText(total + "");
                    item.setLike(true);
                }
            }
        });

        // PM
        Log.d(this.getClass().getSimpleName(), "oid="+item.getOid()+ " uid="+UserInfoCache.getUser().getId());
        if (item.getOid().equals(UserInfoCache.getUser().getId())) {
            messageButton.setVisibility(View.GONE);
        } else {
            messageButton.setVisibility(View.VISIBLE);
            messageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageUtil.openConversation(item.getOid(), activity);
                }
            });
        }

        // delete
        if (item.isO() || (AppController.isUserAdmin())) {
            if (item.isO()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.game_title_text));
            } else if (AppController.isUserAdmin()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.admin_green));
            }
            deleteText.setVisibility(View.VISIBLE);

            final int pos = position;
            deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(DetailListAdapter.this.activity.getString(R.string.post_delete_confirm));
                    alertDialogBuilder.setPositiveButton(DetailListAdapter.this.activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (item.isPost()) {
                                deletePost(item.getId());
                            } else {
                                deleteComment(item.getId(), pos);
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton(DetailListAdapter.this.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {
            deleteText.setVisibility(View.GONE);
        }

        // index
        if (item.isPost()) {
            postIndex.setVisibility(View.INVISIBLE);
        } else {
            postIndex.setVisibility(View.VISIBLE);
            if (page == 1) {
                postIndex.setText("#"+position);
            } else {
                // offset from previous page
                // position starts at 0, add 1
                position = ((page - 1) * DefaultValues.DEFAULT_PAGINATION_COUNT) + position + 1;
                postIndex.setText("#"+position);
            }
        }

        ViewUtil.setHtmlText(item.getD(), postBodyText, activity, true, true);

        ownerName.setText(item.getOn());
        ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserProfileActivity.class);
                i.putExtra("oid", item.getOid());
                i.putExtra("name", item.getOn());
                activity.startActivity(i);
            }
        });

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserProfileActivity.class);
                i.putExtra("oid", item.getOid());
                i.putExtra("name", item.getOn());
                activity.startActivity(i);
            }
        });

        postTime.setText(DateTimeUtil.getTimeAgo(item.getCd()));

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getOid(), userPic);

        // images
        // NOTE: need to load images from UIL cache each time as ListAdapter items are being recycled...
        //       without this item will not show images correctly
        if(item.hasImage) {
            Log.d(this.getClass().getSimpleName(), "getView: load "+item.getImgs().length+" images to post/comment #"+position+" - "+item.getD());
            loadImages(item, postImagesLayout);
            postImagesLayout.setVisibility(View.VISIBLE);

            /*if (!item.imageLoaded) {
                Log.d(this.getClass().getSimpleName(), "getView: load "+item.getImgs().length+" images to post/comment #"+position+" - "+item.getD());
                loadImages(item, postImagesLayout);
            } else {
                for(int i = 0; i < postImagesLayout.getChildCount(); i++) {
                    Log.d(this.getClass().getSimpleName(), "getView: resume image "+i+" visibility for post/comment #"+position+" - "+item.getD());
                    View childView = postImagesLayout.getChildAt(i);
                    childView.setVisibility(View.VISIBLE);
                }
            }
            postImagesLayout.setVisibility(View.VISIBLE);*/
        } else {
            postImagesLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void loadImages(final CommunityPostCommentVM item, final LinearLayout layout) {
        layout.removeAllViewsInLayout();

        Log.d(this.getClass().getSimpleName(), "loadImages: "+item.getImgs().length+" images");
        for (Long imageId : item.getImgs()) {
            ImageView postImage = new ImageView(this.activity);
            postImage.setAdjustViewBounds(true);
            postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImage.setPadding(0, 0, 0, ViewUtil.getRealDimension(10, this.activity.getResources()));
            layout.addView(postImage);

            /*
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewUtil.fullscreenImagePopup(DetailListAdapter.this, source);
                }
            });
            */

            // obsolete
            /*
            String source = activity.getResources().getString(R.string.base_url) + "/image/get-original-post-image-by-id/" + imageId;
            Log.d(this.getClass().getSimpleName(), "loadImages: source - "+source);
            new LoadPostImage().execute(source, postImage);
            */

            ImageUtil.displayOriginalPostImage(imageId, postImage, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ImageView imageView = (ImageView)view;
                    imageView.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null) {
                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();

                        // always stretch to screen width
                        int displayWidth = ViewUtil.getDisplayDimensions(DetailListAdapter.this.activity).width();
                        float scaleAspect = (float)displayWidth / (float)width;
                        width = displayWidth;
                        height = (int)(height * scaleAspect);

                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                        Drawable d = new BitmapDrawable(
                                DetailListAdapter.this.activity.getResources(),
                                Bitmap.createScaledBitmap(loadedImage, width, height, false));
                        ImageView imageView = (ImageView)view;
                        imageView.setImageDrawable(d);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        item.imageLoaded = true;
    }

    private void likeComment(Long id) {
        AppController.getApi().setLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailListAdapter.class.getSimpleName(), "likeComment: failure", error);
            }
        });
    }

    private void unLikeComment(Long id) {
        AppController.getApi().setUnLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailListAdapter.class.getSimpleName(), "unLikeComment: failure", error);
            }
        });
    }

    private void likePost(Long id) {
        AppController.getApi().setLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailListAdapter.class.getSimpleName(), "likePost: failure", error);
            }
        });
    }

    private void unLikePost(Long id) {
        AppController.getApi().setUnLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(DetailListAdapter.class.getSimpleName(), "unLikePost: failure", error);
            }
        });
    }

    private void deletePost(Long id) {
        AppController.getApi().deletePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();
                DetailListAdapter.this.activity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                Log.e(DetailListAdapter.class.getSimpleName(), "deletePost: failure", error);
            }
        });
    }

    private void deleteComment(Long id, final int position) {
        AppController.getApi().deleteComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                DetailListAdapter.this.postComments.remove(position);
                DetailListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.e(DetailListAdapter.class.getSimpleName(), "deleteComment: failure", error);
            }
        });
    }
}