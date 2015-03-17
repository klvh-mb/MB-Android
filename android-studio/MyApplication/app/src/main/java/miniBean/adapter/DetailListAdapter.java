package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.activity.ProfileActivity;
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
    private List<CommunityPostCommentVM> communityItems;
    private boolean likeFlag;
    private LinearLayout linearLayout;
    private ImageView like;
    private TextView likeText, totalLike, indexComment;
    private int page;

    private ActivityUtil activityUtil;

    private int emoticonWidth, emoticonHeight;

    public DetailListAdapter(Activity activity, List<CommunityPostCommentVM> communityItems, int page) {
        this.activity = activity;
        this.communityItems = communityItems;
        this.page = page;
        this.activityUtil = new ActivityUtil(activity);
        emoticonWidth = activityUtil.getRealDimension(EmoticonUtil.WIDTH);
        emoticonHeight = activityUtil.getRealDimension(EmoticonUtil.HEIGHT);
    }

    @Override
    public int getCount() {
        if (communityItems == null)
            return 0;
        return communityItems.size();
    }

    @Override
    public CommunityPostCommentVM getItem(int location) {
        return communityItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.detail_item, null);

        ownerName = (TextView) convertView.findViewById(R.id.postedBy);
        postTime = (TextView) convertView.findViewById(R.id.postedOn);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.questionnare_img);
        like = (ImageView) convertView.findViewById(R.id.likeImage);
        likeText = (TextView) convertView.findViewById(R.id.TextLike);
        linearLayout = (LinearLayout) convertView.findViewById(R.id.likeComponent);
        totalLike = (TextView) convertView.findViewById(R.id.TotalLike);
        indexComment = (TextView) convertView.findViewById(R.id.indexComment);

        final CommunityPostCommentVM item = communityItems.get(position);

        // like
        if (item.isLike()) {
            like.setImageResource(R.drawable.liked);
            likeText.setTextColor(activity.getResources().getColor(R.color.like_blue));
        } else {
            like.setImageResource(R.drawable.like);
            likeText.setTextColor(activity.getResources().getColor(R.color.gray));
        }
        if (item.getNol() >= 0) {
            totalLike.setText(item.getNol()+"");
        }

        // index
        if (item.isPost()) {
            indexComment.setVisibility(View.INVISIBLE);
        } else {
            indexComment.setVisibility(View.VISIBLE);
            if (page == 1) {
                indexComment.setText("#"+position);
            } else {
                // offset from previous page
                // position starts at 0, add 1
                position = ((page - 1) * DefaultValues.DEFAULT_PAGINATION_COUNT) + position + 1;
                indexComment.setText("#"+position);
            }
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeText = (TextView) v.findViewById(R.id.TextLike);
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
                    totalLike.setText(total+"");
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
                    totalLike.setText(total+"");
                    item.setLike(true);
                }
            }
        });

        //txtTest. setMovementMethod(LinkMovementMethod.getInstance(item.getD()));
        Spanned spanned = Html.fromHtml(item.getD(), this, null);
        commentText.setText(spanned);
        commentText.setMovementMethod(LinkMovementMethod.getInstance());

        ownerName.setText(item.getOn());

        ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ProfileActivity.class);
                i.putExtra("id", item.getOid().toString());
                i.putExtra("name", item.getOn());
                activity.startActivity(i);
            }
        });

        Date date = new Date(item.getCd());
        String DATE_FORMAT_NOW = "dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String stringDate = sdf.format(date);
        postTime.setText(stringDate);

        int rounded_value = 120;

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(rounded_value)).build();

        ImageLoader.getInstance().displayImage(activity.getResources().getString(R.string.base_url) + "/image/get-profile-image-by-id/" + item.getOid(), userPic, options);

        /* AppController.mImageLoader.displayImage(activity.getResources().getString(R.string.base_url) + "/image/get-mini-image-by-id/" + item.getOid(), userPic,new SimpleImageLoadingListener(){
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
        });
        */

        return convertView;
    }

    void likeComment(Long id) {
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

    void unLikeComment(Long id) {
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

    void likePost(Long id) {
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

    void unLikePost(Long id) {
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

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();

        int emoticon = EmoticonUtil.map(source);
        if (emoticon != -1) {
            Log.d("getDrawable", "replace source with emoticon");
            Drawable emo = activity.getResources().getDrawable(emoticon);
            d.addLevel(0, 0, emo);
            d.setBounds(0, 0, emoticonWidth, emoticonHeight);
        } else {
            Drawable empty = activity.getResources().getDrawable(R.drawable.empty);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
            new LoadImage().execute(source, d);
        }

        return d;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = activity.getResources().getString(R.string.base_url) + (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d("doInBackground", source);
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
                Log.d("LoadImage", bitmap.getWidth() + "|" + bitmap.getHeight());
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = commentText.getText();
                commentText.setText(t);
            }
        }
    }

}