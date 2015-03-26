package miniBean.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.UserProfileActivity;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.EmoticonUtil;
import miniBean.viewmodel.CommunityPostCommentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailListAdapter extends BaseAdapter implements Html.ImageGetter {
    private TextView ownerName, commentText, postTime;
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostCommentVM> postComments;
    private LinearLayout likeLayout;
    private ImageView like, image1, image2;
    private LinearLayout postImagesLayout;
    private List<ImageView> postImages;
    private TextView deleteText, likeText, totalLike, indexComment;
    private int page;
    private PopupWindow imagePopup;
    private ActivityUtil activityUtil;
    private FrameLayout frameLayout;
    private int emoticonWidth, emoticonHeight;

    public DetailListAdapter(Activity activity, List<CommunityPostCommentVM> postComments, int page) {
        this.activity = activity;
        this.postComments = postComments;
        this.page = page;
        this.activityUtil = new ActivityUtil(activity);
        emoticonWidth = activityUtil.getRealDimension(EmoticonUtil.WIDTH);
        emoticonHeight = activityUtil.getRealDimension(EmoticonUtil.HEIGHT);
    }

    @Override
    public int getCount() {
        if (postComments == null)
            return 0;
        return postComments.size();
    }

    @Override
    public CommunityPostCommentVM getItem(int location) {
        return postComments.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.detail_list_item, null);

        ownerName = (TextView) convertView.findViewById(R.id.postedBy);
        postTime = (TextView) convertView.findViewById(R.id.postedOn);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.questionnare_img);
        like = (ImageView) convertView.findViewById(R.id.likeImage);
        likeText = (TextView) convertView.findViewById(R.id.likeText);
        deleteText = (TextView) convertView.findViewById(R.id.deleteText);
        likeLayout = (LinearLayout) convertView.findViewById(R.id.likeComponent);
        totalLike = (TextView) convertView.findViewById(R.id.TotalLike);
        indexComment = (TextView) convertView.findViewById(R.id.indexComment);
        postImagesLayout = (LinearLayout) convertView.findViewById(R.id.postImages);
        postImages = new ArrayList<>();
        frameLayout= (FrameLayout) convertView.findViewById(R.id.mainFrameLayout);
       

        final CommunityPostCommentVM item = postComments.get(position);

        // like
        if (item.isLike()) {
            like.setImageResource(R.drawable.liked);
            likeText.setTextColor(activity.getResources().getColor(R.color.like_blue));
        } else {
            like.setImageResource(R.drawable.like);
            likeText.setTextColor(activity.getResources().getColor(R.color.gray));
        }
        if (item.getNol() >= 0) {
            totalLike.setText(item.getNol() + "");
        }

        // delete
        if (item.isO() || AppController.getUser().isAdmin()) {
            if (item.isO()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.like_blue));
            } else if (AppController.getUser().isAdmin()) {
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
            indexComment.setVisibility(View.INVISIBLE);
        } else {
            indexComment.setVisibility(View.VISIBLE);
            if (page == 1) {
                indexComment.setText("#" + position);
            } else {
                // offset from previous page
                // position starts at 0, add 1
                position = ((page - 1) * DefaultValues.DEFAULT_PAGINATION_COUNT) + position + 1;
                indexComment.setText("#" + position);
            }
        }

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeText = (TextView) v.findViewById(R.id.likeText);
                like = (ImageView) v.findViewById(R.id.likeImage);
                totalLike = (TextView) v.findViewById(R.id.TotalLike);

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
                    totalLike.setText(total + "");
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
                    totalLike.setText(total + "");
                    item.setLike(true);
                }
            }
        });

        Spanned spanned = activityUtil.getDisplayTextFromHtml(item.getD(), this);
        commentText.setText(spanned);
        commentText.setMovementMethod(LinkMovementMethod.getInstance());

        ownerName.setText(item.getOn());

        ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserProfileActivity.class);
                i.putExtra("oid", item.getOid());
                i.putExtra("name", item.getOn());
                System.out.println("owner" + item.getOid());
                activity.startActivity(i);
            }
        });

        postTime.setText(activityUtil.getTimeAgo(item.getCd()));

        // profile pic
        //Log.d(this.getClass().getSimpleName(), "getView: load user profile pic - "+item.getOn()+"|"+activity.getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + item.getOid());
        ImageLoader.getInstance().displayImage(
                activity.getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + item.getOid(),
                userPic,
                AppController.ROUND_IMAGE_OPTIONS);

        // images
        Log.d(this.getClass().getSimpleName(), "getView: post/comment hasImage - " + item.hasImage);
        if (item.hasImage) {
            if (!item.imageLoaded || postImagesLayout.getChildCount() == 0) {
                loadImages(item.getImgs());
                item.imageLoaded = true;
            } else {
                for (int i = 0; i < postImagesLayout.getChildCount(); ++i) {
                    View childView = postImagesLayout.getChildAt(i);
                    childView.setVisibility(View.VISIBLE);
                    Log.d(this.getClass().getSimpleName(), "getView: resume all post images view - " + i);

                }
            }
            postImagesLayout.setVisibility(View.VISIBLE);
        } else {
            postImagesLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void loadImages(Long[] imageIds) {
        for (Long imageId : imageIds) {
            final String source = activity.getResources().getString(R.string.base_url) + "/image/get-original-post-image-by-id/" + imageId;
            Log.d(this.getClass().getSimpleName(), "loadImages: source - "+source);

            ImageView postImage = new ImageView(this.activity);
            postImage.setAdjustViewBounds(true);
            postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImage.setPadding(0, 0, 0, activityUtil.getRealDimension(10));
            postImagesLayout.addView(postImage);

            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullscreenImagePopup(source);
                }
            });
            //new LoadPostImage().execute(source, postImage);   // obsolete
            AppController.getImageLoader().displayImage(source, postImage, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null) {
                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();

                        // always stretch to screen width
                        int displayWidth = activityUtil.getDisplayDimensions().width();
                        float scaleAspect = (float) displayWidth / (float) width;
                        width = displayWidth;
                        height = (int) (height * scaleAspect);

                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after shrink - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                        Drawable d = new BitmapDrawable(
                                DetailListAdapter.this.activity.getResources(),
                                Bitmap.createScaledBitmap(loadedImage, width, height, false));
                        ImageView imageView = (ImageView) view;
                        imageView.setImageDrawable(d);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }

            });
        }
    }

    private void likeComment(Long id) {
        AppController.api.setLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void unLikeComment(Long id) {
        AppController.api.setUnLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void likePost(Long id) {
        AppController.api.setLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void unLikePost(Long id) {
        AppController.api.setUnLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void deletePost(Long id) {
        AppController.api.deletePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();
                DetailListAdapter.this.activity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), DetailListAdapter.this.activity.getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void deleteComment(Long id, final int position) {
        AppController.api.deleteComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
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
            }
        });
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();

        int emoticon = EmoticonUtil.map(source);
        if (emoticon != -1) {
            //Log.d(this.getClass().getSimpleName(), "getDrawable: replace source with emoticon - " + source);
            Drawable emo = activity.getResources().getDrawable(emoticon);
            d.addLevel(0, 0, emo);
            d.setBounds(0, 0, emoticonWidth, emoticonHeight);
        } else {
            Log.d(this.getClass().getSimpleName(), "getDrawable: load emoticon from background - " + source);
            Drawable empty = activity.getResources().getDrawable(R.drawable.empty);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
            new LoadImageToBody().execute(source, d);
        }

        return d;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        protected ImageView imageView;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            if (!source.startsWith(activity.getResources().getString(R.string.base_url))) {
                source = activity.getResources().getString(R.string.base_url) + source;
            }

            imageView = (ImageView) params[1];
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());
                Drawable d = new BitmapDrawable(
                        DetailListAdapter.this.activity.getResources(),
                        Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
                imageView.setImageDrawable(d);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    // Obsolete... use universal ImageLoader
    class LoadPostImage extends LoadImage {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                // always stretch to screen width
                int displayWidth = activityUtil.getDisplayDimensions().width();
                float scaleAspect = (float) displayWidth / (float) width;
                width = displayWidth;
                height = (int) (height * scaleAspect);

                Log.d(this.getClass().getSimpleName(), "onPostExecute: after shrink - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                Drawable d = new BitmapDrawable(
                        DetailListAdapter.this.activity.getResources(),
                        Bitmap.createScaledBitmap(bitmap, width, height, false));
                imageView.setImageDrawable(d);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    class LoadImageToBody extends LoadImage {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: refresh body text");
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = commentText.getText();
                commentText.setText(t);
            }
        }
    }

    private void fullscreenImagePopup(String source) {
        try {
            frameLayout.getForeground().setAlpha(20);
            frameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.image_popup_window,(ViewGroup) activity.findViewById(R.id.popupElement));
            ImageView fullImage= (ImageView) layout.findViewById(R.id.fullImage);


            imagePopup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            imagePopup.setOutsideTouchable(false);
            imagePopup.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), ""));
            imagePopup.setFocusable(true);
            imagePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            AppController.getImageLoader().displayImage(source, fullImage, new SimpleImageLoadingListener(){

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}