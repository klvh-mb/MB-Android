package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import miniBean.R;
import miniBean.activity.ProfileActivity;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunityPostCommentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailListAdapter extends BaseAdapter {

    public SharedPreferences session = null;

    private TextView ownerName, commentText, postTime;
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunityPostCommentVM> communityItems;
    private boolean likeFlag;
    private LinearLayout linearLayout;
    private ImageView like;
    private TextView likeText, totalLike;

    public DetailListAdapter(Activity activity, List<CommunityPostCommentVM> communityItems) {
        this.activity = activity;
        this.communityItems = communityItems;
    }

    @Override
    public int getCount() {
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
        session = activity.getSharedPreferences("prefs", 0);
        ownerName = (TextView) convertView.findViewById(R.id.postedBy);
        postTime = (TextView) convertView.findViewById(R.id.postedOn);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.questionnare_img);
        like = (ImageView) convertView.findViewById(R.id.likeImage);
        likeText = (TextView) convertView.findViewById(R.id.TextLike);
        linearLayout = (LinearLayout) convertView.findViewById(R.id.likeComponent);
        totalLike = (TextView) convertView.findViewById(R.id.TotalLike);

        final CommunityPostCommentVM item = communityItems.get(position);

        if (!item.isLike()) {
            like.setImageResource(R.drawable.like);
            likeText.setText(activity.getString(R.string.like));
        } else {
            like.setImageResource(R.drawable.liked);
            likeText.setText(activity.getString(R.string.like));
        }
        if (item.getNol() >= 0) {
            totalLike.setText(item.getNol() + "");
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeText = (TextView) v.findViewById(R.id.TextLike);
                like = (ImageView) v.findViewById(R.id.likeImage);
                totalLike = (TextView) v.findViewById(R.id.TotalLike);
                if (item.isLike() == true) {
                    if (item.isPost()) {
                        unLikePost(item.getId());
                    } else {
                        unLikeComment(item.getId());
                    }
                    likeText.setText(activity.getString(R.string.like));
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
                    likeText.setText(activity.getString(R.string.like));
                    like.setImageResource(R.drawable.liked);
                    int total = item.getNol() + 1;
                    item.setNol(total);
                    totalLike.setText(total + "");
                    item.setLike(true);
                }
            }
        });

        //txtTest. setMovementMethod(LinkMovementMethod.getInstance(item.getD()));
        commentText.setText(item.getD());

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

        ImageLoader.getInstance().displayImage(activity.getResources().getString(R.string.base_url) + "/image/get-mini-image-by-id/" + item.getOid(), userPic, options);

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
        AppController.api.setLikeComment(id, session.getString("sessionID", null), new Callback<Response>() {

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
        AppController.api.setUnLikeComment(id, session.getString("sessionID", null), new Callback<Response>() {

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
        AppController.api.setLikePost(id, session.getString("sessionID", null), new Callback<Response>() {

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
        AppController.api.setUnLikePost(id, session.getString("sessionID", null), new Callback<Response>() {

            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}