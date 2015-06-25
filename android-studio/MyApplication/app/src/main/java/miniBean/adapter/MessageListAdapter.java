package miniBean.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyImageGetter;
import miniBean.app.UserInfoCache;
import miniBean.util.ActivityUtil;
import miniBean.util.DateTimeUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.MessageVM;

public class MessageListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageVM> messageVMs;
    private ImageView messageImages;
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

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

         final MessageVM m = messageVMs.get(position);

        Long userId1 = UserInfoCache.getUser().getId();
        Long userId2 = m.getSuid();

        // Identifying the message owner
        if (userId1.longValue() == userId2.longValue()) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_left, null);
            senderImage = (ImageView) convertView.findViewById(R.id.senderImage);
            ImageUtil.displayThumbnailProfileImage(m.getSuid(), senderImage);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView dateMsg = (TextView) convertView.findViewById(R.id.messageDate);
        ViewUtil.setHtmlText(m.getTxt(), imageGetter, txtMsg, true, true);

        dateMsg.setText(DateTimeUtil.getTimeAgo(m.getCd()));

        messageImages = (ImageView) convertView.findViewById(R.id.messageImages);
        if(m.isHasImage()) {
            if(m.getImgs()!=null) {
                loadImages(m, messageImages);
            }
            messageImages.setVisibility(View.VISIBLE);
        } else {
            messageImages.setVisibility(View.GONE);
        }

        messageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fullscreenImagePopup(AppController.BASE_URL + "/image/get-original-private-image-by-id/"+m.getImgs());
            }
        });

    return convertView;
    }
    private void loadImages(MessageVM item, final ImageView messageImage) {

        messageImage.setAdjustViewBounds(true);
        messageImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        messageImage.setPadding(0, 0, 0, ActivityUtil.getRealDimension(10, this.activity.getResources()));

        ImageUtil.displayOriginalMessageImage(item.getImgs(), messageImage, new SimpleImageLoadingListener() {
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

                    // always stretch to message width
                    int displayWidth = ActivityUtil.getDisplayDimensions(MessageListAdapter.this.activity).width();
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
    }

    private void fullscreenImagePopup(String source) {
        try {
            //frameLayout.getForeground().setAlpha(20);
            //frameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.image_popup_window,(ViewGroup) activity.findViewById(R.id.popupElement));
            ImageView fullImage= (ImageView) layout.findViewById(R.id.fullImage);

            PopupWindow imagePopup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            imagePopup.setOutsideTouchable(false);
            imagePopup.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), ""));
            imagePopup.setFocusable(true);
            imagePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ImageUtil.displayImage(source, fullImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
