package miniBean.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import miniBean.R;
import miniBean.app.MyImageGetter;
import miniBean.app.UserInfoCache;
import miniBean.util.ActivityUtil;
import miniBean.util.HtmlUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.MessageVM;

public class MessageListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageVM> messageVMs;
    private LinearLayout postImagesLayout;
    private ActivityUtil activityUtil;
    private ImageView senderImage;
    private MyImageGetter imageGetter;
    public MessageListAdapter(Activity activity, List<MessageVM> messageVMs) {
        this.activity = activity;
        this.messageVMs = messageVMs;
        this.activityUtil = new ActivityUtil(activity);
        this.imageGetter = new MyImageGetter(activity);
    }

    @Override
    public int getCount() {
        return messageVMs.size();
    }

    @Override
    public MessageVM getItem(int location) {
        return messageVMs.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater)activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int size=messageVMs.size();
        System.out.print("inside adapter:::"+size);

         MessageVM m = messageVMs.get(position);

        System.out.println("kendo::::"+UserInfoCache.getUser().getId());
        Long long1 = UserInfoCache.getUser().getId();
        Long long2 = m.getSuid();
        System.out.println("sender::::"+m.getSuid());
        // Identifying the message owner
        if (long1.longValue()== long2.longValue()) {
        // message belongs to you, so load the right aligned layout
            System.out.println("right::::::::::::");
        convertView = mInflater.inflate(R.layout.list_item_message_right,
                null);
    } else {
        // message belongs to other person, load the left aligned layout
        convertView = mInflater.inflate(R.layout.list_item_message_left,
                null);
    }



//    TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
    TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
    postImagesLayout = (LinearLayout) convertView.findViewById(R.id.messageImages);
    senderImage= (ImageView) convertView.findViewById(R.id.senderImage);

    txtMsg.setText(m.getTxt());
  //  lblFrom.setText(m.getSnm());

        System.out.println("suid::::"+m.getSuid());
        ImageUtil.displayThumbnailProfileImage(m.getSuid(),senderImage);

        HtmlUtil.setHtmlText(m.getTxt(),imageGetter,txtMsg);


        if(m.isHasImage()) {
            //Log.d(this.getClass().getSimpleName(), "getView: load " + m.getImgs().length+ " images to post/comment #" + position + " - ");

            if(m.getImgs()!=null) {
                loadImages(m, postImagesLayout);
            }
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
    private void loadImages(MessageVM item, final LinearLayout layout) {
        layout.removeAllViewsInLayout();

        System.out.println("loadimages:::"+item.getImgs());
      //  for (Long imageId : item.getImgs()) {
            ImageView postImage = new ImageView(this.activity);
            postImage.setAdjustViewBounds(true);
            postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImage.setPadding(0, 0, 0, activityUtil.getRealDimension(10));
            layout.addView(postImage);

            /*
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullscreenImagePopup(source);
                }
            });
            */

            // obsolete
            /*
            String source = activity.getResources().getString(R.string.base_url) + "/image/get-original-post-image-by-id/" + imageId;
            Log.d(this.getClass().getSimpleName(), "loadImages: source - "+source);
            new LoadPostImage().execute(source, postImage);
            */

            ImageUtil.displayMessageImage(item.getImgs(), postImage, new SimpleImageLoadingListener() {
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

                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                        Drawable d = new BitmapDrawable(
                                MessageListAdapter.this.activity.getResources(),
                                Bitmap.createScaledBitmap(loadedImage, width, height, false));
                        ImageView imageView = (ImageView) view;
                        imageView.setImageDrawable(d);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        //}

    }
}
